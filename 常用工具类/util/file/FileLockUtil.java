package com.wondertek.mam.util.file;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

/**
 * 文件锁工具
 * 
 * 
 * 注意：基于此此文件锁的实现方式，锁只存在单台服务器的内存中。所以要求多线程上传时，前端负载服务器必须支持session跟踪。
 * 
 * @author <a href="mailto:yushenke@wondertek.com.cn">Ken Yu</a>
 * @version 1.0
 * 
 *          Create on 2009-12-15 下午05:38:28
 */
public class FileLockUtil {

	private static final Logger log = Logger.getLogger(FileLockUtil.class);

	private ConcurrentMap<String, String> fileLockMap = new ConcurrentHashMap<String, String>();

	public static FileLockUtil getInstance() {
		return new FileLockUtil();
	}

	/**
	 * 取锁
	 * 
	 * @param lockId
	 * @return
	 * 		0成功，非0失败
	 */
	public int getLock(String lockId) {
		while (true) {
			//锁机制
			byte[] lock = new byte[0];
			synchronized (lock) {
				if (fileLockMap.get(lockId) == null) {
					fileLockMap.put(lockId, "true");
					break;
				}
			}
			//未取得锁，睡一会
			try {
				Thread.sleep(100);
				log.info("thread sleep while waiting lock : zzZ~");
			} catch (InterruptedException e) {
				log.error("thread sleep is interrupted:", e);
				return -1;
			}
		}
		return 0;
	}

	/**
	 * 解锁
	 * 
	 * @param lockId
	 */
	public void releaseLock(String lockId) {
		fileLockMap.remove(lockId);
	}
}
