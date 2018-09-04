package com.wondertek.mam.util.ftp;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wondertek.mobilevideo.core.util.CmsException;
import com.wondertek.mobilevideo.core.util.CmsUtil;
import com.wondertek.mobilevideo.core.util.ftp.FtpConnection;
import com.wondertek.mobilevideo.core.util.ftp.FtpPoolManager;
import com.wondertek.mobilevideo.core.util.ftp.FtpServer;

public class FtpUtil {

	public static final Log log = LogFactory.getLog(FtpUtil.class);

	public static void upLoadOrDelFile(FtpServer pubServer,
			String localHomePath, String[] filePath, boolean queueType)
			throws CmsException, IOException {
		String userHomePath = pubServer.getFtpurl();
		userHomePath = CmsUtil.replaceSeparator(userHomePath);
		FtpConnection ftpConnection = null;
		try {
			ftpConnection = getConnection(pubServer);
			if (filePath == null || filePath.length == 0)
				throw new CmsException(CmsException.FTP_PATH_NOT_FOUND,
						"path is empty!");

			// 本地文件全路径
			String localFullPath;
			if (filePath[0] == null || filePath[0].equals("")) {
				localFullPath = CmsUtil.replaceSeparator(localHomePath);
			} else {
				localFullPath = CmsUtil.replaceSeparator(localHomePath + "/"
						+ filePath[0]);
			}
			ftpConnection.setWorkingDirectory(userHomePath);
			File file = new File(localFullPath);
			if (queueType) {
				ftpConnection.upLoadFile(file);
			} else {
				ftpConnection.deleteFile(file.getName());
			}
		} catch (CmsException ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
			throw ex;
		} catch (IOException ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
			throw ex;
		} finally {
			FtpPoolManager.freeConnection(pubServer, ftpConnection);
		}
	}
	/**
	 * 上传文件并增加文件夹
	 * 注：需要注意连接池的问题
	 * @param pubServer
	 * @param localHomePath
	 * @param filePath
	 * @throws CmsException
	 * @throws IOException
	 */
	public static void upLoadOrMkdir(FtpServer pubServer,
			String localHomePath, String[] filePath)
			throws CmsException, IOException {
		String userHomePath = pubServer.getFtpurl();
		userHomePath = CmsUtil.replaceSeparator(userHomePath);
		FtpConnection ftpConnection = null;
		try {
			ftpConnection = getConnection(pubServer);
			if (filePath == null || filePath.length == 0)
				throw new CmsException(CmsException.FTP_PATH_NOT_FOUND,
						"path is empty!");

			// 本地文件全路径
			String localFullPath;
			if (filePath[0] == null || filePath[0].equals("")) {
				localFullPath = CmsUtil.replaceSeparator(localHomePath);
			} else {
				localFullPath = CmsUtil.replaceSeparator(localHomePath + "/"
						+ filePath[0]);
			}
			boolean b=ftpConnection.setWorkingDirectory(userHomePath);
			if (!b) {
				// 相对路径拆分
				String[] filePathArray = filePath[1].split("[/\\\\]");
				for (int i = 0; i < filePathArray.length; i++) {
					if (!filePathArray[i].equals(""))
						ftpConnection.makeDirectory(filePathArray[i]);
				}
			}
			System.out.println("FTP最终路径： "+ftpConnection.getWorkingDirectory());
			File file = new File(localFullPath);
			ftpConnection.upLoadFile(file);
			
		} catch (CmsException ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
			throw ex;
		} catch (IOException ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
			throw ex;
		} finally {
			FtpPoolManager.freeConnection(pubServer, ftpConnection);
		}
	}
	private static FtpConnection getConnection(FtpServer pubServer)
			throws CmsException {
		return FtpPoolManager.getConnection(pubServer);
	}
}
