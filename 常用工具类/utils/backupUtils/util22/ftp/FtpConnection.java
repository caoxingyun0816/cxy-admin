package com.wondertek.mam.util.backupUtils.util22.ftp;

import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.wondertek.mam.util.others.CmsUtil;
import com.wondertek.mobilevideo.core.util.FilePathHelper;
import com.wondertek.mobilevideo.core.util.FileUtil;
import com.wondertek.mobilevideo.core.util.StringUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FtpConnection {
	// FTP 默认超时时间(毫秒)
	public static final int FTP_DEFAULT_TIMEOUT = 50000;
	// FTP 连接等待超时时间(毫秒)
	public static final int FTP_CONNECT_TIMEOUT = 20000;
	// FTP 接受数据超时(毫秒)
	public static final int FTP_DATA_TIMEOUT = 50000;
	// FTP 连接重试延时(毫秒)
	public static final Long FTP_TRY_DELAY = 30000L;
	// FTP 连接重试次数
	public static final int FTP_RETRY_TIMES = 5;
	
	public static int FTP_MAX_CONNECTION = 300;
	public static int FTP_IDLE_CONNECTION = 100;

	private Logger log = Logger.getLogger(FtpConnection.class);
	private FTPClient ftpClient = new FTPClient();
	private String ftpHomePath = "";// 记下ftp默认根目录

	private JSch jsch = null;
	private Session sshSession = null;
	private ChannelSftp sftp = null;
	private Boolean localPassiveMode = Boolean.FALSE;
	
	private int ftpMode = FtpServer.FTP_MODE_FTP;

	public void setDefaultHomePath() {
		try {
			ftpHomePath = getWorkingDirectory();
		} catch (Exception e) {
			log.error("ftpConnect error!", e);
		}
	}

	public String getDefaultHomePath() {
		return ftpHomePath;
	}
	/**
	 * 连接ftp服务器
	 * @param server
	 * @param port
	 * @param user
	 * @param password
	 * @param ftpMode
	 * @return
	 */
	public boolean connectServer(String server, int port, String user, String password,int ftpMode) {
		try{
			this.setFtpMode(ftpMode);
			if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_SFTP){
				jsch = new JSch();
				sshSession = jsch.getSession(user, server, port);
				sshSession.setTimeout(FTP_CONNECT_TIMEOUT);
				log.debug("Session created.");
				sshSession.setPassword(password);
				Properties sshConfig = new Properties();
				sshConfig.put("StrictHostKeyChecking", "no");
				sshSession.setConfig(sshConfig);
				sshSession.connect();
				log.debug("Session connected.");
				Channel channel = sshSession.openChannel("sftp");
				channel.connect();
				sftp = (ChannelSftp) channel;
				log.debug("Connected to " + server + "...");
			}else if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_FTP){
				ftpClient.setConnectTimeout(FTP_CONNECT_TIMEOUT);
				ftpClient.setDefaultTimeout(FTP_DEFAULT_TIMEOUT);
				ftpClient.setDataTimeout(FTP_DATA_TIMEOUT);
				ftpClient.connect(server, port);
			}else{
				return false;
			}
			return true;
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return false;
	}
	/**
	 * 登陆
	 * @param user
	 * @param password
	 * @return
	 * @throws IOException
	 */
	public boolean loginServer(String user, String password) throws IOException {
		if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_FTP){
			return ftpClient.login(user, password);
		}else{
			return false;
		}
	}
	/**
	 * 关闭
	 * @throws IOException
	 */
	public void close() throws IOException {
		try {
			//sftp.disconnect();
			//sftp.exit();
			if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_SFTP){
				sshSession.disconnect();
			}else if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_FTP){
				ftpClient.sendCommand("bye");
			}
		} catch (Exception e) {
		}
	}
	/**
	 * 进去指定目录(或多层目录)
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public boolean setWorkingDirectory(String path) throws IOException {
		try{
			if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_SFTP){
				sftp.cd(path);
				return true;
			}else if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_FTP){
				return ftpClient.changeWorkingDirectory(path);
			}else{
				return false;
			}
		}catch (Exception e) {
			return false;
		}
	}
	/**
	 * 获取当前工作目录
	 * @return
	 * @throws IOException
	 */
	public String getWorkingDirectory() throws IOException{
		try {
			if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_SFTP){
				return sftp.pwd();
			}else if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_FTP){
				return ftpClient.printWorkingDirectory();
			}else{
				return "";
			}
		} catch (SftpException e) {
			throw new IOException(e.getMessage());
		}
	}

	public boolean makeDirectory(String dirName) throws IOException ,CmsException{
		if (setWorkingDirectory(dirName)) {
			return true;
		} else {
			try{
				if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_SFTP){
					sftp.mkdir(dirName);
					return setWorkingDirectory(dirName);
				}else if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_FTP){
					if(ftpClient.makeDirectory(dirName)&& setWorkingDirectory(dirName)){
						return true;
					}else{
						throw new CmsException(CmsException.FTP_NO_PERMISSION,"no permission");
					}
				}else{
					return false;
				}
			}catch (Exception e) {
				log.error(e.getMessage(),e);
				return false;
			}
		}
	}
	/**
	 * 列出ftp中的文件名(SFTP包括文件夹)
	 * @param ftpPath
	 * 	若以"/"开头则是登陆以后开始的ftpPath目录下的文件名，与当前目录无关，
	 * 	若不以"/"开头，则为当前登陆的目录地址+filePath组合后的目录下的文件名
	 * @param filter
	 * @return
	 * @throws IOException
	 */
	public String[] listFTPFiles(String ftpPath, FileFilter filter) throws IOException {
		String[] ss = listNames(ftpPath);
		if (ss == null)
			return null;
		List<String> v = new ArrayList<String>();
		for (int i = 0 ; i < ss.length ; i++) {
			File f = new File(ss[i]);
			if ((filter == null) || filter.accept(f)) {
				v.add(f.getName());
			}
		}
		return (String[])(v.toArray(new String[v.size()]));
	}
	/**
	 * 列出path目录下的所有文件(包括路径ftpPath)(SFTP包括文件夹)
	 * @param ftpPath
	 * 	若以"/"开头则是登陆以后开始的ftpPath目录下的文件名，与当前目录无关，
	 * 	若不以"/"开头，则为当前登陆的目录地址+filePath组合后的目录下的文件名
	 * @return 数组(文件全路径+文件名)
	 * @throws IOException
	 * @throws SftpException 
	 */
	public String[] listNames(String ftpPath) throws IOException {
		try {
			if(!ftpPath.endsWith("/"))
				ftpPath = ftpPath + "/";
			if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_SFTP){
				List<LsEntry> ls = sftp.ls(ftpPath);
				String[] res = new String[ls.size()-2];
				int resLen = 0;
				for(LsEntry s : ls){
					String fileName = s.getFilename();
					if(!".".equals(fileName) && !"..".equals(fileName)){
						res[resLen] = ftpPath + fileName;
						resLen++;
					}
				}
				return res;
			}else if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_FTP){
//				String[] ls = ftpClient.listNames(ftpPath);//会卡死线程
				String[] ls = this.listFtpNames(ftpPath);
				String[] res = new String[ls.length];
				int resLen = 0;
				for(String fileName :ls){
					res[resLen] = ftpPath + fileName;
					resLen++;
				}
				return res;
			}else{
				return new String[]{};
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String[]{};
	}
	/**
	 * 获取当前目录下的所有满足文件过滤器的文件名(SFTP包括文件夹)
	 * @param filter
	 * @return 返回文件名
	 * @throws IOException
	 */
	public String[] listFTPFiles(FileFilter filter) throws IOException {
		String[] ss = listNames();
		if (ss == null)
			return null;
		ArrayList<String> v = new ArrayList<String>();
		for (int i = 0 ; i < ss.length ; i++) {
			File f = new File(ss[i]);
			if ((filter == null) || filter.accept(f)) {
				v.add(f.getName());
			}
		}
		return (String[])(v.toArray(new String[v.size()]));
	}
	/**
	 * 列出当前目录下的所有文件及文件夹名数组(SFTP包括文件夹)
	 * @return 数组(文件名)
	 * @throws IOException
	 */
	public String[] listNames() throws IOException {
		try {
			if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_SFTP){
				String ftpPath = sftp.pwd();
				if(!ftpPath.endsWith("/"))
					ftpPath = ftpPath + "/";
				List<LsEntry> ls = sftp.ls(ftpPath);
				String[] res = new String[ls.size()-2];
				int resLen = 0;
				for(LsEntry s : ls){
					String fileName = s.getFilename();
					if(!".".equals(fileName) && !"..".equals(fileName)){
						res[resLen] = fileName;
						resLen++;
					}
				}
				return res;
			}else if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_FTP){
//				return ftpClient.listNames(); //会卡死线程
				return this.listFtpNames((String)null);
			}else{
				return new String[]{};
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return new String[]{};
	}
	/**
	 * 列出文件名
	 * @param ftpPath
	 * 	若以"/"开头则是登陆以后开始的ftpPath目录下的文件名，与当前目录无关，
	 * 	若不以"/"开头，则为当前登陆的目录地址+filePath组合后的目录下的文件名
	 * @return
	 * @throws IOException 
	 */
	private String[] listFtpNames(String ftpPath) throws IOException{
		if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_FTP){
			if(localPassiveMode){
				ftpClient.enterLocalPassiveMode();//设置被动模式，防止死锁
			}
			FTPFile[] ftpFiles = ftpClient.listFiles(ftpPath);
			if(ftpFiles != null){
				List<String> ftpNames = new ArrayList<String>();
				for(FTPFile ftpFile : ftpFiles){
					if (ftpFile.isFile()) {
						ftpNames.add(ftpFile.getName());
					}
				}
				String[] ftp = new String[ftpNames.size()];
				for(int i = 0 ; i < ftpNames.size(); i ++){
					ftp[i] = ftpNames.get(i);
				}
				ftpNames.clear();
				return ftp;
			}
		}
		return new String[]{}; 
	}
	/**
	 * 删除文件，需登录到目录下，才能删除
	 * @param filename
	 * @return
	 */
	public boolean deleteFile(String filename) {
		String filepath = null;
		try{
			if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_SFTP){
				filepath = sftp.pwd();
				sftp.rm(filename);
				log.debug("ftpconnection_deleteFile_success.....file name:" + filepath + "/" + filename);
				return true;
			}else if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_FTP){
				boolean result = ftpClient.deleteFile(filename);
				log.debug("ftpconnection_deleteFile_success.....fileName: "	+ ftpClient.printWorkingDirectory() + "/"+ filename);
				return result;
			}else{
				return false;
			}
		}catch (Exception e) {
			String message = StringUtil.null2Str(e.getMessage()).replaceAll("\\s", "");
			if(message.equalsIgnoreCase("Nosuchfile")){
				log.debug("ftpconnection_deleteFile_success.....file name:" + filepath + "/" + filename);
				return true;
			}else{
				log.error(e.getMessage(),e);
			}
		}
		return false;
	}
	/**
	 * 下载 文件，filePath若以"/"开头则是登陆后的地址，与登陆后的地址无关，若不以"/"开头，则为登陆后的地址+filePath+"/"+fileName
	 * @param filePath
	 * 	若以"/"开头则是登陆以后开始的目录地址 即filePath+"/"+fileName，
	 * 	若不以"/"开头，则为当前登陆的目录地址与其他地址组合即登陆目录地址+"/"+filePath+"/"+fileName
	 * @param fileName
	 * @param localPath
	 * @throws IOException
	 * @throws CmsException
	 */
	// 从FTP服务器下载文件
	public void downLoadFile(String filePath, String fileName, String localPath)throws IOException, CmsException {
		String remoteFileName = CmsUtil.replaceSeparator(filePath + "/" + fileName);
		FileOutputStream fos = null;
		File file = null;
		try {
			file = new File(CmsUtil.replaceSeparator(localPath + "/" + fileName));
			FileUtil.createNewFile(file);
			fos = new FileOutputStream(file);
			
			if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_SFTP){
				sftp.get(remoteFileName, fos);
				log.debug("downloadFile is success:  " + remoteFileName);
			}else if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_FTP){
				if(localPassiveMode){
					ftpClient.enterLocalPassiveMode();//设置被动模式，防止死锁
				}
				if (ftpClient.retrieveFile(remoteFileName, fos)) {
					log.debug("downloadFile is success:  " + remoteFileName);
				}
//				else {
//					fos.close();
//					fos = null;
//					if (file != null)
//						FileUtil.deleteFile(file);
//					log.error("downloadFile is error! " + remoteFileName + "  is not found!");
//					throw new CmsException(CmsException.FTP_FILE_NOT_FOUND,
//							remoteFileName);
//				}
			}else{
				throw new CmsException("ERROR FTP  MODE SETTING");
			}
		} catch(Exception e){
			log.error(e.getMessage(),e);
			if (file != null)
				FileUtil.deleteFile(file);
			log.error("downloadFile is error! " + remoteFileName + "  is not found!");
			throw new CmsException(e.getMessage());
		} finally {
			if (fos != null)
				fos.close();
		}
	}
	/**
	 * 删除文件，需登录到目录下
	 * @param file
	 * @throws CmsException
	 * @throws IOException
	 */
	// 要求工作目录已准备
	public void upLoadFile(File file) throws CmsException, IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			String destFileName = file.getName();
			String tempFileName = "temp_" + destFileName;
			//上传本地文件到服务器上(文件名以'temp_'开头，当上传完毕后，名字改为正式名)
			if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_SFTP){
				sftp.put(fis, tempFileName);
				try{
					sftp.rm(destFileName);
				}catch(Exception e){
					log.error(e.getMessage(),e);
				}
				sftp.rename(tempFileName, destFileName);
				log.debug("ftpconnection_uploadFile_success.....fileName: "	+ sftp.pwd() + "/"+ destFileName);
			}else if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_FTP){
				if(localPassiveMode){
					ftpClient.enterLocalPassiveMode();//设置被动模式，防止死锁
				}
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				if (ftpClient.storeFile(tempFileName, fis)) {
					// 上传完毕后，名字改为正式名(该方法在远程有效，本地不用此方法，而用renameTo方法)
					ftpClient.rename(tempFileName, destFileName);
					log.debug("ftpconnection_uploadFile_success.....fileName: "	+ ftpClient.printWorkingDirectory() + "/"+ destFileName);
				} else {
					throw new CmsException(CmsException.FTP_NO_PERMISSION,"no write permission");
				}
			}else{
				throw new CmsException("ERROR FTP MODE SETTING");
			}
		} catch (Exception e) {
			throw new CmsException(e.getMessage());
		} finally {
			if (fis != null)
				fis.close();
		}
	}
	
	public FTPClient getFtpClient() {
		return ftpClient;
	}
	public ChannelSftp getSftp(){
		return this.sftp;
	}
	public int getFtpMode() {
		return ftpMode;
	}
	public void setFtpMode(int ftpMode) {
		this.ftpMode = ftpMode;
	}
	/**
	 * 下载文件，需登录到fileName的目录下
	 * @param fileName
	 * 	文件名
	 * @param localPath
	 * 	下载到的本地目录
	 * @param prefixName
	 * 	改名下载的前缀
	 * @param isSourceDel
	 * 	是否删除ftp上的文件
	 * @param showFailMsg
	 * 	显示下载失败的信息
	 * @return
	 */
	public Boolean downLoadFile(String fileName,String localPath, String prefixName, boolean isSourceDel, boolean showFailMsg) throws Exception{
		Boolean result = false;
		FileOutputStream fos = null;
		File file = null;
		Boolean renameResult = false;
		String newName = fileName;
		//是否需要改名后下载，需要支持删除，否则改完名以后文件名改不回来就悲剧了。
		Boolean isNeedRename = false;
		if(isSourceDel){
			if(prefixName == null || prefixName.equals("")){
			}else{
				isNeedRename = true;
				newName = prefixName+fileName;
			}
		}
		try {
			file = new File(CmsUtil.replaceSeparator(localPath + "/" + fileName));
			FileUtil.createNewFile(file);
			fos = new FileOutputStream(file);
			
			if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_SFTP){
				if(isNeedRename){
					sftp.rename(fileName, newName);
					sftp.get(newName, fos);
					renameResult = true;
					log.debug("downloadFile is success:  " + fileName);
				}else{
					sftp.get(fileName, fos);
					log.debug("downloadFile is success:  " + fileName);
				}
				result = true;
			}else if(this.ftpMode == com.wondertek.mobilevideo.core.util.ftp.FtpServer.FTP_MODE_FTP){
				if(localPassiveMode){
					ftpClient.enterLocalPassiveMode();//设置被动模式，防止死锁
				}
				if(isNeedRename){
					renameResult = ftpClient.rename(fileName, newName);
					if(renameResult){
						if (ftpClient.retrieveFile(newName, fos)) {
							result = true;
							log.debug("downloadFile is success:  " + fileName);
						}else if(showFailMsg){
							log.error("downloadFile is failure:  " + fileName);
						}
					}else {
						if (ftpClient.retrieveFile(fileName, fos)) {
							result = true;
							log.debug("downloadFile is success:  " + fileName);
						}else if(showFailMsg){
							log.error("downloadFile is failure:  " + fileName);
						}
					}
				}else if (ftpClient.retrieveFile(fileName, fos)) {
					result = true;
					log.debug("downloadFile is success:  " + fileName);
				}else if(showFailMsg){
					log.error("downloadFile is failure:  " + fileName);
				}
			}else{
				log.error("downloadFile is failure ,ftpmode is error:  " + fileName);
			}
			
			try {
				if(result && isSourceDel){
					if(renameResult){
						deleteFile(newName);
					}else{
						deleteFile(fileName);
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
		} catch(Exception e){
			//能进入到这里，说明文件存在，下载失败了。
			log.error("downloadFile is failure,fileName:"+fileName+",error msg:"+e.getMessage(),e);
			throw e;
		} finally {
			if (fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					log.error("downloadFile fileName:"+fileName+" ,close fos error,msg:"+e.getMessage(),e);
				}
			}
			try {
				if(!result){
					file.delete();
				}
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
		}
		return result;
	}
	/**
	 * 判定FTP服务器上文件路径是否存在，如果不存在，自动创建，返回true；否则，创建失败返回false
	 * @param ftpServer	ftp信息
	 * @param localHomePath	根目录
	 * @param filePaths	ftp文件信息
	 * @return
	 * @throws CmsException
	 * @throws IOException
	 */
	public boolean isExistPath(com.wondertek.mobilevideo.core.util.ftp.FtpServer ftpServer, String localHomePath, String[] filePaths) throws CmsException,IOException {
		boolean flag = false;
		synchronized(FtpConnection.class){
			//整理FTP路径
			String userHomePath = ftpServer.getFtpurl();
			userHomePath = CmsUtil.replaceSeparator(userHomePath);
			localHomePath = CmsUtil.replaceSeparator(localHomePath);
			userHomePath = FilePathHelper.joinPath(userHomePath,localHomePath);
			if (!userHomePath.startsWith("/"))
				userHomePath = "/" + userHomePath;
			if(!userHomePath.endsWith("/"))
				userHomePath =  userHomePath+"/";
			
			try {
				if (filePaths == null || filePaths.length == 0)
					throw new CmsException(CmsException.FTP_PATH_NOT_FOUND, "path is empty!");
				
				// 相对路径拆分
				String[] filePathArray = filePaths[1].split("[/\\\\]");
				// 相对路徑
				String dir = "";
				if (filePathArray.length > 1)
					dir = CmsUtil.replaceSeparator(filePaths[1].substring(0,
							filePaths[1].length()
									- filePathArray[filePathArray.length - 1]
											.length() - 1));
				if(dir != null && !dir.equals("") && dir.startsWith("/")){
					dir.substring(1);
				}
				// FTP当前目录和FTP默认目录
				String ftpCurPath = getWorkingDirectory();
				String ftpDefaultHomePath = getDefaultHomePath();
				if (!ftpCurPath.equals((ftpDefaultHomePath.equals("/") ? "" : ftpDefaultHomePath) + userHomePath + dir)) {
					// 先把工作目录设定为用户目录
					if (!setWorkingDirectory((ftpDefaultHomePath.equals("/") ? "" : ftpDefaultHomePath) + userHomePath)) 
						throw new CmsException(CmsException.FTP_FTPURL_NOT_FOUND, "");
					for (int i = 0; i < filePathArray.length - 1; i++) {
						if (!filePathArray[i].equals("")){
							boolean isSucc = makeDirectory(filePathArray[i]);
		                    if(!isSucc){
		                    	try {
		                    		Thread.sleep(1000);
		                    	} catch (InterruptedException e) {
		                    	}
		                    	for(int k =0;k<5;k++){
		                    		isSucc= makeDirectory(filePathArray[i]);
		                    			if(isSucc)
		                    				break;
		                    		}
		                    	if(!isSucc)
		                    		throw new CmsException(CmsException.FTP_UNDIFINE_ERROR, filePathArray[i]);
		                    }
						}
					}
				}
				flag = true;
			}catch(CmsException e){
				log.error(e.getErrorCode(),e);
				throw e;
			}catch(IOException e){
				log.error(e.getMessage(),e);
				throw e;
			} finally {
			}
		}
		return flag;
	}
}

