package com.wondertek.mam.util.backupUtils.util22.ftp;

//用于存储FTPSERVER信息，本意是不入库的，可以放到缓存。
public class FtpServer {
	public static int FTP_MODE_FTP=0;
	public static int FTP_MODE_SFTP=1;
	public static String FTP_MARK_DEFAULT="mark";
	
	private String ftpurl;
	private String ftpuser;
	private String ftppasswd;
	private String ip;
	private Long port;
	private int ftpMode = FTP_MODE_FTP;				//FTP模式：0_ftp,1_sftp
	private String ftpMark = FTP_MARK_DEFAULT;		//同一个FTP账号用于两处的时候不被干扰

	public Long getPort() {
		return port;
	}

	public void setPort(Long port) {
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getFtpurl() {
		return ftpurl;
	}

	public void setFtpurl(String ftpurl) {
		this.ftpurl = ftpurl;
	}

	public String getFtpuser() {
		return ftpuser;
	}

	public void setFtpuser(String ftpuser) {
		this.ftpuser = ftpuser;
	}

	public String getFtppasswd() {
		return ftppasswd;
	}

	public void setFtppasswd(String ftppasswd) {
		this.ftppasswd = ftppasswd;
	}

	public int getFtpMode() {
		return ftpMode;
	}

	public void setFtpMode(int ftpMode) {
		this.ftpMode = ftpMode;
	}

	public String getFtpMark() {
		return ftpMark;
	}

	public void setFtpMark(String ftpMark) {
		this.ftpMark = ftpMark;
	}
}
