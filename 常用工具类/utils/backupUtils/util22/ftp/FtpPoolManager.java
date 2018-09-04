package com.wondertek.mam.util.backupUtils.util22.ftp;

import com.wondertek.mobilevideo.core.util.CmsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FtpPoolManager {
	protected final Log log = LogFactory.getLog(FtpPoolManager.class);
	static private ConcurrentHashMap<String, FtpConnectionPool> pools = new ConcurrentHashMap<String, FtpConnectionPool>();
	public static FtpConnection getConnection(FtpServer ftpServer) throws CmsException {
		FtpConnectionPool pool = getFtpConnectionPool(ftpServer);
		FtpConnection ftpConnection = null;
		try {
			ftpConnection = pool.getConnection();
		} catch (IOException e) {
			e.printStackTrace();
			throw new CmsException(CmsException.FTP_CONNECT_ERROR);
		}
		return ftpConnection;
	}
	
	private static synchronized FtpConnectionPool getFtpConnectionPool(FtpServer ftpServer) throws CmsException {
		FtpConnectionPool pool = null;
		String key = getFtpConnectionPoolKey(ftpServer);
		pool = pools.get(key);
		
		if (pool == null) {
			if (ftpServer.getIp() != null 
					&& ftpServer.getFtpuser() != null
					&& ftpServer.getFtppasswd() != null) {
				pool = new FtpConnectionPool();
				pool.setHost(ftpServer.getIp());
				if (ftpServer.getPort() != null)
					pool.setPort(ftpServer.getPort().intValue());
				pool.setUser(ftpServer.getFtpuser());
				pool.setPassword(ftpServer.getFtppasswd());
				pool.setFtpMode(ftpServer.getFtpMode());
				
				pools.put(key, pool);
			} else {
				throw new CmsException(ftpServer.getIp() + ":"	+ ftpServer.getPort()+" : "+ftpServer.getFtpurl() + "setting is error!");
			}
		}
		return pool;
	}
	
	public static void freeConnection(FtpServer pubServer, FtpConnection con) {
		FtpConnectionPool pool = pools.get(getFtpConnectionPoolKey(pubServer));
		if(pool != null)
			pool.freeConnection(con);
	}
	
	public static void freeAllConnection() {
		for (Iterator<Map.Entry<String, FtpConnectionPool>> it = pools.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, FtpConnectionPool> entry = it.next();
			FtpConnectionPool pool = entry.getValue();
			pool.freeAllConnection();
			pools.remove(entry.getKey());
		}
	}
	
	public static String getFtpConnectionPoolKey(FtpServer ftpServer){
		if(ftpServer == null){
			return null;
		}else if(ftpServer.getFtpMode() == FtpServer.FTP_MODE_SFTP){
			return "sftp_"+ ftpServer.getIp()+"_"+ftpServer.getFtpuser()+"_"+ftpServer.getFtpMark();
		}else if(ftpServer.getFtpMode() == FtpServer.FTP_MODE_FTP){
			return "ftp_"+ ftpServer.getIp()+"_"+ftpServer.getFtpuser()+"_"+ftpServer.getFtpMark();
		}else{
			return null;
		}
	}
	/**
	 * 为了不重启服务器，删除pool配置
	 * @param pubServer
	 * @return
	 */
	public static boolean removeFtpConnectionPool(FtpServer pubServer){
		String key = getFtpConnectionPoolKey(pubServer);
		FtpConnectionPool pool = pools.get(key);
		if(pool != null){
			pools.remove(key);
			pool.removeAllList();
		}
		return true;
	}
	
}
