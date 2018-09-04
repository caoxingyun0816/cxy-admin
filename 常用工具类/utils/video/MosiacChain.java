package com.wondertek.mam.util.video;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 责任链
 * 
 * @author yaoyu
 * @time 2016年4月25日 下午1:39:22
 */
public class MosiacChain {
	private ArrayList<HashMap<String, String>> param0 = new ArrayList<HashMap<String, String>>();
	private VideoMosiacBatch vmp = null;
	private int step = 0;

	// 视频编辑参数初始化
	public MosiacChain(ArrayList<HashMap<String, String>> param0) {
		this.param0 = param0;
	}

	public String doChain() {
		if (step == 0) {
			step++;
			vmp = new VideoMosiacBatch(this.param0.size(), this);
			vmp.doTasks(param0);
		}
		return step == 1 ? "success" : "failure";
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public ArrayList<HashMap<String, String>> getParam0() {
		return param0;
	}

	public void setParam0(ArrayList<HashMap<String, String>> param0) {
		this.param0 = param0;
	}
}
