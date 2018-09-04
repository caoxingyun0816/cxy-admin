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
 * @time 2016年1月15日 下午2:15:41
 */
public class VideoCutBatch {
	private int poolSize = 0;// 线程池大小
	private int taskNum = 0;// 任务数
	private ExecutorService poolService = null;
	public Chain chain;

	/**
	 * @param taskNum
	 * @param chain
	 */
	public VideoCutBatch(int taskNum, Chain chain) {
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
			final HashMap<String, String> map = params.get(i);
			poolService.execute(new Runnable() {
				String cmd = generateCmd(map);
				@Override
				public void run() {
					Runtime runtime = Runtime.getRuntime();
					try {
						Process process = null;
						String os = System.getProperty("os.name");
						if (os.contains("Windows")) {
							process = runtime.exec(new String[]{"cmd", "/C", cmd});
						} else {
							process = runtime.exec(new String[]{"sh", "-c", cmd});
						}
						InputStream result = process.getErrorStream();
						InputStreamReader reader = new InputStreamReader(result);
						BufferedReader br = new BufferedReader(reader);
						String line = "";
						while ((line = br.readLine()) != null) {
							System.out.println(line);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
		poolService.shutdown();
		try {
			poolService.awaitTermination(60L, TimeUnit.MINUTES);
			System.out.println(">>>>>>one part complete<<<<<<");
			chain.doChain();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public String generateCmd(HashMap<String, String> params) {
		String cmd = "";
		if ("cutVideo".equals(params.get("type"))) {
			cmd = "ffmpeg -y -ss " + params.get("start") + " -t "
					+ params.get("duration") + " -i " + params.get("input")
					+ " -vcodec copy -acodec copy " + params.get("output");
			System.out.println("cutVideo : " + cmd);
		}
		if ("packageTs".equals(params.get("type"))) {
			cmd = "ffmpeg -y -i " + params.get("input")
					+ " -vcodec copy -acodec copy -vbsf h264_mp4toannexb "
					+ params.get("output");
			System.out.println("packageTs : " + cmd);
		}
		if ("combine".equals(params.get("type"))) {
			cmd = "ffmpeg -y -i concat:\""
					+ params.get("input")
					+ "\" -vcodec copy -acodec copy -absf aac_adtstoasc -f mp4 "
					+ params.get("output");
			System.out.println("combine : " + cmd);
		}
		return cmd;
	}
}
