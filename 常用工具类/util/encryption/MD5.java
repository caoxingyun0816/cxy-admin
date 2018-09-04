/**
 * Copyright (c) 2012 netqin.
 * All Rights Reserved
 */
package com.wondertek.mam.util.encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.wondertek.mam.commons.MamConstants;

public class MD5 {
	private static final Logger log = Logger.getLogger(MD5.class);
	
	protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	
	/**
	 * md5算法
	 */
	public static String md5(String string) {
		return md5(string, "UTF-8");
	}
	
	/**
	 * md5算法
	 * @param string
	 * @param charset
	 * @return
	 */
	public static String md5(String string, String charset) {
		try {
			MessageDigest messagedigest = MessageDigest.getInstance("MD5");
			/*
			byte tmp[] = MessageDigest.getInstance("MD5").digest(
					string.getBytes(charset)); // MD5 的计算结果
			*/
			byte tmp[] = messagedigest.digest(
					string.getBytes(charset)); // MD5 的计算结果
			//最后结果缓冲区
			StringBuilder buf = new StringBuilder(32);
			for (byte b : tmp) {
				buf.append(String.format("%02x", b & 0xff));
			}
			return buf.toString();
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String getFileMD5String(File file) throws IOException{
		try {
			MessageDigest messagedigest = MessageDigest.getInstance("MD5");
			FileInputStream in = new FileInputStream(file);
			byte[] buffer = new byte[8192];
			int len;
			while((len = in.read(buffer)) != -1){
				messagedigest.update(buffer, 0, len);
			}
			in.close();
			return bufferToHex(messagedigest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 读取视频文件MD5，因视频文件多数都很大，所以采用该方式读取，不保证MD5会相同
	 * @param file
	 * @param info 用ffprobe读取的json串
	 * @return
	 * @throws IOException
	 */
	public static String getVideoMD5String(File file, String info) throws IOException{
		try {
			long startTime = System.currentTimeMillis();
			MessageDigest messagedigest = MessageDigest.getInstance("MD5");
			
			if(info != null && !"".equals(info)) {
				info  = info.replace(" ", "");//忽略空格
				
				Pattern p = Pattern.compile("\"filename\\s*\":\"?(.*?)(\"|\"|\\s+,)", Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(info);
				while (m.find()) {
					String dom = m.group();
					info = info.replace(dom, "");//忽略文件路径
				}
				
				byte[] infb = info.getBytes();
				messagedigest.update(infb, 0, infb.length);
			}
			
			FileInputStream in = new FileInputStream(file);
			byte[] buffer = new byte[8192];
			int len= 0;
			
			in.skip(512L);
			len = in.read(buffer);
			if(len != -1) {
				messagedigest.update(buffer, 0, len);
			}
			
			Long half = file.length()/2;
			in.skip(half);
			len = in.read(buffer);
			if(len != -1) {
				messagedigest.update(buffer, 0, len);
			}
			
			Long last = file.length() - 2048;
			in.skip(last);
			len = in.read(buffer, 0, 2048);
			if(len != -1) {
				messagedigest.update(buffer, 0, len);
			}
			
			in.close();
			
			byte[] lenb = String.valueOf(file.length()).getBytes();
			messagedigest.update(lenb, 0, lenb.length);
			
			String rst = bufferToHex(messagedigest.digest());
			long endTime = System.currentTimeMillis();
			log.info("============MD5计算持续时长[" + (endTime - startTime) + " ms]===========");
			return rst;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	private static String bufferToHex(byte bytes[]) {
		return bufferToHex(bytes, 0, bytes.length);
	}

	private static String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}
	
	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char c0 = hexDigits[(bt & 0xf0) >> 4];
		char c1 = hexDigits[bt & 0xf];
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}
	

	public static void main(String args[]) {
//		log.info("MD5(\"123456\"): " + md5("123456"));
		String str = "http://xxxx/" + MamConstants.PLATFORM_NAME + "/assetImport?userid=&orgid=&username=&cpid=&liveChannelPath=&channelId=";
		//String str = "http://xxxx/mam-admin/assetImport?userid=&orgid=&username=&cpid=&xmlPath=";
		String cpid = "";
		String userid = "";
		String orgid = "";
		String username = "";
		String liveChannelPath = "";
		String channelId = "";
		String xmlPath = "";
		String param[] = str.split("\\?")[1].split("&");
		System.out.println(str.split("\\?")[1].contains("liveChannel")&&str.split("\\?")[1].contains("channelId"));
		String md5Url = "";
		if(str.split("\\?")[1].contains("liveChannel")&&str.split("\\?")[1].contains("channelId")){
			for(String parameter:param){
				String p = parameter.split("=")[0];
				if(p.equals("cpid")){
					cpid = p;
				}else if(p.equals("userid")){
					userid = p;
				}else if(p.equals("username")){
					username = p;
				}else if(p.equals("orgid")){
					orgid=p;
				}else if(p.equals("channelId")){
					channelId=p;
				}else if(p.equals("liveChannelPath")){
					liveChannelPath=p;
				}
			}
		}else{
			for(String parameter:param){
				String p = parameter.split("=")[0];
				if(p.equals("cpid")){
					cpid = p;
				}else if(p.equals("userid")){
					userid = p;
				}else if(p.equals("username")){
					username = p;
				}else if(p.equals("orgid")){
					orgid=p;
				}else if(p.equals("xmlPath")){
					xmlPath=p;
				}
			}
		}
		System.out.println(md5("cpiduseridusernameorgidxmlPath"));
		System.out.println("节目单"+md5(cpid+userid+username+orgid+channelId+liveChannelPath).equals(md5("cpiduseridusernameorgidchannelIdliveChannelPath")));
		System.out.println("内容"+md5(cpid+userid+username+orgid+xmlPath).equals(md5("cpiduseridusernameorgidxmlPath")));
		/*
		try{
			 File big = new File("D:\\software\\CentOS6.4.iso");
			 
			 String md5 = getFileMD5String(big);
			 System.out.println("********" + md5);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		*/
	}
	
}