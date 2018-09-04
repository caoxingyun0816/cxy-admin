package com.wondertek.mam.util.backupUtils.util22;

import java.util.Date;

import org.apache.log4j.Logger;

import com.wondertek.mobilevideo.core.util.DateUtil;

public class LogUtil {
	public static Logger log = Logger.getLogger(LogUtil.class);
	
	/**
	 * 创建log日志(初始化时以时间开头)
	 * @param path	日志文件全路径
	 * @param fields	日志参数
	 */
	public static void log(Logger logger, String[] fields){
		StringBuffer buffer=new StringBuffer();
		buffer.append(DateUtil.getDateTime("yyyyMMddHHmmss", new Date()));

		for(String field:fields){
			if(field==null|| field.trim().length()==0)field="";
			field = field.replace("|", "_");
			buffer.append("|").append(field);
		}

		logger.debug(buffer.toString());
	}
	
	/**
	 * 创建log日志(初始化时不以时间开头)
	 * @param logger		日志文件全路径
	 * @param fields		日志参数
	 */
	public static void logNoDate(Logger logger, String[] fields){
		StringBuffer buffer=null;
		for(String field:fields){
			if(field==null|| field.trim().length()==0)field="";
			field = field.replace("|", "_");
			if(buffer==null)
				buffer=new StringBuffer(field);
			else
				buffer.append("|").append(field);
		}
		logger.debug(buffer.toString());
	}
	
	
	/**
	 * 日志上传(FTP)
	 * @param localPath	本地路径
	 * @param date	路径上的时间参数
	 * @param remotePath	远程路径
	 * @param ftpServers	ftp服务器
	 */
/*	public static void ftpUpload(String localPath, String date, String remotePath, List<FtpServer> ftpServers){
		try {
			String localPathWithDate = localPath.replace("{date}", date);
			remotePath = remotePath.replace("{date}", date);
			int pos = remotePath.lastIndexOf("/");
			remotePath = remotePath.substring(0, pos) + FtpExcConfLoader.ipToDir() + remotePath.substring(pos);
			
			//如果找不到带日期的文件，为没有带日期的文件重命名
			File localFileWithDate = new File(localPathWithDate);
			if(!localFileWithDate.exists()){
				FileUtil.createNewFile(localFileWithDate);
				String tmpPath = localPath.replace(".{date}", "");
				File tmpFile = new File(tmpPath);
				if(tmpFile.exists()){
					tmpFile.renameTo(localFileWithDate);
				}
			}
			localPath = localPathWithDate;
			
    		//发布到FTP上
    		for(FtpServer ftpServer : ftpServers){
    			FtpTemplate.upLoadOrDelFile(ftpServer, localPath, new String[] {
    					null, remotePath }, true);
    		}
        	log.debug("[success] System publish Exchange log to ftp: " + ftpServers);
		} catch (Exception e) {
			log.error(e,e);
		}
	}*/
	
	/**
	 * 日志上传(NAS)
	 * @param localPath
	 * @param date
	 * @param nasPath
	 */
/*	public static void nasUpload(String localPath, String date, String nasPath){
		try {
			String localPathWithDate = localPath.replace("{date}", date).replace("{month}", date.substring(0,6));
			nasPath = nasPath.replace("{date}", date).replace("{month}", date.substring(0,6))
				.replace("{ip}", FtpExcConfLoader.ipToDir());
			
			
			//如果找不到带日期的文件，为没有带日期的文件重命名
			File localFileWithDate = new File(localPathWithDate);
			if(!localFileWithDate.exists()){
				FileUtil.createNewFile(localFileWithDate);
				String tmpPath = localPath.replace(".{date}", "").replace("{month}", date.substring(0,6));
				File tmpFile = new File(tmpPath);
				if(tmpFile.exists()){
					boolean renameTo = tmpFile.renameTo(localFileWithDate);
				}
			}
			localPath = localPathWithDate;
			
    		//发布到NAS上
			FileIO.fileCopy(localPath, nasPath);
        	log.debug("[success] System publish Exchange log to nas: " + nasPath);
		} catch (Exception e) {
			log.error(e,e);
		}
	}*/

}
