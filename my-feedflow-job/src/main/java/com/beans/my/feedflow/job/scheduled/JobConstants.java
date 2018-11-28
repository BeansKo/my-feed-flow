package com.beans.my.feedflow.job.scheduled;

public class JobConstants {
	/** 默认文件编码 */
	public static final String DEFAULT_ENCODE = "UTF-8";
	
	/** AutoJob获取数据库配置的SQL语句模板 */
	public static final String AUTO_JOB_SQL_TEMPLATE = "DECLARE @WebSiteID integer = <WebSiteID>\n" + 
			"DECLARE @Group varchar(10)\n" + 
			"DECLARE @Titles varchar(8000)\n" + 
			"DECLARE @AvailabilityFields varchar(8000)\n" + 
			"\n" + 
			"SELECT @Group= Value FROM mis.dbo.DF_TalendJobConfig WITH(NOLOCK)\n" + 
			"WHERE  WebSiteID=0 AND ActiveMark=1 AND GroupName='Normal' AND [Key]='DefaultGroup'\n" + 
			"\n" + 
			"SELECT @Titles = COALESCE(@Titles + ',', '') + FieldName FROM mis.[dbo].[DF_FeedFields] WITH (NOLOCK)\n" + 
			"WHERE WebSiteID = @WebSiteID ORDER BY OrderID ASC\n" + 
			"\n" + 
			"SELECT @AvailabilityFields = COALESCE(@AvailabilityFields + ',', '') + A.FieldName FROM mis.dbo.DF_FeedFields A WITH (NOLOCK)\n" + 
			"INNER JOIN mis.dbo.DF_Intermediate_Feed_Mapping B WITH (NOLOCK) ON a.ID =b.FeedFieldID\n" + 
			"INNER JOIN mis.dbo.DF_IntermediateFields C WITH (NOLOCK) ON c.ID =b.IntermediateFieldID\n" + 
			"WHERE C.FieldName='Availability' AND A.WebsiteID= @WebSiteID\n" + 
			"\n" + 
			"SET  @Group= ISNULL( @Group,'E11')\n" + 
			"SET  @Titles = ISNULL( @Titles, '')\n" + 
			"SET  @AvailabilityFields = ISNULL( @AvailabilityFields, '')\n" + 
			"\n" + 
			"SELECT \n" + 
			"    [Key] AS Name\n" + 
			"    ,VALUE AS Value\n" + 
			"FROM mis.dbo.DF_TalendJobConfig A WITH(NOLOCK) \n" + 
			"WHERE \n" + 
			"    WebSiteID IN (0,@WebSiteID)\n" + 
			"	AND ActiveMark = 1\n" + 
			"    AND GroupName IN ('normal',@Group)\n" + 
			" 	AND NOT EXISTS (\n" + 
			"SELECT 1 FROM mis.dbo.DF_TalendJobConfig B WITH(NOLOCK) \n" + 
			"WHERE WebSiteID IN (0,@WebSiteID) AND ActiveMark = 1 AND A.[Key]=B.[Key] AND A.WebSiteID< B.WebSiteID AND GroupName IN ('normal',@Group))\n" + 
			"UNION\n" + 
			"SELECT 'Titles' AS Name, @Titles AS Value\n" + 
			"UNION\n" + 
			"SELECT 'WebSiteID' AS Name, CONVERT(varchar(10), @WebSiteID) AS Value\n" + 
			"UNION\n" + 
			"SELECT 'FormatType' AS Name, ISNULL(FormatType,'') AS Value FROM mis.dbo.DF_Website WITH (NOLOCK) WHERE WebSiteID = @WebSiteID\n" + 
			"UNION\n" + 
			"SELECT 'FileName' AS Name, ISNULL(FeedName,'') AS Value FROM mis.dbo.DF_Website WITH (NOLOCK) WHERE WebSiteID = @WebSiteID\n" + 
			"UNION\n" + 
			"SELECT 'IsSplitFeed' AS Name, CONVERT(varchar(10), IsSplitFeed) AS Value FROM mis.dbo.DF_Website WITH (NOLOCK) WHERE WebSiteID = @WebSiteID\n" + 
			"UNION\n" + 
			"SELECT 'FeedSize' AS Name, CONVERT(varchar(10), FeedSize) AS Value FROM mis.dbo.DF_Website WITH (NOLOCK) WHERE WebSiteID = @WebSiteID\n" + 
			"UNION\n" + 
			"SELECT 'IsUseTimestamp' AS Name, CONVERT(varchar(10), IsUseTimestamp) AS Value FROM mis.dbo.DF_Website WITH (NOLOCK) WHERE WebSiteID = @WebSiteID\n" + 
			"UNION\n" + 
			"SELECT 'AvailabilityFields' AS Name, @AvailabilityFields AS Value\n";
	
	public static class ConfigName{
		/** 本地配置 */
		public static final String BASE_CONFIG_PATH = "baseConfigPath";
		
		/** 数据目录 */
		public static final String BASE_DATA_PATH = "baseDataPath";
		
		/** ConvertFileEncode: 文件编码 */
		public static final String COMMON_FILE_ENCODE = "commonFileEncode";
		
		/** ReadConfigDatabase: AutoJob的配置数据库 */
		public static final String AUTO_DATABASE = "autoDatabase";
		/** ReadConfigFile: AutoJob本地配置文件 */
		public static final String AUTO_CONFIG_FILE = "autoConfigFile";
		/** ReadConfigDatabase: AutoJob的查询配置SQL */
		public static final String AUTO_SQL = "autoSql";	
	}
}
