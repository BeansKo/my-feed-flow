package com.beans.my.feedflow.job.dao;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.exceptions.HBaseException;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import com.beans.my.feedflow.base.model.BaseDomain;
import com.beans.my.feedflow.job.util.DBFieldUtil;

public class HBaseDao implements Closeable{
	static Log LOGGER = LogFactory.getLog(HBaseDao.class);
	
	static Integer hbase_zookeeper_pool = 50;
	final String table;
	final String family;
	protected Connection connection = null;

	static{
		/**
		 * 此代码为了防止 windows下 不设置 hadoop.home.dir 会报错
		 * 但设不设值都不影响程序执行
		 */
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Windows")) {
			try {
				String hadoop = HBaseDao.class.getResource("/hadoop").getPath();
				if(hadoop == null || hadoop.indexOf("!") != -1){
					String userdir = System.getProperty("user.dir");
					hadoop = "/" + userdir +"/config/hadoop";
				}
				hadoop = java.net.URLDecoder.decode(hadoop,"utf-8");
				System.setProperty("hadoop.home.dir",hadoop);
			} catch (Exception e) {
				LOGGER.error("set hadoopHome error",e);
			}
		}
	}
	
	public HBaseDao(String zookeeper, String table, String family) throws HBaseException{
		this(zookeeper, 2181, table, family);
	}
	
	public HBaseDao(String zookeeper, int port, String table, String family) throws HBaseException{
		try {
			this.table = table;
			this.family = family;
			Configuration HBASE_CONFIG = HBaseConfiguration.create();
			HBASE_CONFIG.set("hbase.zookeeper.quorum",zookeeper);
		    HBASE_CONFIG.set("hbase.zookeeper.property.clientPort", port + "");
		    HBASE_CONFIG.setInt("hbase.htable.threads.max",hbase_zookeeper_pool);
		    this.connection = ConnectionFactory.createConnection(HBASE_CONFIG);
			LOGGER.info("HBase connection success");
		} catch (Exception e) {
			throw new HBaseException("connect error", e);
		}
	}
	
	public Table getTable() throws HBaseException{
		try {
			return connection.getTable(TableName.valueOf(table));
		} catch (Exception e) {
			throw new HBaseException("HBase getTable exception", e);
		}
	}
	
	public boolean check() throws HBaseException, IOException {
		Table table = getTable();
		try {
			table.exists(new Get(Bytes.toBytes("NULL")));
			return true;
		}finally{
			if(table != null){ closeTable(table); }
		}
	}
	
	public boolean ping() {
		Table table = null;
		try {
			table = getTable();
			return table.exists(new Get(Bytes.toBytes("NULL")));
		} catch (Exception e) {
			return false;
		}finally{
			if(table != null){ closeTable(table); }
		}
	}
	
	/**
	 * 是否存在
	 */
	public boolean exist(String key) throws HBaseException {
		Table table = getTable();
		try {
			Get get = new Get(Bytes.toBytes(key));
			return table.exists(get);
		} catch (Exception e) {
			throw new HBaseException("exist error:" + key ,e);
		}finally{
			closeTable(table);
		}
	}
	
	private Put initPut(String key, BaseDomain obj) throws IllegalArgumentException, IllegalAccessException, IOException, HBaseException{
		Map<String, String> map = DBFieldUtil.format(obj);
		Put put = new Put(Bytes.toBytes(key));
		for (String name : map.keySet()) {
			String value = map.get(name);
			if(value != null){
				put.addColumn(Bytes.toBytes(family),  Bytes.toBytes(name), Bytes.toBytes(value));
			}else{
				put.addColumn(Bytes.toBytes(family),  Bytes.toBytes(name), null);
			}
		}
		return put;
	}
	
	public void put(String key, BaseDomain obj) throws HBaseException{
		Table table = getTable();
		try {
			Put put = initPut(key, obj);
			table.put(put);
		} catch (Exception e) {
			throw new HBaseException("put data error:" + key,e);
		}finally{
			closeTable(table);
		}
	}
	
	/**
	 * 查询数据
	 */
	public Map<String, String> get(String key) throws HBaseException{
		if(key == null){return null;}
		Table table = getTable();
		try {
			Get get = new Get(Bytes.toBytes(key));
			get.addFamily(Bytes.toBytes(family));
			Result result = table.get(get);
			if(result.isEmpty()){ return null; }
			Map<String,String> returns = new HashMap<String,String>();
			Iterator<Cell> iterator = result.listCells().iterator();
			while(iterator.hasNext()){
				Cell cell = iterator.next();
				String name = Bytes.toString(CellUtil.cloneQualifier(cell));
				String value = Bytes.toString(CellUtil.cloneValue(cell));
				returns.put(name, value);
			}
			return returns;
		} catch (Exception e) {
			throw new HBaseException("get data error:" + key ,e);
		}finally{
			closeTable(table);
		}
	}
	
	/**
	 * 查询数据
	 */
	public <T extends BaseDomain> T get(String key, Class<T> clazz) throws HBaseException{
		if(key == null){return null;}
		Table table = getTable();
		try {
			Get get = new Get(Bytes.toBytes(key));
			get.addFamily(Bytes.toBytes(family));
			Result result = table.get(get);
			if(result.isEmpty()){ return null; }
			Map<String,String> returns = new HashMap<String,String>();
			Iterator<Cell> iterator = result.listCells().iterator();
			while(iterator.hasNext()){
				Cell cell = iterator.next();
				String name = Bytes.toString(CellUtil.cloneQualifier(cell));
				String value = Bytes.toString(CellUtil.cloneValue(cell));
				returns.put(name, value);
			}
			return DBFieldUtil.parse(clazz, returns);
		} catch (Exception e) {
			throw new HBaseException("get data error:" + key ,e);
		}finally{
			closeTable(table);
		}
	}
	
	/**
	 * 查询数据
	 */
	public <T extends BaseDomain> List<T> getAll(List<String> keys, Class<T> clazz) throws HBaseException{
		if(keys == null){return null;}
		Table table = getTable();
		try {
			List<T> list = new ArrayList<T>();
			for (String key : keys) {
				if(key == null){ continue; }
				Get get = new Get(Bytes.toBytes(key));
				get.addFamily(Bytes.toBytes(family));
				Result result = table.get(get);
				if(result.isEmpty()){ continue; }
				Map<String,String> returns = new HashMap<String,String>();
				Iterator<Cell> iterator = result.listCells().iterator();
				while(iterator.hasNext()){
					Cell cell = iterator.next();
					String name = Bytes.toString(CellUtil.cloneQualifier(cell));
					String value = Bytes.toString(CellUtil.cloneValue(cell));
					returns.put(name, value);
				}
				T t = DBFieldUtil.parse(clazz, returns);
				list.add(t);
			}
			return list;
		} catch (Exception e) {
			throw new HBaseException("get data error:" + keys ,e);
		}finally{
			closeTable(table);
		}
	}
	
	public <T extends BaseDomain> List<T> listByType(final String type, Class<T> clazz) throws HBaseException {
		return this.listByType(null, null, type, clazz);
	}
	
	public <T extends BaseDomain> List<T> listByType(String rowKeyPrefix, final String type, Class<T> clazz) throws HBaseException {
		return listByType(rowKeyPrefix, null, type, clazz);
	}
	
	public <T extends BaseDomain> List<T> listByType(String rowKeyPrefix, String rowKeyRegEx, final String type, Class<T> clazz) throws HBaseException {
		List<T> list = new ArrayList<T>();
		this.listByType(rowKeyPrefix, rowKeyRegEx, type, result->{
			Map<String,String> returns = new HashMap<String,String>();
			Iterator<Cell> iterator = result.listCells().iterator();
			while(iterator.hasNext()){
				Cell cell = iterator.next();
				String name = Bytes.toString(CellUtil.cloneQualifier(cell));
				String value = Bytes.toString(CellUtil.cloneValue(cell));
				returns.put(name, value);
			}
			if(returns.containsKey("class_type") && type.equals(returns.get("class_type"))){
				list.add(DBFieldUtil.parse(clazz, returns));
			}
		});
		return list;
	}
	
	public <T extends BaseDomain> void listByType(String type, Class<T> clazz, Consumer<T> consumer) throws HBaseException {
		this.listByType(null, null, type, result->{
			Map<String,String> returns = new HashMap<String,String>();
			Iterator<Cell> iterator = result.listCells().iterator();
			while(iterator.hasNext()){
				Cell cell = iterator.next();
				String name = Bytes.toString(CellUtil.cloneQualifier(cell));
				String value = Bytes.toString(CellUtil.cloneValue(cell));
				returns.put(name, value);
			}
			if(returns.containsKey("class_type") && type.equals(returns.get("class_type"))){
				T t = DBFieldUtil.parse(clazz, returns);
				consumer.accept(t);
			}
		});
	}
	
	public void listByType(String type, Consumer<Result> consumer) throws HBaseException {
		this.listByType(null, null, type, consumer);
	}

	public void listByType(String rowKeyPrefix, String type, Consumer<Result> consumer) throws HBaseException {
		this.listByType(rowKeyPrefix, null, type, consumer);
	}

	public void listByType(String rowKeyPrefix, String rowKeyRegEx, String type, Consumer<Result> consumer) throws HBaseException {
		Table table = getTable();
		try {
			Scan scan = new Scan();
			FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
			if(type != null && !"".equals(type)){
				SingleColumnValueFilter scvf = new SingleColumnValueFilter(  
				        Bytes.toBytes(family),   
				        Bytes.toBytes("class_type"),   
				        CompareOp.EQUAL,
				        Bytes.toBytes(type));
				scvf.setFilterIfMissing(true);  
				scvf.setLatestVersionOnly(true);
				filterList.addFilter(scvf);
			}
			if(rowKeyPrefix != null && !"".equals(rowKeyPrefix)) {
				filterList.addFilter(new PrefixFilter(Bytes.toBytes(rowKeyPrefix)));
			}
			if(rowKeyRegEx != null && !"".equals(rowKeyRegEx)) {
				RegexStringComparator keyRegEx = new RegexStringComparator(rowKeyRegEx);
				filterList.addFilter(new RowFilter(CompareOp.EQUAL, keyRegEx));
			}
			scan.setFilter(filterList);
			scan.setMaxResultSize(Long.MAX_VALUE);
			scan.addFamily(Bytes.toBytes(family));
			scan.setCaching(10000);
			ResultScanner scanner = table.getScanner(scan);
			scanner.forEach(consumer);
		} catch (Exception e) {
			throw new HBaseException("list data error:" + type ,e);
		}finally{
			closeTable(table);
		}
	}
	
	/**
	 * 删除数据
	 */
	public void delete(String key) throws HBaseException {
		Table table = getTable();
		try {
			table.delete(new Delete(Bytes.toBytes(key)));
		} catch (Exception e) {
			throw new HBaseException("delete error:" + key ,e);
		}finally{
			closeTable(table);
		}
	}
	
	/**
	 * 删除数据
	 */
	public void delete(Set<String> keys) throws HBaseException {
		Table table = getTable();
		try {
			List<Delete> deletes = new ArrayList<Delete>();
			keys.forEach(k->{
				deletes.add(new Delete(Bytes.toBytes(k)));
			});
			table.delete(deletes);
		} catch (Exception e) {
			throw new HBaseException("delete error:" + keys ,e);
		}finally{
			closeTable(table);
		}
	}
	
	/**
	 * 根据类型全部删除数据
	 * @return 
	 */
	public List<String> keys(String type) throws HBaseException {
		List<String> results = new ArrayList<String>();
		this.listByType(type, result->{
			results.add(Bytes.toString(result.getRow()));
		});
		return results;
	}
	
	public void truncateDB()throws Exception{
		Table table = getTable();
		try {
			ResultScanner resultScanner = table.getScanner(new Scan());
			List<Delete> deletes = new ArrayList<Delete>();
			resultScanner.forEach(scan->{
				deletes.add(new Delete(scan.getRow()));
			});
			table.delete(deletes);
		} catch (Exception e) {
			throw new HBaseException("truncateDB error" ,e);
		}finally{
			closeTable(table);
		}
	}
	
	/**
	 * 关闭到表的连接
	 */
	protected void closeTable(Table table){
		try {
			if(table != null){ table.close(); }
		} catch (Exception e) {
			LOGGER.error("HBase close exception", e);
		}
	}
	
	@Override
	public void close() throws IOException {
		try {
			if(connection != null){
				connection.close();
				LOGGER.info("Shutdown HBase Connection");
			}
		} catch (Exception e) {
			LOGGER.error("HBase close exception", e);
		}
	}
}
