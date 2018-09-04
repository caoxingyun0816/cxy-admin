package com.wondertek.mam.util.video;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wondertek.mam.commons.MamConstants;

public class TaskQueue {
	public static LinkedBlockingQueue<Wrapper<MergeChain>> merge_task_queue = new LinkedBlockingQueue<Wrapper<MergeChain>>(
			1024);
	public static LinkedBlockingQueue<Wrapper<Chain>> cut_task_queue = new LinkedBlockingQueue<Wrapper<Chain>>(
			1024);
	public static LinkedBlockingQueue<Wrapper<Chain>> extract_task_queue = new LinkedBlockingQueue<Wrapper<Chain>>(
			1024);
	public static LinkedBlockingQueue<Wrapper<MosiacChain>> mosiac_task_queue = new LinkedBlockingQueue<Wrapper<MosiacChain>>(
			1024);

	private static class TheadTemplate_Mosiac extends Thread {
		protected final Log log = LogFactory.getLog(getClass());

		@Override
		public void run() {
//			while (mosiac_task_queue.size() > 0) {
			while (!mosiac_task_queue.isEmpty()) {
				Wrapper<MosiacChain> wrapper = get_mosiac_chain();
				if (wrapper.chain != null) {
					String ret = wrapper.chain.doChain();
					int status = 3;
					if(!"success".equals(ret)){
						status = 4;
					}
					log.debug("dochain ret:" + ret + ", chain:" + wrapper.chain);
					String output = wrapper.chain.getParam0().get(0).get("output");
					ServletUtil(wrapper.ip + "/acceptMessageServer?optype=mosiacVideo&id="
							+ wrapper.id + "&status=" + status + "&output=" + output);
				}
			}
		}
	}
	
	private static class TheadTemplate_Cut extends Thread {
		protected final Log log = LogFactory.getLog(getClass());

		@Override
		public void run() {
//			while (cut_task_queue.size() > 0) {
			while (!cut_task_queue.isEmpty()) {
				Wrapper<Chain> wrapper = get_cut_chain();
				if (wrapper.chain != null) {
					String ret = wrapper.chain.doChain();
					int status = 3;
					log.debug("dochain ret:" + ret + ", chain:" + wrapper.chain);
					String output = wrapper.chain.getParam2().get(0).get("output");
					if(!"success".equals(ret)){
						status = 4;
					}
					ServletUtil(wrapper.ip + "/acceptMessageServer?optype=cutVideo&id="
							+ wrapper.id + "&status=" + status + "&output=" + output);
				}
			}
		}
	}
	
	private static class TheadTemplate_Extract extends Thread {
		protected final Log log = LogFactory.getLog(getClass());

		@Override
		public void run() {
//			while (extract_task_queue.size() > 0) {
			while (!extract_task_queue.isEmpty()) {
				Wrapper<Chain> wrapper = get_extract_chain();
				if (wrapper.chain != null) {
					String ret = wrapper.chain.doChain();
					int status = 3;
					if(!"success".equals(ret)){
						status = 4;
					}
					log.debug("dochain ret:" + ret + ", chain:" + wrapper.chain);
					String output = wrapper.chain.getParam2().get(0).get("output");
					ServletUtil(wrapper.ip + "/acceptMessageServer?optype=extractVideo&id="
							+ wrapper.id + "&status=" + status + "&output=" + output);
				}
			}
		}
	}
	
	private static class TheadTemplate_Merge extends Thread {
		protected final Log log = LogFactory.getLog(getClass());

		@Override
		public void run() {
//			while (merge_task_queue.size() > 0) {
			while (!merge_task_queue.isEmpty()) {
				Wrapper<MergeChain> wrapper = get_merge_chain();
				if (wrapper.chain != null) {
					String ret = wrapper.chain.doChain();
					int status = 3;
					if(!"success".equals(ret)){
						status = 4;
					}
					log.debug("dochain ret:" + ret + ", chain:" + wrapper.chain);
					ServletUtil(wrapper.ip + "/acceptMessageServer?optype=mergeVideos&id="
							+ wrapper.id + "&status=" + status);
				}
			}
		}
	}

	public static void add_merge(Wrapper<MergeChain> e) {
		try {
			merge_task_queue.put(e);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	public static void add_mosiac(Wrapper<MosiacChain> e) {
		try {
			mosiac_task_queue.put(e);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public static void add_cut(Wrapper<Chain> e) {
		try {
			cut_task_queue.put(e);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public static void add_extract(Wrapper<Chain> e) {
		try {
			extract_task_queue.put(e);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public static Wrapper<MergeChain> get_merge_chain() {
		Wrapper<MergeChain> result = null;
		try {
			result = merge_task_queue.poll(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Wrapper<Chain> get_cut_chain() {
		Wrapper<Chain> result = null;
		try {
			result = cut_task_queue.poll(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Wrapper<Chain> get_extract_chain() {
		Wrapper<Chain> result = null;
		try {
			result = extract_task_queue.poll(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Wrapper<MosiacChain> get_mosiac_chain() {
		Wrapper<MosiacChain> result = null;
		try {
			result = mosiac_task_queue.poll(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String ServletUtil(String url) {
		PrintWriter out = null;
		BufferedReader in = null;
		String readTmp = "";
		try {
			URL realUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) realUrl
					.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("charset", "UTF-8");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求前设置
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print("");
			// flush输出流的缓冲
			out.flush();
			// 读取URL响应
			in = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			readTmp = in.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return readTmp;
	}

	public synchronized static void merge_control() {
		TheadTemplate_Merge merge_thread = new TheadTemplate_Merge();
		if (merge_task_queue.size() > 0 && !merge_thread.isAlive()) {
			merge_thread.start();
		}
	}

	public synchronized static void cut_control() {
		TheadTemplate_Cut cut_thread = new TheadTemplate_Cut();
		if (cut_task_queue.size() > 0 && !cut_thread.isAlive()) {
			cut_thread.start();
		}
	}
	
	public synchronized static void mosiac_control() {
		TheadTemplate_Mosiac mosiac_thread = new TheadTemplate_Mosiac();
		if (mosiac_task_queue.size() > 0 && !mosiac_thread.isAlive()) {
			mosiac_thread.start();
		}
	}

	public synchronized static void extract_control() {
		TheadTemplate_Extract extract_thread = new TheadTemplate_Extract();
		if (extract_task_queue.size() > 0 && !extract_thread.isAlive()) {
			extract_thread.start();
		}
	}

}
