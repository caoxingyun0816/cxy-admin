package com.wondertek.mam.util.video;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 责任链
 * 
 * @author yaoyu
 * @time 2016年4月25日 下午1:39:22
 */
public class MergeChain {
	private ArrayList<HashMap<String, String>> param0 = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> param1 = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> param2 = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> param3 = new ArrayList<HashMap<String, String>>();
	private VideoMergeBatch vmp = null;
	private MergeTmpFile mergeTmpFile = null;
	private int step = 0;
	private int pc = 0;

	// 视频编辑参数初始化
	// 临时文件管理初始化
	public MergeChain(ArrayList<HashMap<String, String>> param0,
			ArrayList<HashMap<String, String>> param1,
			ArrayList<HashMap<String, String>> param2,
			ArrayList<HashMap<String, String>> param3) {
		this.param0 = param0;
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;
		mergeTmpFile = new MergeTmpFile(param0, param1, param2, param3);
	}

	public String doChain() {
		if (step == 0) {// 图片批量拷贝+图片转视频+剪切片段
			step++;
			int numbers = 0;
			List<String> source = new ArrayList<String>();
			List<String> destination = new ArrayList<String>();
			for (HashMap<String, String> map : param0) {
				if (map.get("type").equals("img")) {
					System.out.println("Enter doChain()-->pc++");
					pc++;
					source.add(map.get("input"));
					destination.add(map.get("output"));
					String time[] = map.get("duration").split(":");
					int seconds = Integer.parseInt(time[0]) * 3600
							+ Integer.parseInt(time[1]) * 60
							+ Integer.parseInt(time[2]);
					numbers = seconds * 25;
				}
			}
			if(pc > 0) {
				for (int i = 0; i < pc; i++) {
					mergeTmpFile.copyimgs(numbers, source.get(i), destination.get(i));
				}
			}
			vmp = new VideoMergeBatch(this.param0.size(), this);
			vmp.doTasks(param0);
		} else if (step == 1) {// 封装ts
			step++;
			vmp = new VideoMergeBatch(this.param1.size(), this);
			vmp.doTasks(param1);
		} else if (step == 2) {// 拼接片段
			step++;
			vmp = new VideoMergeBatch(this.param2.size(), this);
			vmp.doTasks(param2);
		} else if (step == 3) {// 合并(烧录)
			step++;
			vmp = new VideoMergeBatch(this.param3.size(), this);
			vmp.doTasks(param3);
		} else if (step == 4) {
			// 删除临时文件
			mergeTmpFile.deletes();
		}
		return step == 4 ? "success" : "failure";
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}
}
