package com.wondertek.mam.util.video;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 责任链
 * 
 * @author yaoyu
 * @time 2016年1月18日 下午1:59:22
 */
public class Chain {
	private ArrayList<HashMap<String, String>> param0 = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> param1 = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> param2 = new ArrayList<HashMap<String, String>>();
	private VideoCutBatch vtp = null;
	private TmpFile tmpFile = null;
	private int step = 0;

	// 视频编辑参数初始化
	// 临时文件管理初始化
	public Chain(ArrayList<HashMap<String, String>> param0,
			ArrayList<HashMap<String, String>> param1,
			ArrayList<HashMap<String, String>> param2) {
		this.param0 = param0;
		this.param1 = param1;
		this.param2 = param2;
		tmpFile = new TmpFile(param0, param1);
	}

	public String doChain() {
		if (step == 0) {
			// 剪切
			step++;
			vtp = new VideoCutBatch(this.param0.size(), this);
			vtp.doTasks(param0);
		} else if (step == 1) {
			// 封装ts
			step++;
			vtp = new VideoCutBatch(this.param0.size(), this);
			vtp.doTasks(param1);
		} else if (step == 2) {
			// 合并
			step++;
			vtp = new VideoCutBatch(1, this);
			vtp.doTasks(param2);
		} else if (step == 3) {
			// 删除临时文件
			tmpFile.deletes();
		} else {
			return "failure";
		}
		return step == 3 ? "success" : "failure";
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public ArrayList<HashMap<String, String>> getParam2() {
		return param2;
	}

	public void setParam2(ArrayList<HashMap<String, String>> param2) {
		this.param2 = param2;
	}
}
