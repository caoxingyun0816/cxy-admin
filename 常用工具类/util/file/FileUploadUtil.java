package com.wondertek.mam.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileUploadUtil {
	private final static Log log = LogFactory.getLog(FileUploadUtil.class);
	
	public static boolean createFolder(String path) {
		try {
			File folder = new File(path);
			if (!folder.exists()) {
				folder.mkdir();
			}
		} catch (Exception e) {
			log.error("error occur when creating folder!errorMsg:"+e.getMessage());
			return false;
		}
		return true;
	}

	public static boolean newFile(String fullName) {
		try {
			File file = new File(fullName);
			if (!file.exists()) {
				if (!file.getParentFile().exists())
					file.getParentFile().mkdirs();
				file.createNewFile();
			}
		} catch (Exception e) {
			log.error("error occur when creating folder!errorMsg:"+e.getMessage());
			return false;
		}
		return true;
	}

	public static boolean newFile(String fullName, String text) {

		try {
			newFile(fullName);
			FileWriter out = new FileWriter(fullName, false);
			out.write(text);
			out.close();
		} catch (Exception e) {
			log.error("error occur when creating file!errorMsg:"+e.getMessage());
			return false;
		}
		return true;

	}

	public static boolean appendFile(String fullName, String text) {

		try {
			newFile(fullName);
			FileWriter out = new FileWriter(fullName, true);
			out.write(text);
			out.close();
		} catch (Exception e) {
			log.error("error occur when creating file!errorMsg:"+e.getMessage());
			return false;
		}
		return true;

	}

	public static boolean delFile(String fullName) {
		try {
			File file = new File(fullName);
			if (file.exists()) {
				return file.delete();
			} else {
				return true;
			}

		} catch (Exception e) {
			log.error("errorMsg:"+e.getMessage());
			return false;
		}
	}

	public static void delFileOnExit(String fullName) {
		try {
			File file = new File(fullName);
			if (file.exists()) {
				file.deleteOnExit();
			}
		} catch (Exception e) {
			log.error("errorMsg:"+e.getMessage());
		}
	}

	public static boolean rename(String fileName, String distFile) {
		synchronized (fileName) {
			File oldFile = new File(fileName);
			if (oldFile.exists()) {
				try {
					oldFile.renameTo(new File(distFile));
					return true;
				} catch (Exception e) {
					log.error("errorMsg:"+e.getMessage());
					return false;
				}
			} else
				return true;
		}
	}

	public static boolean removeFolder(String pathFolder,
			boolean recursiveRemove) {
		File folder = new File(pathFolder);
		if (folder.isDirectory()) {
			return removeFolder(folder, recursiveRemove);
		}
		return false;
	}

	public static boolean removeFolder(File folder, boolean removeRecursivly) {
		if (removeRecursivly) {
			for (File current : folder.listFiles()) {
				if (current.isDirectory()) {
					removeFolder(current, true);
				} else {
					current.delete();
				}
			}
		}
		return folder.delete();
	}

	public static boolean delFolder(String path) {
		return removeFolder(path, true);
	}

	public static boolean copyFile(String source, String target) {
		boolean isSuc = false;
		InputStream in = null;
		FileOutputStream out = null;
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(source);
			newFile(target);
			if (oldfile.exists()) {
				in = new FileInputStream(source);
				out = new FileOutputStream(target, false);// no append,
															// overwrite old.
				byte[] buffer = new byte[4096];
				while ((byteread = in.read(buffer)) != -1) {
					bytesum += byteread;
					out.write(buffer, 0, byteread);
				}
				isSuc = true;
			} else {
				System.err.println("File " + source + " not exists");
			}

		} catch (Exception e) {
			log.error("Exception occur when copying a file!errorMsg:"+e.getMessage());
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
			}
		}
		return isSuc;
	}

	public static boolean copyFileNoOverWrite(String oldfile,
			String newfilepath, String newfile) {
		return copyFileNoOverWrite(oldfile, newfilepath + newfile);
	}

	public static boolean copyFileNoOverWrite(String oldfile, String newfile) {
		if (com.wondertek.mobilevideo.core.util.FileUtil.checkFileExists(oldfile)
				&& !com.wondertek.mobilevideo.core.util.FileUtil.checkFileExists(newfile)) { // 文件存在时
			return copyFile(oldfile, newfile);
		}
		return true;
	}

	public static void copyFolder(String source, String target) {
		
		if(target.indexOf(source) != -1){
			System.err.println("target is source's sub directory");
			return ;
		}
				
		try {
			File dir = new File(source);
			File[] listFiles = dir.listFiles();
			File fileSource = null;
			for (int i = 0; i < listFiles.length; i++) {
				fileSource = listFiles[i];
				if (fileSource.isDirectory()) {
						createFolder(target + "/" + fileSource.getName());
						copyFolder(fileSource.getAbsolutePath() + "/", target
								+ "/" + fileSource.getName() + "/");
				} else {
					copyFile(fileSource.getAbsolutePath(), target + "/"
							+ fileSource.getName());
				}
			}

		} catch (Exception e) {
			
		}
	}

	public static boolean moveFile(String oldPath, String newFolderPath,
			String fileName) {
		return copyFile(oldPath, newFolderPath + fileName) && delFile(oldPath);
	}
	
	public static boolean moveFile(String oldPath, String newPath) {
		return copyFile(oldPath, newPath) && delFile(oldPath);
	}

	public static void moveFolder(String oldPath, String newPath) {
		copyFolder(oldPath, newPath);
		delFolder(oldPath);
	}

	public static String getExtName(String filename) {
		int index = filename.lastIndexOf('.');
		if (index == -1) {
			return "";
		} else {
			return filename.substring(index + 1);
		}
	}

}
