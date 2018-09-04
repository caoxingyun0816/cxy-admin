package com.wondertek.mam.util.video;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
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
public class VideoMergeBatch {
	private int poolSize = 0;// 线程池大小
	private int taskNum = 0;// 任务数
	private ExecutorService poolService = null;
	public MergeChain chain;

	/**
	 * @param taskNum
	 * @param chain
	 */
	public VideoMergeBatch(int taskNum, MergeChain chain) {
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
					BufferedReader br = null;
					try {
						Process process = null;
						String os = System.getProperty("os.name");
						if (os.contains("Windows")) {
							process = runtime.exec(new String[]{"cmd", "/C", cmd});
						} else {
							process = runtime.exec(new String[]{"sh", "-c", cmd});
						}
						InputStream result = process.getErrorStream();
						br = new BufferedReader(new InputStreamReader(result, "UTF-8"));
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

	public String generateCmd(HashMap<String, String> params) {
		String cmd = "";
		if ("cutVideo".equals(params.get("optype"))) {
			if (!"1.000".equals(params.get("speed"))) {
				DecimalFormat df2 = new DecimalFormat("##0.000");
				Double video_speed = 1 / Double.valueOf(params.get("speed"));
				String str = "setpts=0.5*PTS[v0];atempo=2.0[a0];";
				Double audio_speed = 0.000;
				if (video_speed < 0.5) {
					int num = 0;
					while (video_speed < 0.5) {
						video_speed = video_speed * 2;
						num++;
					}
					audio_speed = 1 / video_speed;
					if (num > 0) {
						for (int i = 1; i < num; i++) {
							str += "[v" + (i - 1) + "]setpts=0.5*PTS[v" + i
									+ "];[a" + (i - 1) + "]atempo=2.0[a" + i
									+ "];";
						}
						str += "[v" + (num - 1) + "]setpts="
								+ df2.format(video_speed) + "*PTS[v];[a"
								+ (num - 1) + "]atempo="
								+ df2.format(audio_speed) + "[a]";
					}
				} else {
					audio_speed = 1 / video_speed;
					str = "setpts=" + df2.format(video_speed)
							+ "*PTS[v];atempo=" + df2.format(audio_speed)
							+ "[a]";
				}
				cmd = "ffmpeg -y -ss " + params.get("start") + " -t "
						+ params.get("duration") + " -i " + params.get("input")
						+ " -c:v libx264 -filter_complex \"" + str
						+ "\" -map \"[v]\" -map \"[a]\" -strict -2 -f mp4 "
						+ params.get("output");
			} else {
				cmd = "ffmpeg -y -ss " + params.get("start") + " -t "
						+ params.get("duration") + " -i " + params.get("input")
						+ " -vcodec copy -acodec copy " + params.get("output");
			}
			System.out.println("param0 : " + cmd);
		}
		if ("packageTs".equals(params.get("optype"))) {
			cmd = "ffmpeg -y -i " + params.get("input")
					+ " -vcodec copy -acodec copy -vbsf h264_mp4toannexb -s 1280*720 "
					+ params.get("output");
			System.out.println("param1 : " + cmd);
		}
		if ("combine".equals(params.get("optype"))) {
			cmd = "ffmpeg -y -i concat:\""
					+ params.get("input")
					+ "\" -vcodec copy -acodec copy -absf aac_adtstoasc -f mp4 "
					+ params.get("output");
			System.out.println("param2 : " + cmd);
		}
		if ("burn".equals(params.get("optype"))) {
			String complex = "";
			String stream = "";
			cmd = "ffmpeg -y -i " + params.get("input");
			if (!"".equals(params.get("audio"))) {
				complex = "[0:a]amix=inputs=2:duration=first:dropout_transition=2[a]";
				cmd += " -i " + params.get("audio");
			}
			if (!"".equals(params.get("watermark"))
					|| !"".equals(params.get("word"))) {
				String ss = "";
				if (!"".equals(params.get("watermark"))) {
					ss = "movie=" + params.get("watermark") + "[wm];";
					stream += "[wm]";
					complex = "overlay=main_w-overlay_w-40:10[v];" + complex;
				}
				if (!"".equals(params.get("word"))) {
					String word = params.get("word");
					if (word.endsWith(".ass")) {
						ss += "ass=" + word + "[word];";
					} else {
						ss += "subtitles=" + word + "[word];";
					}
					stream = "[word]" + stream;
				} else {
					stream = "[0:v]" + stream;
				}
				complex = " -c:v libx264 -filter_complex \"" + ss + stream + complex + "\"";
			}
			if (!"".equals(params.get("audio"))) {
				cmd += complex
						+ " -map \"[v]\" -map \"[a]\" -strict -2 -f mp4 "
						+ params.get("output");
			} else if (complex.equals("")) {
				cmd = "cp " + params.get("input") + " " + params.get("output");
			}
			System.out.println("param3 : " + cmd);
		}
		if ("imgTOvideo".equals(params.get("optype"))) {
			String input = params.get("input");
			String img_input = params.get("output");
			String imgName = input.substring(input.lastIndexOf("/") + 1,
					input.lastIndexOf("."))
					+ "%4d.jpg";
			img_input = img_input.substring(0, img_input.lastIndexOf("/") + 1)
					+ "imgs/" + imgName;
			cmd = "ffmpeg -y -f image2 -i " + img_input + " -r 23.98 -s 1280*720 "
					+ params.get("output");
			System.out.println("paramImg : " + cmd);
		}
		return cmd;
	}
}
