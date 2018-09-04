package com.wondertek.mam.util.backupUtils.util22.ftp;

import com.wondertek.mobilevideo.core.util.CmsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;


public class FtpConnectionPool {
	protected final Log log = LogFactory.getLog(FtpConnectionPool.class);
	private CopyOnWriteArrayList<FtpConnection> freeCons = new CopyOnWriteArrayList<FtpConnection>();
	private CopyOnWriteArrayList<FtpConnection> usingCons = new CopyOnWriteArrayList<FtpConnection>();

	private String host = "localhost";// FTP ip
	private int port = 21;// FTP port
	private String user = "anonymous";// FTP帐号
	private String password = "";// FTP密码
	private int ftpMode = FtpServer.FTP_MODE_FTP;

	public void freeAllConnection() {
		while (freeCons.size() > 0) {
			closeConnection(freeCons.get(0));
			freeCons.remove(0);
		}
		while (usingCons.size() > 0) {
			closeConnection(usingCons.get(0));
			usingCons.remove(0);
		}
	}
	
	public void removeAllList() {
		freeCons.removeAll(freeCons);
		usingCons.removeAll(usingCons);
	}
	
	public int usingCount(){
		return usingCons.size();
	}

	public int freeCount() {
		return freeCons.size();
	}

	public FtpConnection getConnection() throws CmsException,IOException {
		FtpConnection con = null;
		log.debug("before free using Conns size is :"+usingCons.size()+"\t free Conns size is :"+ freeCons.size());
		while (freeCons.size() > 0) {
			con = freeCons.remove(0);
			try {
				if (con.getWorkingDirectory() != null)
					break;
				else
					closeConnection(con);
			} catch (Exception e) {
				closeConnection(con);
			}
			con = null;
			continue;
		}
		
		if (con == null && usingCons.size() < FtpConnection.FTP_MAX_CONNECTION) {
			con = openConnection();
		}
		if (con != null) {
			usingCons.add(con);
		}

		log.debug("FtpConnectionPool: ......host: " + host + " port: " + port
				+ "\tusingCons :" + usingCons.size()
				+ "\tfreeCons : " + freeCons.size());
		return con;
	}

	public synchronized void freeConnection(FtpConnection con) {
		log.debug("before free using Conns size is :"+usingCons.size()+"\t free Conns size is :"+ freeCons.size());
		usingCons.remove(con);
		if (freeCons.size() < FtpConnection.FTP_IDLE_CONNECTION && con != null) {
			freeCons.add(con);
		} else {
			closeConnection(con);
		} 
		log.debug("after free using Conns size is :"+usingCons.size()+"\t free Conns size is :"+ freeCons.size());
		
	}

	private FtpConnection openConnection() throws CmsException, IOException {
		log.debug("openConnection  start:-----------------------------" + host);
		FtpConnection con = new FtpConnection();
		boolean result = false;
		if(this.ftpMode == FtpServer.FTP_MODE_SFTP){
			result = con.connectServer(host, port, user, password,this.ftpMode);
		}else if(this.ftpMode == FtpServer.FTP_MODE_FTP){
			con.connectServer(host, port, user, password,this.ftpMode);
			log.debug("openConnection  connectServer----" + host);
			result = con.loginServer(user, password);
			log.debug("openConnection  loginServer:-----" + host+",result:"+result);
		}else{
			throw new CmsException("ERROR FTP MODE SETTING");
		}
		
		if (result) {
			con.setDefaultHomePath();
			log.debug("openConnection  end:-----------------------------" + host);
			return con;
		} else {
			closeConnection(con);
			throw new CmsException(CmsException.FTP_LOGIN_ERROR);
		}
	}

	private void closeConnection(FtpConnection con) {
		try {
			con.close();
			con = null;
		} catch (Exception e) {
			log.error(e.getMessage());
			// 关闭出错的异常自己处理，不再抛出去，简化处理
			// throw new CmsException(e.getMessage());
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getFtpMode() {
		return ftpMode;
	}

	public void setFtpMode(int ftpMode) {
		this.ftpMode = ftpMode;
	}
}
