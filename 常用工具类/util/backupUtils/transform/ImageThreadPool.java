package com.wondertek.mam.util.backupUtils.transform;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ImageThreadPool {
	static int corePoolSize = 30;
	static int maximumPoolSize = 50;
	static int keepAliveTime = 20;
	public static ThreadPoolExecutor workers;
	static int QueueSize = Integer.MAX_VALUE;
	
//	public static Log log = LogFactory.getLog(ImageThreadPool.class);
	
	public static void init() {
		try {
			corePoolSize = 30;
		} catch (Throwable e) {
			corePoolSize = 30;
		}
		BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(
				QueueSize);
		RejectedExecutionHandler handler = new RejectedExecutionHandler() {
			public void rejectedExecution(Runnable r,
					ThreadPoolExecutor executor) {
				r.run();
			}
		};

		if (workers == null) {
			workers = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
					keepAliveTime, TimeUnit.SECONDS, workQueue, handler);
		}
	}

	public synchronized static void addTask(Task task) {
		if (workers == null)
			init();
//		log.debug("addTask...........");
		workers.execute(new TaskThread(task));
	}
}

class TaskThread implements Runnable {

	private Task task;
	
	public TaskThread(Task task) {
		this.task=task;
	}

	public void run() {
		try {
			task.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}