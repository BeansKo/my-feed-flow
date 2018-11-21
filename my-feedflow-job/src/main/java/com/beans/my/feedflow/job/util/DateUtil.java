package com.beans.my.feedflow.job.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class DateUtil {
	private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = new ThreadLocal<SimpleDateFormat>();  
	private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT_ISO8601 = new ThreadLocal<SimpleDateFormat>();  
    
    /** 
     * 获取SimpleDateFormat 
     * @param pattern 日期格式 
     * @return SimpleDateFormat对象 
     * @throws RuntimeException 异常：非法日期格式 
     */  
    public synchronized static SimpleDateFormat getDateFormat(String pattern) throws RuntimeException {  
        SimpleDateFormat dateFormat = DATE_FORMAT.get();  
        if (dateFormat == null) {  
            if (dateFormat == null) {  
                dateFormat = new SimpleDateFormat(pattern);  
                dateFormat.setLenient(false);  
                DATE_FORMAT.set(dateFormat);  
            }
        }  
        dateFormat.applyPattern(pattern);  
        return dateFormat;  
    }  
  
    /** 
     * 获取ISO8601标准的SimpleDateFormat 
     * @param pattern 日期格式 
     * @return SimpleDateFormat对象 
     * @throws RuntimeException 异常：非法日期格式 
     */  
    public synchronized static SimpleDateFormat getDateFormatISO8601(String pattern) throws RuntimeException {  
        SimpleDateFormat dateFormat = DATE_FORMAT_ISO8601.get();  
        if (dateFormat == null) {  
            if (dateFormat == null) {  
                dateFormat = new SimpleDateFormat(pattern);  
                dateFormat.setLenient(false);
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                DATE_FORMAT_ISO8601.set(dateFormat);  
            }
        }  
        dateFormat.applyPattern(pattern);  
        return dateFormat;  
    }  
  
    /** 
     * 获取精确的日期 
     * @param timestamps 时间long集合 
     * @return 日期 
     */  
    public static Date getAccurateDate(ArrayList<Long> timestamps) {  
        Date date = null;  
        long timestamp = 0;  
        HashMap<Long, long[]> map = new HashMap<Long, long[]>();  
        ArrayList<Long> absoluteValues = new ArrayList<Long>();  
  
        if (timestamps != null && timestamps.size() > 0) {  
            if (timestamps.size() > 1) {  
                for (int i = 0; i < timestamps.size(); i++) {  
                    for (int j = i + 1; j < timestamps.size(); j++) {  
                        long absoluteValue = Math.abs(timestamps.get(i) - timestamps.get(j));  
                        absoluteValues.add(absoluteValue);  
                        long[] timestampTmp = { timestamps.get(i), timestamps.get(j) };  
                        map.put(absoluteValue, timestampTmp);  
                    }  
                }  
  
                // 有可能有相等的情况。如2012-11和2012-11-01。时间戳是相等的。此时minAbsoluteValue为0  
                // 因此不能将minAbsoluteValue取默认值0  
                long minAbsoluteValue = -1;  
                if (!absoluteValues.isEmpty()) {  
                    minAbsoluteValue = absoluteValues.get(0);  
                    for (int i = 1; i < absoluteValues.size(); i++) {  
                        if (minAbsoluteValue > absoluteValues.get(i)) {  
                            minAbsoluteValue = absoluteValues.get(i);  
                        }  
                    }  
                }  
  
                if (minAbsoluteValue != -1) {  
                    long[] timestampsLastTmp = map.get(minAbsoluteValue);  
  
                    long dateOne = timestampsLastTmp[0];  
                    long dateTwo = timestampsLastTmp[1];  
                    if (absoluteValues.size() > 1) {  
                        timestamp = Math.abs(dateOne) > Math.abs(dateTwo) ? dateOne : dateTwo;  
                    }  
                }  
            } else {  
                timestamp = timestamps.get(0);  
            }  
        }  
  
        if (timestamp != 0) {  
            date = new Date(timestamp);  
        }  
        return date;  
    }  
  
    /** 
     * 判断字符串是否为日期字符串 
     * @param date 日期字符串 
     * @return true or false 
     */  
    public static boolean isDate(String date) {  
        boolean isDate = false;  
        if (date != null) {  
            if (getDateStyle(date) != null) {  
                isDate = true;  
            }  
        }  
        return isDate;  
    }  
  
    /** 
     * 获取日期字符串的日期风格。失敗返回null。 
     * @param date 日期字符串 
     * @return 日期风格 
     */  
    public static DateStyle getDateStyle(String date) {  
        DateStyle dateStyle = null;  
        HashMap<Long, DateStyle> map = new HashMap<Long, DateStyle>();  
        ArrayList<Long> timestamps = new ArrayList<Long>();  
        for (DateStyle style : DateStyle.values()) {  
            if (style.isShowOnly()) {  
                continue;  
            }  
            Date dateTmp = null;  
            if (date != null) {  
                try {  
                    ParsePosition pos = new ParsePosition(0);  
                    dateTmp = getDateFormat(style.getValue()).parse(date, pos);  
                    if (pos.getIndex() != date.length()) {  
                        dateTmp = null;  
                    }  
                } catch (Exception e) {  
                }  
            }  
            if (dateTmp != null) {  
                timestamps.add(dateTmp.getTime());  
                map.put(dateTmp.getTime(), style);  
            }  
        }  
        Date accurateDate = getAccurateDate(timestamps);  
        if (accurateDate != null) {  
            dateStyle = map.get(accurateDate.getTime());  
        }  
        return dateStyle;  
    }  
  
    /** 
     * 将日期字符串转化为日期。失败返回null。 
     * @param date 日期字符串 
     * @return 日期 
     */  
    public static Date StringToDate(String date) {
    	if(date == null || "".equals(date)){ return null;}
        DateStyle dateStyle = getDateStyle(date);  
        return StringToDate(date, dateStyle);  
    }
  
    /** 
     * 将日期字符串转化为时间戳。失败返回null。 
     * @param date 日期字符串 
     * @return 日期 
     */  
    public static long getTime(String datetime, TimeZone zone) {
    	if(datetime == null || "".equals(datetime)){ return timeZoneOfset(System.currentTimeMillis(), zone, TimeZone.getDefault());}
        DateStyle dateStyle = getDateStyle(datetime);
        Date date = StringToDate(datetime, dateStyle);
        if(date == null){ return timeZoneOfset(System.currentTimeMillis(), zone, TimeZone.getDefault()); }
        return timeZoneOfset(date.getTime(), zone, TimeZone.getDefault());
    }
    /** 
     * 将日期字符串转化为日期。失败返回null。 
     * @param date 日期字符串 
     * @param pattern 日期格式 
     * @return 日期 
     */  
    public static Date StringToDate(String date, String pattern) {  
        Date myDate = null;  
        if (date != null) {  
            try {  
                myDate = getDateFormat(pattern).parse(date);  
            } catch (Exception e) {  
            }  
        }  
        return myDate;  
    }
  
    /** 
     * 将日期字符串转化为日期。失败返回null。 
     * @param date 日期字符串 
     * @param dateStyle 日期风格 
     * @return 日期 
     */  
    public static Date StringToDate(String date, DateStyle dateStyle) {  
        Date myDate = null;  
        if (dateStyle != null) {  
            myDate = StringToDate(date, dateStyle.getValue());  
        }  
        return myDate;  
    }

    /** 
     * 将日期转化为日期字符串。失败返回null。 
     * @param date 日期 
     * @param pattern 日期格式 
     * @return 日期字符串 
     */  
    public static String DateToString(Date date, String pattern) {  
        String dateString = null;  
        if (date != null) {  
            try {  
                dateString = getDateFormat(pattern).format(date);  
            } catch (Exception e) {  
            }  
        }  
        return dateString;  
    }  
    
    /** 
     * 将日期转化为ISO8601标准的日期字符串。失败返回null。 
     * @param date 日期 
     * @param pattern 日期格式 
     * @return 日期字符串 
     */  
    public static String DateToStringISO8601(Date date, String pattern) {  
    	String dateString = null;  
    	if (date != null) {  
    		try {  
    			dateString = getDateFormatISO8601(pattern).format(date);  
    		} catch (Exception e) {  
    		}  
    	}  
    	return dateString;  
    }  
  
    /** 
     * 将日期转化为日期字符串。失败返回null。 
     * @param date 日期 
     * @param dateStyle 日期风格 
     * @return 日期字符串 
     */  
    public static String DateToString(Date date, DateStyle dateStyle) {  
        String dateString = null;  
        if (dateStyle != null) {  
            dateString = DateToString(date, dateStyle.getValue());  
        }  
        return dateString;  
    }  
  
    /** 
     * 将日期字符串转化为另一日期字符串。失败返回null。 
     * @param date 旧日期字符串 
     * @param newPattern 新日期格式 
     * @return 新日期字符串 
     */  
    public static String StringToString(String date, String newPattern) {
    	if(date == null || "".equals(date)){ return null;}
        DateStyle oldDateStyle = getDateStyle(date);  
        return StringToString(date, oldDateStyle, newPattern);  
    }  
  
    /** 
     * 将日期字符串转化为另一日期字符串。失败返回null。 
     * @param date 旧日期字符串 
     * @param newDateStyle 新日期风格 
     * @return 新日期字符串 
     */  
    public static String StringToString(String date, DateStyle newDateStyle) {  
        DateStyle oldDateStyle = getDateStyle(date);  
        return StringToString(date, oldDateStyle, newDateStyle);  
    }  
  
    /** 
     * 将日期字符串转化为另一日期字符串。失败返回null。 
     * @param date 旧日期字符串 
     * @param olddPattern 旧日期格式 
     * @param newPattern 新日期格式 
     * @return 新日期字符串 
     */  
    public static String StringToString(String date, String olddPattern, String newPattern) {  
        return DateToString(StringToDate(date, olddPattern), newPattern);  
    }  
  
    /** 
     * 将日期字符串转化为另一日期字符串。失败返回null。 
     * @param date 旧日期字符串 
     * @param olddDteStyle 旧日期风格 
     * @param newParttern 新日期格式 
     * @return 新日期字符串 
     */  
    public static String StringToString(String date, DateStyle olddDteStyle, String newParttern) {  
        String dateString = null;  
        if (olddDteStyle != null) {  
            dateString = StringToString(date, olddDteStyle.getValue(), newParttern);  
        }  
        return dateString;  
    }  
  
    /** 
     * 将日期字符串转化为另一日期字符串。失败返回null。 
     * @param date 旧日期字符串 
     * @param olddPattern 旧日期格式 
     * @param newDateStyle 新日期风格 
     * @return 新日期字符串 
     */  
    public static String StringToString(String date, String olddPattern, DateStyle newDateStyle) {  
        String dateString = null;  
        if (newDateStyle != null) {  
            dateString = StringToString(date, olddPattern, newDateStyle.getValue());  
        }  
        return dateString;  
    }  
  
    /** 
     * 将日期字符串转化为另一日期字符串。失败返回null。 
     * @param date 旧日期字符串 
     * @param olddDteStyle 旧日期风格 
     * @param newDateStyle 新日期风格 
     * @return 新日期字符串 
     */  
    public static String StringToString(String date, DateStyle olddDteStyle, DateStyle newDateStyle) {  
        String dateString = null;  
        if (olddDteStyle != null && newDateStyle != null) {  
            dateString = StringToString(date, olddDteStyle.getValue(), newDateStyle.getValue());  
        }  
        return dateString;  
    }  
  
    /** 
     * 获取日期 。默认yyyy-MM-dd格式。失败返回null。 
     * @param date 日期字符串 
     * @return 日期 
     */  
    public static String getDate(String date) {  
        return StringToString(date, DateStyle.YYYY_MM_DD);  
    }  
  
    /** 
     * 获取日期。默认yyyy-MM-dd格式。失败返回null。 
     * @param date 日期 
     * @return 日期 
     */  
    public static String getDate(Date date) {  
        return DateToString(date, DateStyle.YYYY_MM_DD);  
    }  
  
    /** 
     * 获取日期的时间。默认HH:mm:ss格式。失败返回null。 
     * @param date 日期字符串 
     * @return 时间 
     */  
    public static String getTime(String date) {  
        return StringToString(date, DateStyle.HH_MM_SS);  
    }  
  
    /** 
     * 获取日期的时间。默认HH:mm:ss格式。失败返回null。 
     * @param date 日期 
     * @return 时间 
     */  
    public static String getTime(Date date) {  
        return DateToString(date, DateStyle.HH_MM_SS);  
    }  
  
    /** 
     * 获取日期的时间。默认yyyy-MM-dd HH:mm格式。失败返回null。 
     * @param date 日期 
     * @return 时间 
     */  
    public static String getDateTime_ForMM(Date date) {  
        return DateToString(date, DateStyle.YYYY_MM_DD_HH_MM);  
    }  
    
    /** 
     * 获取日期的时间。默认yyyy-MM-dd HH:mm:ss格式。失败返回null。 
     * @param date 日期 
     * @return 时间 
     */  
    public static String getDateTime(Date date) {  
        return DateToString(date, DateStyle.YYYY_MM_DD_HH_MM_SS);  
    }
    
    /** 
     * 获取两个日期相差的天数 
     * @param date 日期字符串 
     * @param otherDate 另一个日期字符串 
     * @return 相差天数。如果失败则返回-1 
     */  
    public static int getIntervalDays(String date, String otherDate) {  
        return getIntervalDays(StringToDate(date), StringToDate(otherDate));  
    }  
  
    /** 
     * @param date 日期 
     * @param otherDate 另一个日期 
     * @return 相差天数。如果失败则返回-1 
     */  
    public static int getIntervalDays(Date date, Date otherDate) {  
        int num = -1;  
        Date dateTmp = DateUtil.StringToDate(DateUtil.getDate(date), DateStyle.YYYY_MM_DD);  
        Date otherDateTmp = DateUtil.StringToDate(DateUtil.getDate(otherDate), DateStyle.YYYY_MM_DD);  
        if (dateTmp != null && otherDateTmp != null) {  
            long time = Math.abs(dateTmp.getTime() - otherDateTmp.getTime());  
            num = (int) (time / (24 * 60 * 60 * 1000));  
        }  
        return num;  
    }
    
	/**
	 * <p>@Title: 获取当前指定时区的时间<p>
	 * @param targetZone 目标时区
	 */
	public static long timeZoneOfset(long time, TimeZone targetZone){
		return timeZoneOfset(time, TimeZone.getDefault(), targetZone);
	}
	/**
	 * <p>@Title: 获取当前指定时区的时间<p>
	 * @param targetZone 目标时区
	 */
	public static long timeZoneOfset(TimeZone targetZone){
		return timeZoneOfset(System.currentTimeMillis(), TimeZone.getDefault(), targetZone);
	}
	
	/**
	 * <p>@Title: 获取当前指定时区的时间,指定当前机器所在时区<p>
	 * @param sourceZone 源时区 (服务器所在的时区)
	 * @param targetZone 目标时区
	 */
	public static long timeZoneOfset(TimeZone sourceZone,TimeZone targetZone){
		return timeZoneOfset(System.currentTimeMillis(), sourceZone, targetZone);
	}
	
	/**
	 * <p>@Title: 获取当前指定时区的时间,指定当前机器所在时区<p>
	 * @param time 源时区的时间
	 * @param sourceZone 源时区 (服务器所在的时区)
	 * @param targetZone 目标时区
	 */
	public static long timeZoneOfset(long timestrap, TimeZone sourceZone, TimeZone targetZone){
		if(targetZone.inDaylightTime(new Date(timestrap))){
			return timestrap - (sourceZone.getRawOffset() - (targetZone.getRawOffset() + targetZone.getDSTSavings()));
		}else{
			return timestrap - (sourceZone.getRawOffset() - targetZone.getRawOffset());
		}
	}
	
	public static TimeZone getTimeZone(String zone) {
		if(zone.startsWith("+") || zone.startsWith("-")){
			zone = "GMT" + zone;
		}else if(!zone.startsWith("+") && !zone.startsWith("-") && isInteger(zone)){
			zone = "GMT+" + zone;
		}else if(zone.indexOf(".") != -1){
			zone = zone.substring(0, zone.indexOf("."));
		}else if(!isInteger(zone)){
			for (String id : TimeZone.getAvailableIDs()) {
				if(zone.toLowerCase().equals(id.toLowerCase())){
					return TimeZone.getTimeZone(id);
				}
			}
		}
		return TimeZone.getTimeZone(zone);
	}
	
	 public static boolean isInteger(String str) {  
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");  
        return pattern.matcher(str).matches();  
	 }
    
    public enum DateStyle {  
        
        YYYY_MM("yyyy-MM", false),  
        YYYY_MM_DD("yyyy-MM-dd", false),  
        YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm", false),  
        YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss", false),  
          
        YYYY_MM_EN("yyyy/MM", false),  
        YYYY_MM_DD_EN("yyyy/MM/dd", false),  
        YYYY_MM_DD_HH_MM_EN("yyyy/MM/dd HH:mm", false),  
        YYYY_MM_DD_HH_MM_SS_EN("yyyy/MM/dd HH:mm:ss", false),  
          
        YYYY_MM_CN("yyyy年MM月", false),  
        YYYY_MM_DD_CN("yyyy年MM月dd日", false),  
        YYYY_MM_DD_HH_MM_CN("yyyy年MM月dd日 HH:mm", false),  
        YYYY_MM_DD_HH_MM_SS_CN("yyyy年MM月dd日 HH:mm:ss", false),  
          
        HH_MM("HH:mm", true),  
        HH_MM_SS("HH:mm:ss", true),  
          
        MM_DD("MM-dd", true),  
        MM_DD_HH_MM("MM-dd HH:mm", true),  
        MM_DD_HH_MM_SS("MM-dd HH:mm:ss", true),  
          
        MM_DD_EN("MM/dd", true),  
        MM_DD_HH_MM_EN("MM/dd HH:mm", true),  
        MM_DD_HH_MM_SS_EN("MM/dd HH:mm:ss", true),  
          
        MM_DD_CN("MM月dd日", true),  
        MM_DD_HH_MM_CN("MM月dd日 HH:mm", true),  
        MM_DD_HH_MM_SS_CN("MM月dd日 HH:mm:ss", true);  
          
        private String value;  
          
        private boolean isShowOnly;  
          
        DateStyle(String value, boolean isShowOnly) {  
            this.value = value;  
            this.isShowOnly = isShowOnly;  
        }  
          
        public String getValue() {  
            return value;  
        }  
          
        public boolean isShowOnly() {  
            return isShowOnly;  
        }  
    }
    enum Week {  
    	  
        MONDAY("星期一", "Monday", "Mon.", 1),  
        TUESDAY("星期二", "Tuesday", "Tues.", 2),  
        WEDNESDAY("星期三", "Wednesday", "Wed.", 3),  
        THURSDAY("星期四", "Thursday", "Thur.", 4),  
        FRIDAY("星期五", "Friday", "Fri.", 5),  
        SATURDAY("星期六", "Saturday", "Sat.", 6),  
        SUNDAY("星期日", "Sunday", "Sun.", 7);  
          
        String name_cn;  
        String name_en;  
        String name_enShort;  
        int number;  
          
        Week(String name_cn, String name_en, String name_enShort, int number) {  
            this.name_cn = name_cn;  
            this.name_en = name_en;  
            this.name_enShort = name_enShort;  
            this.number = number;  
        }  
          
        public String getChineseName() {  
            return name_cn;  
        }  
      
        public String getName() {  
            return name_en;  
        }  
      
        public String getShortName() {  
            return name_enShort;  
        }  
      
        public int getNumber() {  
            return number;  
        }  
    }  
}