package com.wondertek.mam.util.video;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class AbstractTaskQueueController<T> {

	public String type;

	// 任务线程模板
	private class TheadTemplate extends Thread {
		@Override
		public void run() {
			while (TaskQueueStore.storemap.get(type).size() > 0) {
				doit();
			}
		}
	}

	public abstract void doit();

	// 加入任务队列
	@SuppressWarnings("unchecked")
	public void add(T e) {
		try {
			if (!TaskQueueStore.storemap.containsKey(type)) {
				TaskQueueStore.storemap.put(type, new LinkedBlockingQueue<T>(
						1024));
			}
			TaskQueueStore.storemap.get(type).put(e);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	// 获取责任链
	@SuppressWarnings("unchecked")
	public T getchain() {
		T result = null;
		try {
			result = (T) TaskQueueStore.storemap.get(type).poll(2,
					TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}

	// 控制流程
	public synchronized void control() {
		TheadTemplate thread = new TheadTemplate();
		if (TaskQueueStore.storemap.get(type).size() > 0 && !thread.isAlive()) {
			thread.start();
		}
	}

	// 调用servlet
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
}
