package com.wondertek.mam.util.others;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.wondertek.mam.cache.SystemConfigCache;
import com.wondertek.mam.commons.MamConstants;

public class FfmpegUtil {
	public static final String FFMPEG_PATH = SystemConfigCache.getValue(MamConstants.FFMPEG_PATH);;
	
	public static boolean cutImg(String path,String imgPath,int offset,String width,String height) {
		String os = System.getProperty("os.name");
		if (os.contains("Windows")) {
			return doExe(path, imgPath, offset, width, height);
		} else if (os.contains("Linux")) {
			return doShell(path, imgPath, offset, width, height);
		}
		return false;
	}

	public static boolean doShellBatch(String path,String imgPath, final int[] offset,final String width,final String height) {
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(16);
		for (int i = 0; i < offset.length; i++) {
			final String newName = imgPath + File.separator + offset[i];
			final String pt = path;
			final int count = i;
			fixedThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						final String command = "ffmpeg -y -ss " + offset[count]
								+ " -i " + pt
								+ " -s "+width+"x"+height+" -frames 1 -f image2 " + newName;
						Runtime rt = Runtime.getRuntime();
						Process proc = null;
						String os = System.getProperty("os.name");
			            if (os.contains("Windows")) {
			                    proc = rt.exec(new String[] { "cmd",
			                                    "/C", command });
			            } else if (os.contains("Linux")) {
			                    proc = rt.exec(new String[] { "sh",
			                                    "-c", command });
			            }
//						Process proc = rt.exec(command);
						InputStream stderr = proc.getErrorStream();
						InputStreamReader isr = new InputStreamReader(stderr);
						BufferedReader br = new BufferedReader(isr);
						String line = null;
						while ((line = br.readLine()) != null) {
							System.out.println(line);
						}
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
			});
		}
		fixedThreadPool.shutdown();
		try {
			fixedThreadPool.awaitTermination(60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean doShell(String path,String imgPath,int offset,String width,String height) {
		String command = "ffmpeg -y -ss " + offset + " -i " + path
				+ " -s "+width+"x"+height+" -frames 1 -f image2 " + imgPath;
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = null;
			String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                    proc = rt.exec(new String[] { "cmd",
                                    "/C", command });
            } else if (os.contains("Linux")) {
                    proc = rt.exec(new String[] { "sh",
                                    "-c", command });
            }
//			Process proc = rt.exec(command);
			InputStream stderr = proc.getErrorStream();
			InputStreamReader isr = new InputStreamReader(stderr);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null)
				System.out.println(line);
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean doExe(String path,String imgPath,int offset,String width,String height) {
		if(path != null) {
//			String comm = "cmd /c start " + FFMPEG_BAT_PATH + " " + path + " " 
//				+ offset + " " + width + "*" + height + " " + imgPath;
			
				String[] comm = new String[18];
			comm[0] = FFMPEG_PATH;
			comm[1] = "-ss";
			comm[2] = offset+"";
			comm[3] = "-i";
			comm[4] = path;
			comm[5] = "-vframes";
			comm[6] = "1";
			comm[7] = "-r";
			comm[8] = "1";
			comm[9] = "-ac";
			comm[10] = "1";
			comm[11] = "-ab";
			comm[12] = "2";
			comm[13] = "-s";
			comm[14] = width+"*"+height;
			comm[15] = "-f";
			comm[16] = "image2";
			comm[17] = imgPath;

			try {
				exeOSCommond(comm);
			} catch (Exception e) {
				e.printStackTrace();
				
				return false;
			}
			return true;
		}
		return false;
	}
	
	public static void exeOSCommond(String[] comm) throws Exception{
		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(comm);
			builder.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}