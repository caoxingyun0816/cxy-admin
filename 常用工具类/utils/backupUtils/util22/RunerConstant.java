/**
 * @copyright ：
 * @projectName：OMS服务监控系统
 * @company:网达信息
 */
package com.wondertek.mam.util.backupUtils.util22;

/**
 * @anther :叶来印
 * @开发时间： 2012-4-27 -- 上午9:34:49
 * @editUser ：
 * @修改时间：
 * @程序作用：
 * 1：
 * 2：
 */
public class RunerConstant {
	
	public final static long NO_DBSERVER_ID=0L;
	
	//cpu配置标示
	public final static int CPU_RUNNER_TYPEID =1;
	//获取CPU的命令
	public final static String CPUINFO_COMMOND = "ps -eo pcpu,pid | sort -k 1 -r";
	//获取内存的明亮
	public final static String MEMORY_COMMOND = "free -mo";
	//内存配置标识
	public final static int MEMORY_RUNNER_TYPEID=2;
	//获取硬盘数据信息命令
	public final static String HARDDISK_COMMOND = "df -m";
	//硬盘配置标识
	public final static int HARDDISK_RUNNER_TYPEID=3;
	//获取当前系统的执行的线程
	public final static String PROGRESS_COMMOND = "ps -aux";
	public final static int PROGRESS_RUNNER_TYPE =4;
	//获取当前登录用户的命令
	public final static String USERLOGIN_COMMOND ="who";
	
	
	//监控数据库服务器
	public final static int  DBSERVER_TABLE_ORACLE_TYPEID=11;
	
	public final static int DBSERVER_TABLESPACE_MYSQL_TYPEID =12;	
	public final static int DBSERVER_TABLESPACE_ORACLE_TYPEID =13;
	
	//监视web地址模块
	public final static  int WEB_URL_MOINT_TYPEID =20;
	
	
	public final static String DBSERVER_TYPE_ORACLE ="1";
	public final static String DBSERVER_TYPE_MYSQL ="2";
	public final static String DB_ORACLE_DRIVER_CLASS = "oracle.jdbc.OracleDriver";
	public final static String DB_MYSQL_DRIVER_CLASS = "com.mysql.jdbc.Driver";
	/**
	 * 获取所有的用户（需要用户权限为DBA）
	 */
	public final static String GET_ALL_USER_BY_DBA ="select t.username,t.user_id,t.account_status,t.default_tablespace from  dba_users t where t.account_status='OPEN' and username not in ('SYS','SYSTEM','SYSMAN','DBSNMP')";
	/**
	 * 获取某个用户下的所有表
	 */
	public final static String GET_ALL_TABLE_BY_USER = "select t.table_name from all_all_tables t where t.owner = ?";
	/**
	 * 统计某个用户的下的某个表的记录
	 */
	public final static String COUNT_TABLE_USER_TABLENAME = "select count(1) as total from ?.?";
	
	public final static String GET_ALL_TABLE_BY_NOMAL_USER = "select t.TABLE_NAME from tabs t";
	
	public final static String GETT_ALL_TABLESPACE_BY_DBA_USER = "SELECT UPPER(F.TABLESPACE_NAME) as  spacename, 　　D.TOT_GROOTTE_MB as  totalsize,D.TOT_GROOTTE_MB - F.TOTAL_BYTES as usersize,　TO_CHAR(ROUND((D.TOT_GROOTTE_MB - F.TOTAL_BYTES) / D.TOT_GROOTTE_MB * 100,2),'990.99') as  rate,　　F.TOTAL_BYTES as  freedpace,　F.MAX_BYTES as maxblock　FROM (SELECT TABLESPACE_NAME,　ROUND(SUM(BYTES) / (1024 * 1024), 2) TOTAL_BYTES,　　ROUND(MAX(BYTES) / (1024 * 1024), 2) MAX_BYTES　　FROM SYS.DBA_FREE_SPACE　　GROUP BY TABLESPACE_NAME) F,　　(SELECT DD.TABLESPACE_NAME,　　ROUND(SUM(DD.BYTES) / (1024 * 1024), 2) TOT_GROOTTE_MB　　FROM SYS.DBA_DATA_FILES DD　　GROUP BY DD.TABLESPACE_NAME) D　　WHERE D.TABLESPACE_NAME = F.TABLESPACE_NAME　　ORDER BY 1";
	
	
	public final static String COUNT_SESSION_INFO_BY_DBA_USER = "select count(*) as total,t.USERNAME from v$session t group by t.USERNAME";
	
}
