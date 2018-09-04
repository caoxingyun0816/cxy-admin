package com.wondertek.mam.util.backupUtils.util22.ftp;

import com.wondertek.mam.util.others.CmsUtil;
import com.wondertek.mam.util.others.PagingFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FtpTemplate {
	public static final Log log = LogFactory.getLog(FtpTemplate.class);
	public static FtpConnection getConnection(FtpServer pubServer)throws CmsException {
		//return FtpPoolManager.getConnection(pubServer);
		return null;
	}

	/**
	 * 上传或者删除ftp服务器上的文件
	 * 
	 * @param pub	 *            ftp服务器对象
	 * @param localHomePath	 *            本地文件路径根目录
	 * @param filePaths	 *            上传文件路径集合
	 * @param queueType	 *            上传or删除 true 上传 false 删除
	 * @return
	 */
	public static String upLoadOrDelFile(FtpServer pub, String localHomePath,
                                         List<String[]> filePaths, boolean queueType) {
		String result = "";		
		for (String[] filePath : filePaths) {
			try {
				FtpTemplate.upLoadOrDelFile(pub, localHomePath, filePath,
						queueType);
			} catch (CmsException e) {
				result = result + "|" + e.getErrorCode() + ":" + e.getMessage();
			} catch (IOException e1) {
				result = result + "|" + e1.getMessage();
			}
		}
		return result;
	}
	
	/**
	 * 上传或删除文件
	 * @param localHomePath
	 *            本地根路径
	 * @param filePath
	 *            文件路径(包括文件名) *
	 * @param queueType
	 *            true: uploadfile false : delfile
	 * @throws CmsException
	 */
	public static void upLoadOrDelFile(FtpServer pubServer, String localHomePath,
                                       String[] filePath, boolean queueType) throws CmsException,
			IOException {
		String userHomePath = pubServer.getFtpurl();
		userHomePath = CmsUtil.replaceSeparator(userHomePath);
		if (!userHomePath.startsWith("/"))
			userHomePath = "/" + userHomePath;
		FtpConnection ftpConnection = null;
		try {
			ftpConnection = getConnection(pubServer);
			if (filePath == null || filePath.length == 0)
				throw new CmsException(CmsException.FTP_PATH_NOT_FOUND, "path is empty!");
			
			// 本地文件全路径
			String localFullPath ;
			if(filePath[0] == null || filePath[0].equals("")){
				localFullPath = CmsUtil.replaceSeparator(localHomePath);
			}else{
				localFullPath = CmsUtil.replaceSeparator(localHomePath + "/" + filePath[0]);
			}
		
			// 相对路径拆分
			String[] filePathArray = filePath[1].split("[/\\\\]");
			// 相对路徑
			String dir = "";
			if (filePathArray.length > 1)
				dir = CmsUtil.replaceSeparator(filePath[1].substring(0,
						filePath[1].length()
								- filePathArray[filePathArray.length - 1]
										.length() - 1));
			// 文件名
			String fname = filePathArray[filePathArray.length - 1];
			// FTP当前目录和FTP默认目录
			String ftpCurPath = ftpConnection.getWorkingDirectory();
			String ftpDefaultHomePath = ftpConnection.getDefaultHomePath();
			if (!ftpCurPath.equals((ftpDefaultHomePath.equals("/") ? "" : ftpDefaultHomePath) + userHomePath + dir)) {
				
				// 先把工作目录设定为用户目录
				if (!ftpConnection.setWorkingDirectory((ftpDefaultHomePath.equals("/") ? "" : ftpDefaultHomePath) + userHomePath)) 
					throw new CmsException(CmsException.FTP_FTPURL_NOT_FOUND, "");
				
				for (int i = 0; i < filePathArray.length - 1; i++) {
					if (!filePathArray[i].equals("")){
						boolean isSucc = ftpConnection.makeDirectory(filePathArray[i]);
	                    if(!isSucc){
	                    	try {
	                    		Thread.sleep(1000);
	                    	} catch (InterruptedException e) {
	                    	}
	                    	for(int k =0;k<5;k++){
	                    		isSucc= ftpConnection.makeDirectory(filePathArray[i]);
	                    			if(isSucc)
	                    				break;
	                    		}
	                    	if(!isSucc)
	                    		throw new CmsException(CmsException.SYNC_PARSEFILE_ERROR, filePathArray[i]);//创建文件路劲失败，异常变量需要重新定义
	                    }
					}
				}
			}
			
			if (localFullPath.contains("*")) {
				// 本地文件全路径的目录
				String fullDirPath = localFullPath.substring(0, localFullPath.length() - fname.length() - 1);
				File fullDir = new File(fullDirPath);
				File[] allFile = fullDir.listFiles(new PagingFileFilter(fname));
				if (allFile == null || allFile.length < 1) {
					if(queueType == true)
						throw new CmsException(CmsException.FTP_FILE_NOT_FOUND, localFullPath);
				} else {
					for (File file : allFile) {
						if (queueType) {
							ftpConnection.upLoadFile(file);
						} else {
							ftpConnection.deleteFile(file.getName());
						}

					}
				}
			} else {
				File file = new File(localFullPath);
				if (queueType) {
					ftpConnection.upLoadFile(file);
				} else {
					ftpConnection.deleteFile(file.getName());
				}

			}
		}catch(CmsException ex){
			ex.printStackTrace();
			log.error(ex.getMessage());
			throw ex;
		}catch(IOException ex){
			ex.printStackTrace();
			log.error(ex.getMessage());
			throw ex;
		} finally {
			FtpPoolManager.freeConnection(pubServer, ftpConnection);
		}
	}
	/**
	 * 下载到传入localHomePath地址下面
	 * @param pubServer
	 * @param ftpFilePath
	 *            路径
	 * @param name
	 *            文件名
	 * @param localPath
	 *            本地路径
	 * @param isSourceDel 
	 *            true   删除ftp上的源文件
	 *            false  不删除
	 * @throws Exception 
	 */
	public static void downloadFile(FtpServer pubServer, String ftpFilePath, String name,
                                    String localPath, boolean isSourceDel) throws CmsException,IOException,Exception {
		FtpConnection ftpConnection = null;
		try {
			ftpConnection = getConnection(pubServer);
			String userHomePath = pubServer.getFtpurl();
			userHomePath = CmsUtil.replaceSeparator(userHomePath);
			if (!userHomePath.startsWith("/"))
				userHomePath = "/" + userHomePath;
			log.debug("[UserHomePath=" + userHomePath + "]");
			
			// FTP当前目录和FTP默认目录
			String ftpCurPath = ftpConnection.getWorkingDirectory();
			String ftpDefaultHomePath = ftpConnection.getDefaultHomePath();
			if (!ftpCurPath.equals((ftpDefaultHomePath.equals("/") ? "" : ftpDefaultHomePath) + userHomePath)) {
				// 先把工作目录设定为用户目录
				if (!ftpConnection.setWorkingDirectory((ftpDefaultHomePath.equals("/") ? "" : ftpDefaultHomePath) + userHomePath)) 
					throw new CmsException(CmsException.FTP_FTPURL_NOT_FOUND, "");
			}
			log.debug("[WorkingDirectory=" + ftpConnection.getWorkingDirectory() + "]");
			
			// FTP文件全路径
			String ftpFullPath ;
			if(ftpFilePath == null || ftpFilePath.equals("")){
				ftpFullPath = CmsUtil.replaceSeparator(ftpConnection.getWorkingDirectory());
			}else{
				ftpFullPath = CmsUtil.replaceSeparator(ftpConnection.getWorkingDirectory() + "/" + ftpFilePath);
			}
			log.debug("[FtpFullPath=" + ftpFullPath + "]");
			
			ftpConnection.downLoadFile(ftpFullPath, name, localPath);
			if(isSourceDel){
				String remoteFileName = CmsUtil.replaceSeparator(ftpFilePath + "/" + name);
				if (remoteFileName.startsWith("/")){
					remoteFileName = remoteFileName.substring(1);
				}
				log.debug("ftp deleteFile[remoteFileName=" + remoteFileName + "] doing!");
				ftpConnection.deleteFile(remoteFileName);
				log.debug("ftp deleteFile[remoteFileName=" + remoteFileName + "] success!");
			}
		}catch(CmsException ce){
			throw ce;
		}catch(IOException ie){
			throw ie;
		}catch(Exception e){
			throw e;
		}finally {
			FtpPoolManager.freeConnection(pubServer, ftpConnection);
		}
	}
	
	/**
	 * 列举ftp服务器指定path下的所有文件/文件夹名称(SFTP包括文件夹)
	 * @param pubServer
	 * @param path
	 * @return
	 * @throws CmsException
	 * @throws IOException
	 * @throws Exception
	 */
	public static String[] listNames(FtpServer pubServer, String path) throws CmsException,IOException,Exception {
		String[] listNames = null;
		FtpConnection ftpConnection = null;
		try {
			ftpConnection = getConnection(pubServer);
			ftpConnection.setWorkingDirectory(ftpConnection
					.getDefaultHomePath());
			String userHomePath = pubServer.getFtpurl();
			log.debug("[userHomePath="+userHomePath+"]");
			if(!"/".equals(userHomePath)){
				userHomePath = CmsUtil.replaceSeparator(userHomePath);
				if (userHomePath.startsWith("/"))
					userHomePath = userHomePath.substring(1);				
				ftpConnection.setWorkingDirectory(userHomePath);
			}
			log.debug("[WorkingDirectory="+ftpConnection.getWorkingDirectory()+"]");
			if(!"/".equals(path)){
				path = CmsUtil.replaceSeparator(path);
				if (path.startsWith("/"))
					path = path.substring(1);				
				ftpConnection.setWorkingDirectory(path);
			}
			log.debug("[WorkingDirectory="+ftpConnection.getWorkingDirectory()+"]");
			
			listNames = ftpConnection.listNames();
		}catch(CmsException ce){
			throw ce;
		}catch(IOException ie){
			throw ie;
		}catch(Exception e){
			throw e;
		}finally {
			FtpPoolManager.freeConnection(pubServer, ftpConnection);
		}
		return listNames;
	}
	/**
	 * 下载指定目录下面指定类型文件
	 * @param ftpServer
	 * @param ftpPath
	 * @param localPath
	 * @param prefixName
	 * @param fileNameLength
	 * @param fileFilter
	 * @param isSourceDel
	 * @return
	 */
	public static List<FtpFileVo> downLoadFilesByPath(FtpServer ftpServer, String ftpPath, String localPath, String prefixName, Integer fileNameLength, FileFilter fileFilter, boolean isSourceDel) {
		List<FtpFileVo> ftpFileVos = new ArrayList<FtpFileVo>();
		FtpConnection ftpConnection = null;
		try {
			//进入工作目录
			ftpConnection = getConnection(ftpServer);
			ftpConnection.setWorkingDirectory(ftpConnection.getDefaultHomePath());
			String userHomePath = ftpServer.getFtpurl();
			log.debug("[userHomePath="+userHomePath+"]");
			if(!"/".equals(userHomePath)){
				userHomePath = CmsUtil.replaceSeparator(userHomePath);
				if (userHomePath.startsWith("/"))
					userHomePath = userHomePath.substring(1);				
				if(!ftpConnection.setWorkingDirectory(userHomePath))
					return ftpFileVos;
			}
			//进入指定目录
			if(!"/".equals(ftpPath)){
				ftpPath = CmsUtil.replaceSeparator(ftpPath);
				if (ftpPath.startsWith("/"))
					ftpPath = ftpPath.substring(1);				
				if(!ftpConnection.setWorkingDirectory(ftpPath))
					return ftpFileVos;
			}
			
			String[] filenames = ftpConnection.listFTPFiles(fileFilter);
			if (filenames != null) {
				for (String fileName : filenames) {
					if(fileName.startsWith("temp_") || fileName.startsWith(".")){
						continue;
					}
					if(fileNameLength != null && fileNameLength.intValue() != fileName.length()){//读取指定文件长度的
						continue;
					}
					
					Boolean result = ftpConnection.downLoadFile(fileName,localPath,"temp_"+prefixName,isSourceDel,true);
					if(result){
						FtpFileVo ftpFileVo = new FtpFileVo();
						ftpFileVo.setFileName(fileName);
						ftpFileVos.add(ftpFileVo);
					}
				}
			}
		}catch(CmsException ce){
			log.error(ce.getMessage(),ce);
		}catch(IOException ie){
			log.error(ie.getMessage(),ie);
		}catch(Exception e){
			log.error(e.getMessage(),e);
		}finally {
			FtpPoolManager.freeConnection(ftpServer, ftpConnection);
		}
		return ftpFileVos;
	}
	/**
	 * 列出目录下指定类型文件
	 * @param ftpServer
	 * @param ftpPath
	 * @param fileNameLength
	 * @param fileFilter
	 * @return
	 * @throws CmsException
	 * @throws IOException
	 * @throws Exception
	 */
	public static List<String> listNames(FtpServer ftpServer, String ftpPath, Integer fileNameLength, FileFilter fileFilter) throws CmsException,IOException,Exception {
		List<String> ftpFileNames = new ArrayList<String>();
		FtpConnection ftpConnection = null;
		try {
			//进入工作目录
			ftpConnection = getConnection(ftpServer);
			ftpConnection.setWorkingDirectory(ftpConnection.getDefaultHomePath());
			String userHomePath = ftpServer.getFtpurl();
			log.debug("[userHomePath="+userHomePath+"]");
			if(!"/".equals(userHomePath)){
				userHomePath = CmsUtil.replaceSeparator(userHomePath);
				if (userHomePath.startsWith("/"))
					userHomePath = userHomePath.substring(1);				
				if(!ftpConnection.setWorkingDirectory(userHomePath))
					return ftpFileNames;
			}
			//进入指定目录
			if(!"/".equals(ftpPath)){
				ftpPath = CmsUtil.replaceSeparator(ftpPath);
				if (ftpPath.startsWith("/"))
					ftpPath = ftpPath.substring(1);				
				if(!ftpConnection.setWorkingDirectory(ftpPath))
					return ftpFileNames;
			}
			
			String[] filenames = ftpConnection.listFTPFiles(fileFilter);
			if(filenames != null){
				for(String filename : filenames){
					if(filename.startsWith(".") || filename.startsWith("temp_")){
						continue;
					}
					if(fileNameLength != null && filename.length() != fileNameLength){
						continue;
					}
					ftpFileNames.add(filename);
				}
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
			throw e;
		}finally {
			FtpPoolManager.freeConnection(ftpServer, ftpConnection);
		}
		return ftpFileNames;
	}
	/**
	 * 根据文件名下载 文件
	 * @param ftpServer
	 * @param ftpPath
	 * @param localPath
	 * @param ftpFileNameList
	 * @param isSourceDel
	 * @param showFailMsg
	 * @param nofileError
	 * @return
	 * @throws CmsException
	 * @throws IOException
	 * @throws Exception
	 */
	public static List<FtpFileVo> downLoadFilesByNames(FtpServer ftpServer, String ftpPath, String localPath,
                                                                                               List<String> ftpFileNameList, boolean isSourceDel, boolean showFailMsg, boolean nofileError) throws CmsException,IOException,Exception {
		List<FtpFileVo> ftpFileVos = new ArrayList<FtpFileVo>();
		FtpConnection ftpConnection = null;
		try {
			//进入工作目录
			ftpConnection = getConnection(ftpServer);
			ftpConnection.setWorkingDirectory(ftpConnection.getDefaultHomePath());
			String userHomePath = ftpServer.getFtpurl();
			log.debug("[userHomePath="+userHomePath+"]");
			if(!"/".equals(userHomePath)){
				userHomePath = CmsUtil.replaceSeparator(userHomePath);
				if (userHomePath.startsWith("/"))
					userHomePath = userHomePath.substring(1);				
				if(!ftpConnection.setWorkingDirectory(userHomePath))
					throw new CmsException("ftp path not found:ftpPath:"+userHomePath);
			}
			//进入指定目录
			if(!"/".equals(ftpPath)){
				ftpPath = CmsUtil.replaceSeparator(ftpPath);
				if (ftpPath.startsWith("/"))
					ftpPath = ftpPath.substring(1);				
				if(!ftpConnection.setWorkingDirectory(ftpPath))
					throw new CmsException("ftp path not found:ftpPath:"+ftpPath);
			}
			if(ftpFileNameList != null){
				for(String fileName : ftpFileNameList){
					Boolean result = ftpConnection.downLoadFile(fileName,localPath,null,isSourceDel,showFailMsg);
					if(result){
						FtpFileVo ftpFileVo = new FtpFileVo();
						ftpFileVo.setFileName(fileName);
						ftpFileVos.add(ftpFileVo);
					}else if(nofileError){
						throw new CmsException("downLoadFile error,fileName:"+fileName);
					}
				}
			}
		}catch(Exception e){
			log.error("downLoadFilesByNames error,msg:"+e.getMessage(),e);
			throw e;
		}finally{
			FtpPoolManager.freeConnection(ftpServer, ftpConnection);
		}
		return ftpFileVos;
	}
}