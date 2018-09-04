package com.wondertek.mam.util.video;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 临时文件管理
 * 
 * @author yaoyu
 * @time 2016年1月18日 下午6:46:43
 */
public class TmpFile {
	public ArrayList<File> list = new ArrayList<File>();
	String tmpDir = "";

	public TmpFile(ArrayList<HashMap<String, String>> param0,
			ArrayList<HashMap<String, String>> param1) {
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
	}

	public boolean deletes() {
		for (File file : list) {
			if (file.exists()) {
				file.delete();
			}
		}
		File file = new File(tmpDir.substring(0,tmpDir.length()-1));
		if (file.exists()) {
			file.delete();
		}
		return true;
	}
}
