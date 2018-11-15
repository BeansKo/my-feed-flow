package com.beans.my.feedflow.job.scheduled;

import org.apache.log4j.Logger;

import com.beans.my.feedflow.base.model.Version;

/**
 * Job执行过程中事件通知及日志记录, 实例化后之后必须先调用init()方法初始化运行版本并获取日志对象
 * @author fl76
 *
 */
public interface JobNotice {
	/**
	 * 初始化本次Job运行的version和logger对象
	 * @return 用于记录job运行过程的logger对象
	 */
	Logger init();
	/**
	 * Job启动
	 * @param startTime 开始时间
	 */
	void onStart(long startTime);
	/**
	 * 生成文件完成
	 * @param recordNumber 生成文件记录数
	 * @param fileSize 生成文件大小
	 */
	void onFileComplete(long recordNumber,long fileSize);
	/**
	 * Job执行成功
	 */
	void onSuccess();
	/**
	 * Job执行失败
	 */
	void onFail();
	/**
	 * Job执行结束
	 * @param endTime 结束时间
	 */
	void onEnd(long endTime);
	/**
	 * 获取当前Job执行的日志
	 * @return 当前执行日志
	 */
	Version currentVersion();
	/**
	 * 获取上次Job执行的日志
	 * @return 上次执行日志
	 */
	Version lastVersion();
}
