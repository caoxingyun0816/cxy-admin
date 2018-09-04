package com.wondertek.mam.util.others;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class CompressUtils implements Serializable {
	Logger log = LoggerFactory.getLogger(CompressUtils.class);

	/**
	 * 
	 * @param str
	 * @return 基于Base64编码的字符串
	 */

	public static byte[] gzip(String str) {
		if (StringUtils.isBlank(str)) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = null;
		try {
			gzip = new GZIPOutputStream(out);
			gzip.write(str.getBytes("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (gzip != null) {
				try {
					gzip.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return out.toByteArray();
	}

	/**
	 * 
	 * @param input
	 *            基于Base64编码的字符串
	 * @return
	 * @throws IOException
	 */
	public static String gunzip(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = null;
		GZIPInputStream ginzip = null;
		String decompressed = "";
		try {
			in = new ByteArrayInputStream(bytes);
			ginzip = new GZIPInputStream(in);
			byte[] buffer = new byte[1024 * 1024];
			int offset = -1;
			while ((offset = ginzip.read(buffer)) != -1) {
				out.write(buffer, 0, offset);
			}
			decompressed = out.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ginzip != null) {
				try {
					ginzip.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
		return decompressed;
	}

	public void compressLocalFile(String path, boolean deleteSrc) throws IOException, ClassNotFoundException {
		long start = System.currentTimeMillis();
		log.debug("begin compress file :" + path + "......");
		File fileIn = new File(path);
		// 输入流
		InputStream in = new FileInputStream(fileIn);
		File fileOut = new File(fileIn.getAbsolutePath() + ".gz");
		fileOut.delete();
		// 文件输出流
		OutputStream out = new GZIPOutputStream(new FileOutputStream(fileOut));
		byte[] buffer = new byte[1024 * 1024];
		// 压缩
		//IOUtils.copyLarge(in, out, buffer);//报错所以注释了
		if (deleteSrc)
			fileIn.deleteOnExit();
		log.debug("zip compress end size[" + fileIn.length() + "] ,write to path:" + fileOut.getAbsolutePath() + " used time " + (System.currentTimeMillis() - start) + "ms");
	}

	public void deCompressLocalFile(String path, boolean deleteSrc) throws IOException, ClassNotFoundException {
		long start = System.currentTimeMillis();
		log.debug("begin decompress file :" + path + "......");
		File fileIn = new File(path);
		// 输入流
		InputStream in = new GZIPInputStream(new FileInputStream(fileIn));
		String outPath = fileIn.getAbsolutePath().substring(0, fileIn.getAbsolutePath().lastIndexOf("\\."));
		File fileOut = new File(outPath);
		// 文件输出流
		OutputStream out = new FileOutputStream(fileOut);
		byte[] buffer = new byte[1024 * 1024];
		// 解压缩
		//IOUtils.copyLarge(in, out, buffer);//报错所以注释了
		if (deleteSrc)
			fileIn.deleteOnExit();
		log.debug("decompress file end size[" + fileIn.length() + "],write to path:" + fileOut.getAbsolutePath() + " used time " + (System.currentTimeMillis() - start) + "ms");
	}
}
