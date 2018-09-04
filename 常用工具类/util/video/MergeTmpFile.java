package com.wondertek.mam.util.video;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 临时文件管理
 * 
 * @author yaoyu
 * @time 2016年4月25日 下午1:39:43
 */
public class MergeTmpFile {
	public ArrayList<File> list = new ArrayList<File>();
	String tmpDir = "";

	public MergeTmpFile(ArrayList<HashMap<String, String>> param0,
			ArrayList<HashMap<String, String>> param1,
			ArrayList<HashMap<String, String>> param2,
			ArrayList<HashMap<String, String>> param3) {
		tmpDir = param0.get(0).get("output");
		tmpDir = tmpDir.substring(0, tmpDir.lastIndexOf("/") + 1);
		System.out.println("tmpDir : " + tmpDir);
		File tmpFile = new File(tmpDir);
		if (!tmpFile.exists()) {
			tmpFile.mkdirs();
		}
		for (HashMap<String, String> map : param0) {
			File file = new File(map.get("output"));
			list.add(file);
		}
		for (HashMap<String, String> map : param1) {
			File file = new File(map.get("output"));
			list.add(file);
		}
		for (HashMap<String, String> map : param2) {
			File file = new File(map.get("output"));
			list.add(file);
			File dir = new File(map.get("output").substring(0, map.get("output").lastIndexOf("/")));
			if(!dir.exists()){
				dir.mkdirs();
			}
		}
		for (HashMap<String, String> map : param3) {
			File dir = new File(map.get("output").substring(0, map.get("output").lastIndexOf("/")));
			if(!dir.exists()){
				dir.mkdirs();
			}
		}
	}

	public boolean deletes() {
		for (File file : list) {
			if (file.exists()) {
				file.delete();
			}
		}
		File root = new File(tmpDir + "imgs/");
		if(root.exists()) {
			File[] files = root.listFiles();
			for (File f : files) {
				f.delete();
			}
			root.delete();
		}
		File file = new File(tmpDir);
		if (file.exists()) {
			File[] files = file.listFiles();
			for (File f : files) {
				f.delete();
			}
			file.delete();
		}
		return true;
	}

	public void copyimgs(int numbers, String source, String destination) {
		File fileIn = new File(source);
		String imgName = source.substring(source.lastIndexOf("/") + 1,
				source.lastIndexOf("."));
		for (int i = 0; i < numbers; i++) {
			String s = "";
			if (i < 10) {
				s = "000" + i;
			} else if (i < 100) {
				s = "00" + i;
			} else if (i < 1000) {
				s = "0" + i;
			} else {
				s = "" + i;
			}
			String copyPath = destination.substring(0,
					destination.lastIndexOf("/"))
					+ "/imgs/";
			System.out.println("copyPath : " + copyPath);
			File dir = new File(copyPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File fileOut = new File(copyPath + imgName + s + ".jpg");
			try {
				this.copy(fileIn, fileOut);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void copy(File fileIn, File fileOut) throws IOException {
		FileInputStream fi = null;
		FileOutputStream fo = null;
		FileChannel in = null;
		FileChannel out = null;
		try {
			fi = new FileInputStream(fileIn);
			fo = new FileOutputStream(fileOut);
			in = fi.getChannel();// 得到对应的文件通道
			out = fo.getChannel();// 得到对应的文件通道
			in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fi != null)
					fi.close();
				if (in != null)
					in.close();
				if (fo != null)
					fo.close();
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
