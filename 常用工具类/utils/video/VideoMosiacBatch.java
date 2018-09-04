package com.wondertek.mam.util.video;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 视频编辑批处理
 * 
 * @author yaoyu
 * @time 2016年4月25日 下午1:39:41
 */
public class VideoMosiacBatch {
	private int poolSize = 0;// 线程池大小
	private int taskNum = 0;// 任务数
	private ExecutorService poolService = null;
	public MosiacChain chain;

	/**
	 * @param taskNum
	 * @param chain
	 */
	public VideoMosiacBatch(int taskNum, MosiacChain chain) {
		if (taskNum <= 8) {
			this.poolSize = taskNum;
		} else if (taskNum <= 16) {
			this.poolSize = 8;
		} else {
			this.poolSize = 16;
		}
		this.taskNum = taskNum;
		this.chain = chain;
		poolService = Executors.newFixedThreadPool(this.poolSize);
	}

	public void doTasks(ArrayList<HashMap<String, String>> params) {
		for (int i = 0; i < taskNum; i++) {
			final ArrayList<HashMap<String, String>> param = params;
			poolService.execute(new Runnable() {
				String cmd = generateCmd(param);

				@Override
				public void run() {
					Runtime runtime = Runtime.getRuntime();
					BufferedReader br = null;
					Process process = null;
					try {
						String os = System.getProperty("os.name");
						if (os.contains("Windows")) {
							process = runtime.exec(new String[] { "cmd", "/C", cmd });
						} else if (os.contains("Linux")) {
							process = runtime.exec(new String[] { "sh", "-c", cmd });
						} 
						InputStream result = process.getErrorStream();
						br = new BufferedReader(new InputStreamReader(result,
								"UTF-8"));
						String line = "";
						while ((line = br.readLine()) != null) {
							System.out.println(line);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (br != null) {
							try {
								br.close();
							} catch (IOException e) {
							}
						}
					}
				}
			});
		}
		poolService.shutdown();
		try {
			poolService.awaitTermination(120L, TimeUnit.MINUTES);
			System.out.println(">>>>>>one part complete<<<<<<");
			chain.doChain();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String generateCmd(ArrayList<HashMap<String, String>> params) {
		String cmd = "ffmpeg -y -nhb -i " + params.get(0).get("input")
				+ " -vf [in]";
		for (int i = 0; i < params.size(); i++) {
			if (params.get(i).get("type").equals("0")) {
				cmd += "blackblock=" + params.get(i).get("X") + ":"
						+ params.get(i).get("Y") + ":" + params.get(i).get("W")
						+ ":" + params.get(i).get("H");
			}
			if (params.get(i).get("type").equals("1")) {
				cmd += "mosaic=" + params.get(i).get("X") + ":"
						+ params.get(i).get("Y") + ":" + params.get(i).get("W")
						+ ":" + params.get(i).get("H");
			}
			if (i < params.size() - 1) {
				cmd += "[" + i + "],[" + i + "]";
			} else {
				cmd += "[out] " + params.get(i).get("output");
			}
		}
		System.out.println("mosiac cmd is : " + cmd);
		return cmd;
	}
}
