package com.wondertek.mam.util.video;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 将视频转换为gif图片的工具类
 * @author ZengMeng
 * @time 2017-4-25
 */
public class Video2GifUtil {
	
	private static final Log log = LogFactory.getLog(Video2GifUtil.class);
	
	/**
	 * 获取视频帧转化为图片
	 * @param startTime     视频转gif开始时间,格式:s/hh:mm:ss
	 * @param duration		转换持续时间,格式:s/hh:mm:ss
	 * @param videoPath		视频文件地址(完整地址)
	 * @param extractFps	抽取视频转化为图片帧率(至少为5合成时才具有动画效果，特殊要求除外)
	 * @param outputWidth	图片宽度
	 * @param outputheight	图片高度
	 * @param outputPath	图片临时存储目录(完整目录地址,操作完成后将被删除)
	 * @param pos 			临时图片存储起始序号
	 * @return 转换是否成功
	 * getImageFromVideo("5", "10", "D:/video_test/gif1/1.mp4", "10", "320", "240",
				"D:/video_test/gif1/tmp",i);
	 */
	public static boolean getImageFromVideo(String startTime, String duration,
			String videoPath, String extractFps, String outputWidth,
			String outputheight, String outputPath,int pos) {
		//验证文件是否存在
		File f1 = new File(videoPath);
		if(!f1.exists()){
			log.info("视频文件不存在!");
//			System.out.println("视频文件不存在!");
			return false;
		}
		//验证临时存储目录是否存在，不存在则创建
		File f2 = new File(outputPath);
		boolean flag = judeDirExists(f2);
		if(!flag){
			f2.mkdirs();
		}
		//截图命令拼接
		String outputfile = outputPath + "/%5d.jpg";
		String cmd = "ffmpeg -y -ss " + startTime + " -t " + duration + " -i "
				+ videoPath + " -r " + extractFps + " -s " + outputWidth + "x"
				+ outputheight + " -f image2 -start_number " + pos + " " + outputfile;
		log.info("Video2Gif 开始视频抽取图片操作...");
//		System.out.println("Video2Gif 开始视频抽取图片操作...");
		flag = execCmd(cmd);
		log.info("Video2Gif 视频抽取图片操作完成!");
//		System.out.println("Video2Gif 视频抽取图片操作完成!");
		return flag;
	}
	
	/**
	 * 将图片转换成gif
	 * @param framerate		gif播放帧率
	 * @param inPath 		抽取图片路径(完整文件路径)   
	 * @param outPath		gif文件存储路径(完整文件路径)
	 * @return 转换是否成功
	 * mergeImage2Gif("12", "D:/video_test/gif1/tmp/%5d.jpg",
				"D:/aaa/bbb/test.gif");
	 */
	public static boolean mergeImage2Gif(String framerate,String inPath,String outPath){
		//判断输出目录是否存在
		String outPathMenu = outPath.substring( 0 , outPath.lastIndexOf("/"));
		File f = new File(outPathMenu);
		boolean flag = judeDirExists(f);
		if(!flag){
			f.mkdirs();
		}
		//生成gif命令拼接
		String cmd = "ffmpeg -y -f image2 -framerate " + framerate + " -i " + inPath + " " + outPath;
		log.info("Video2Gif 开始图片转gif操作...");
//		System.out.println("Video2Gif 开始图片转gif操作...");
		flag = execCmd(cmd);
		log.info("Video2Gif 图片转gif操作完成!");
//		System.out.println("Video2Gif 图片转gif操作完成!");
		return flag;
	}
	
	/**
	 * 删除临时文件目录
	 * @param tmpfile 文件目录(完整目录地址)
	 * @return 删除操作是否成功
	 * deleteTmpDirs("D:/video_test/gif1/tmp")
	 */
	public static boolean deleteTmpDirs(String tmpfile){
		File f = new File(tmpfile);
		boolean flag = judeDirExists(f);
		if(!flag){
			log.info("传入的不是目录或者文件不存在!");
//			System.out.println("传入的不是目录或者文件不存在!");
		}else{
			File[] files = f.listFiles();
			for (File f1 : files) {
				f1.delete();
			}
			f.delete();
		}
		return true;
	}
	
	/**
	 * 判断:1.文件是否存在,2.是否是目录
	 * @param file 目录文件
	 * @return 是否是目录
	 */
	public static boolean judeDirExists(File file) {
		if (file.exists() && file.isDirectory()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获取某个目录中的文件总数(应用于其内不含文件夹的目录)
	 * @param file 目录文件
	 * @return 当前目录中文件总数
	 */
	public static int fileNumber(String file){
		File f = new File(file);
		boolean flag = judeDirExists(f);
		if(!flag){
			log.info("传入的不是目录或者文件不存在!");
//			System.out.println("传入的不是目录或者文件不存在!");
			return -1;//错误码
		}else{
			File[] files = f.listFiles();
			return files.length;
		}
	}
	
	/**
	 * 执行终端命令
	 * @param cmd 命令终端将运行的命令
	 * @return 执行是否顺利完成
	 */
	public static boolean execCmd(String cmd){
		try{
			Runtime rt = Runtime.getRuntime();
			Process proc = null;
			String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                    proc = rt.exec(new String[] { "cmd",
                                    "/C", cmd });
            } else if (os.contains("Linux")) {
                    proc = rt.exec(new String[] { "sh",
                                    "-c", cmd });
            }
			InputStream stderr = proc.getErrorStream();
			InputStreamReader isr = new InputStreamReader(stderr);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			log.info(">>>>>>>>>>>execCmd start!!<<<<<<<<<<<");
//			System.out.println(">>>>>>>>>>>execCmd start!!<<<<<<<<<<<");
			while ((line = br.readLine()) != null)
				log.info(line);
//			System.out.println(line);
			log.info(">>>>>>>>>>>execCmd complete!!<<<<<<<<<<<");
//			System.out.println(">>>>>>>>>>>execCmd complete!!<<<<<<<<<<<");
			return true;
		}catch(Throwable t){
			t.printStackTrace();
			log.info("操作进入异常块!!!");
//			System.out.println("操作进入异常块!!!");
			return false;
		}
	}
	
	/*public static void main(String[] args) {
		Date start = new Date();
		System.out.println("start : " + start);
		// 1.测试图片能否顺利生成
		int i = 0;
		getImageFromVideo("5", "10", "D:/video_test/gif1/1.mp4", "10", "320", "240",
				"D:/video_test/gif1/tmp",i);
		i = fileNumber("D:/video_test/gif1/tmp");
		getImageFromVideo("15", "10", "D:/video_test/gif1/1.mp4", "10", "320", "240",
				"D:/video_test/gif1/tmp",i);
		i = fileNumber("D:/video_test/gif1/tmp");
		getImageFromVideo("25", "10", "D:/video_test/gif1/1.mp4", "10", "320", "240",
				"D:/video_test/gif1/tmp",i);
		i = fileNumber("D:/video_test/gif1/tmp");
		getImageFromVideo("35", "10", "D:/video_test/gif1/1.mp4", "10", "320", "240",
				"D:/video_test/gif1/tmp",i);
		// 2.测试gif能否顺利生成
		mergeImage2Gif("12", "D:/video_test/gif1/tmp/%5d.jpg",
				"D:/aaa/bbb/test.gif");
		// 3.删除之前生成的临时目录
		deleteTmpDirs("D:/video_test/gif1/tmp");
		Date end = new Date();
		System.out.println("end : " + end);
		System.out.println("用时: " + (end.getTime()-start.getTime()) + "ms" );
	}*/
}
