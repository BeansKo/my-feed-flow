package com.beans.my.feedflow.job.log4j;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public class DetailFilter extends Filter{

	@Override
	public int decide(LoggingEvent event) {
		return event.getLevel() == DetailLevel.FEED_DETAIL ? -1 : 0;
	}
	
}
