package com.wondertek.mam.util.video;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONObject;
import com.wondertek.mam.relation.vo.ContentRelation;
import com.wondertek.mam.vo.VideoSubtitleVO;

/**
 * 从视频文件中抽取字幕文件
 * @author zhao
 *
 */
public class VideoDrawSubtitleUtil {
	private static final Log log = LogFactory.getLog(VideoDrawSubtitleUtil.class);
	/**
	 * 判断视频文件中是否有字幕文件
	 * @param videoPath 视频路径
	 * @return List 字幕文件的索引和相应的语言类型
	 */
	public static List<VideoSubtitleVO> hasSubtitleStream(String videoPath){
		//ffprobe PS.mp4 -print_format json -show_streams -select_streams v -hide_banner -v quiet
		//验证文件是否存在
		List<VideoSubtitleVO> subs = new ArrayList<VideoSubtitleVO>();
		int count = 0;//统计字符流的数量
		File f1 = new File(videoPath);
		if(!f1.exists()){
			log.info(videoPath + " 视频文件不存在!");
//			System.out.println("视频文件不存在!");
			return subs;
		}
		//从视频中抽取字幕流信息
		String cmd = "ffprobe " + videoPath + " -print_format json -show_streams -select_streams s -hide_banner -v quiet";
//		System.out.println("---------执行的指令--------" + cmd);
		log.info("---------执行的指令--------" + cmd);
//		boolean flag = true;
		StringBuffer sb = new StringBuffer();
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = null;
			String os = System.getProperty("os.name");
			if (os.contains("Windows")) {
				proc = rt.exec(new String[] { "cmd", "/C", cmd });
			} else if (os.contains("Linux")) {
				proc = rt.exec(new String[] { "sh", "-c", cmd });
			} 
			InputStream result = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(result);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			
			while((line = br.readLine()) != null){
				System.out.println(line);
				sb.append(line);
			}
			
		} catch (Exception e) {
//			System.out.println(e.getMessage());
			log.info(e.getMessage(), e);
			return subs;
		}
		JSONObject jb = JSONObject.parseObject(sb.toString());
		String streams = jb.get("streams").toString().replace("},{", "};{");
		
//		System.out.println("-----------返回信息----------" + streams);
		log.info("-----------返回信息----------" + streams);
		if(null != streams && streams.length() > 2){
			streams = streams.substring(1, streams.length() - 1);
			String[] stream = streams.split(";");
			count = stream.length;
			for(int i=0; i<count; i++){
				JSONObject jb2 = JSONObject.parseObject(stream[i]);
				String tags = jb2.get("tags").toString();
//				System.out.println(tags);
				VideoSubtitleVO vs = (VideoSubtitleVO)JSONObject.parseObject(tags, VideoSubtitleVO.class);
				vs.setIndex(i);
				subs.add(vs);
			}
		}else {
//			System.out.println("该视频不存在字幕流！！！！");
			log.info("该视频不存在字幕流！！！！");
		}
//		System.out.println("-------subs.size()------------" + subs.size());
		log.info("-------subs.size()------------" + subs.size());
		int i=0;
		for (VideoSubtitleVO vvo : subs) {
//			System.out.println("-----language" + i++ + "-------"+ vvo.getIndex() + "-----name-----" + vvo.getLanguage());
			log.info("-----language" + i++ + "-------"+ vvo.getIndex() + "-----name-----" + vvo.getLanguage());
		}
		return subs;
	}
	
	/**
	 * 获取视频中的字幕文件
	 * @param videoPath 视频路径
	 * @param outPath 字幕文件输出路径
	 * @param ind 输出指定的字幕文件索引
	 * @return Boolean型数据
	 */
	public static boolean getSubtitleFromVideo(String videoPath, String outPath, List<Integer> ind){ 
		boolean flag = false;
		List<VideoSubtitleVO> subs = hasSubtitleStream(videoPath);
		if(0 == subs.size()){
			log.info("视频不存在字幕流信息");
//			System.out.println("视频不存在字幕流信息");
			return false;
		}
		File file = new File(outPath);
		if (!file.exists() || !file.isDirectory()){
			file.mkdirs();
		}
		String fileName = file.getName();
		String outputPath = "";
		String cmd = "";
		if(null == ind || ind.size() == 0){//没有指定哪个字幕文件，则输出所有的字幕文件
			for(int i=0; i<subs.size(); i++){
				int index = subs.get(i).getIndex();
				outputPath = outPath + "/"+fileName+"_sub_" + index + "_" + subs.get(i).getLanguage() + ".srt";//+ subs.get(i).getName() + "_" + subs.get(i).getLanguage() 
				cmd = "ffmpeg -i " + videoPath + " -map 0:s:" + i + " " + outputPath;
				
				log.info("获取第"+ (i+1) +"个字符流的命令" + cmd);
//				System.out.println("获取第"+ (i+1) +"个字符流的命令" + cmd);
				flag = execCmd(cmd);
//				log.info("第" + i + "个字幕流获取成功：" + flag);
				if(flag){
//					System.out.println("----------第" + (i+1) + "个字幕流获取成功----------");
					log.info("----------第" + (i+1) + "个字幕流获取成功----------");
				}else{
//					System.out.println("----------第" + (i+1) + "个字幕流获取失败----------");
					log.info("----------第" + (i+1) + "个字幕流获取失败----------");
				}
			}
		}else {//输出指定索引的字幕文件
			for (int i = 0; i < ind.size(); i++) {
				outputPath = outPath + "/sub_" + ind.get(i) + "_" + subs.get(ind.get(i)).getLanguage() + ".srt";//+ subs.get(i).getName() + "_" + subs.get(i).getLanguage() 
				cmd = "ffmpeg -i " + videoPath + " -map 0:s:" + ind.get(i) + " " + outputPath;
				log.info(cmd);
				flag = execCmd(cmd);
				if(flag){
//					System.out.println("----------第" + (i+1) + "个字幕流获取成功----------");
					log.info("----------第" + (i+1) + "个字幕流获取成功----------");
				}else{
//					System.out.println("----------第" + (i+1) + "个字幕流获取失败----------");
					log.info("----------第" + (i+1) + "个字幕流获取失败----------");
				}
			}
		}
		return flag;
	}
	
	
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
			while ((line = br.readLine()) != null){
				log.info(line);
//				System.out.println(line);
			}
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
//	public static void main(String[] args) {
//		List<VideoSubtitleVO> vs = hasSubtitleStream("F:/video/800033_muxingshangxing_sc99_01.mkv");
//		if(vs.size() > 0)
//		System.out.println(vs.size()+ "  " + vs.get(0).getLanguage());
//		List<Integer> list = new ArrayList<Integer>();
//		System.out.println(list.size());
//		list.add(0);
//		list.add(1);
//		list.add(2);
//		list.add(3);
//		list.add(4);
//		list.add(5);
//		list.add(6);
//		System.out.println(getSubtitleFromVideo("F:/video/800033_muxingshangxing_sc99_01.mkv", "F:/video/temp", list));
//	}
}
