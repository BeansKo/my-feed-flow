package com.beans.my.feedflow.job.log4j;

import org.apache.log4j.Level;
import org.apache.log4j.net.SyslogAppender;

public class DetailLevel extends Level{

	private static final long serialVersionUID = -2679135460962791923L;

	public static final Level FEED_DETAIL = new DetailLevel(20050000, "FEED_DETAIL", SyslogAppender.LOG_LOCAL0);
	
	public DetailLevel(int level, String name, int sysLogLevel) {
		super(level, name, sysLogLevel);
	}

}
