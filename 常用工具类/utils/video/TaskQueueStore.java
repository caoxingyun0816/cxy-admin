package com.wondertek.mam.util.video;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskQueueStore {
	//存储任务队列，每种任务队列只初始化一次
	@SuppressWarnings("rawtypes")
	public static HashMap<String, LinkedBlockingQueue> storemap = new HashMap<String, LinkedBlockingQueue>();
}
