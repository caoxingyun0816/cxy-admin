package com.wondertek.mam.util.backupUtils.util22;

import java.io.BufferedInputStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
	/**
	 * 写入文本内容
	 * 
	 * @param filename
	 * @param text
	 * @throws IOException
	 */
	public static void writeTextFile(String filename, String text)
			throws IOException {
		File file = new File(filename);
		if (!file.exists())
			file.createNewFile();

		FileWriter writer = new FileWriter(file);
		writer.write(text);
		writer.flush();
		writer.close();
	}

	/**
	 * 复制文件到指定目录
	 * 
	 * @param sourceFile
	 * @param targetFile
	 * @throws IOException
	 */
	public static void copyFile(File sourceFile, File targetFile)
			throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			outBuff.flush();
		} finally {
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param filename
	 */
	public static void deleteFile(String filename) {
		File file = new File(filename);
		if (file.exists())
			file.delete();
	}

	/**
	 * 重命名文件
	 * 
	 * @param oldName
	 * @param newName
	 */
	public static void renameFile(String oldName, String newName) {
		File src = new File(oldName);
		File dest = new File(newName);
		src.renameTo(dest);
	}

	/**
	 * 递归删除目录及其中文件
	 * 
	 * @param folderName
	 */
	public static void deleteFolder(String folderName) {
		File folder = new File(folderName);
		if (folder.isDirectory()) {
			String[] children = folder.list();
			// 递归删除目录中的子目录下
			for (int i = 0; i < children.length; i++) {
				deleteFolder(folderName + File.separator + children[i]);
			}
		}
		// 目录此时为空，可以删除
		folder.delete();
	}

	/**
	 * 移动文件到新目录
	 * 
	 * @param filename
	 * @param foldername
	 */
	public static void moveFile(String filename, String foldername) {
		File file = new File(filename);
		File newFile = new File(foldername + File.separator + file.getName());
		file.renameTo(newFile);
		file.delete();
	}
}
