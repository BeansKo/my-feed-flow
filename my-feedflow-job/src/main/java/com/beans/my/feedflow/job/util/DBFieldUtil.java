package com.beans.my.feedflow.job.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.exceptions.HBaseException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.beans.my.feedflow.base.model.BaseDomain;

public class DBFieldUtil {
	/**
	 * 将对象转换为Map
	 */
	public static Map<String, String> format(BaseDomain obj) throws IllegalArgumentException, IllegalAccessException, IOException, HBaseException {
		if(obj == null){ return null; }
		Class<? extends BaseDomain> clazz = obj.getClass();
		List<Field> fields = new ArrayList<>() ;
		Class<?> tempClass = clazz;
		while (tempClass != null) {
			fields.addAll(Arrays.asList(tempClass .getDeclaredFields()));
		    tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
		}
		Map<String, String> map = new HashMap<String, String>();
		for (Field field : fields) {
			boolean accessible = field.isAccessible();
			if(!accessible){field.setAccessible(true);}
			Object fieldData = field.get(obj);
			if(fieldData == null){
				map.put(field.getName(), null);
				continue;
			}
			Class<?> fieldClazz = field.getType();
			String data = null;
			if (isPrimitive(fieldClazz)){//类型不用转换
            	data = fieldData.toString();
            }else if(fieldClazz == java.util.Date.class){//时间类型
            	data = ((java.util.Date)fieldData).getTime() + "";
            }else {//其他类型
            	data = JSON.toJSONString(fieldData);
            }
            if(!accessible){field.setAccessible(false);}
            if(data == null){ throw new HBaseException("can not parse this field: " + field.getName() + " : " + fieldData); }
            map.put(field.getName(), data);
		}
		return map;
	}
	
	/**
	 * 将Map转换为对象
	 */
	public static <T extends BaseDomain> T parse(Class<T> clazz, Map<String,String> map) {
		if(map == null || map.isEmpty()){ return null; }
		try {
			T obj = clazz.newInstance();
			List<Field> fields = new ArrayList<>() ;
			Class<?> tempClass = clazz;
			while (tempClass != null) {
				fields.addAll(Arrays.asList(tempClass .getDeclaredFields()));
			    tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
			}
			for (Field field : fields) {
				if(Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())){
					continue;
				}
				String data = map.get(field.getName());
				if(data == null || "".equals(data)){ continue; }
				boolean accessible = field.isAccessible();
				if(!accessible){field.setAccessible(true);}
				Class<?> fieldClazz = field.getType();
				if (isPrimitive(fieldClazz)){//类型不用转换
					if(fieldClazz == boolean.class || fieldClazz == Boolean.class){
						field.set(obj, Boolean.valueOf(data));
					}else if(fieldClazz == short.class || fieldClazz == Short.class){
						field.set(obj, Short.valueOf(data));
					}else if(fieldClazz == int.class || fieldClazz == Integer.class){
						field.set(obj, Integer.valueOf(data));
					}else if(fieldClazz == long.class || fieldClazz == Long.class){
						field.set(obj, Long.valueOf(data));
					}else if(fieldClazz == double.class || fieldClazz == Double.class){
						field.set(obj, Double.valueOf(data));
					}else if(fieldClazz == float.class || fieldClazz == Float.class){
						field.set(obj, Float.valueOf(data));
					}else if(fieldClazz == BigInteger.class){
						field.set(obj, new BigInteger(data));
					}else if(fieldClazz == BigDecimal.class){
						field.set(obj, new BigDecimal(data));
					}else if(fieldClazz == byte.class || fieldClazz == Byte.class){
						field.set(obj, Byte.valueOf(data));
					}else if(fieldClazz == byte[].class || fieldClazz == Byte[].class){
						field.set(obj, data.getBytes());
					}else if(fieldClazz == char.class || fieldClazz == Character.class){
						field.set(obj, data.charAt(0));
					}else{
						field.set(obj, data);
					}
	            }else if(fieldClazz == java.util.Date.class){//时间类型
	            	long time = Long.valueOf(data);
	            	field.set(obj, new java.util.Date(time));
	            }else if(fieldClazz == java.sql.Date.class){
	            	long time = Long.valueOf(data);
	            	field.set(obj, new java.sql.Date(time));
	            }else if(fieldClazz.isAssignableFrom(List.class)){
	            	Type genericType = field.getGenericType();
	                if(genericType == null) continue;
	                if(genericType instanceof ParameterizedType){     
	                    ParameterizedType pt = (ParameterizedType) genericType;  
	                    Class<?> genericClazz = (Class<?>)pt.getActualTypeArguments()[0]; 
	                    List<?> list = JSONArray.parseArray(data, genericClazz);
	                    field.set(obj, list);
	                }else{
	                	throw new RuntimeException("can not parse data by field [" + field.getName() + "]");
	                }
	            }else{
	            	field.set(obj, JSON.parseObject(data, fieldClazz));
	            }
	            if(!accessible){field.setAccessible(false);}
			}
			return obj;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
    public static boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive() //
               || clazz == Boolean.class //
               || clazz == Character.class //
               || clazz == Byte.class //
               || clazz == Short.class //
               || clazz == Integer.class //
               || clazz == Long.class //
               || clazz == Float.class //
               || clazz == Double.class //
               || clazz == BigInteger.class //
               || clazz == BigDecimal.class //
               || clazz == String.class;
    }
}
