package com.wondertek.mam.util.backupUtils.util22.cache.memcached;

import java.util.Date;
import java.util.Map;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class Memcached {

	private MemCachedClient mc;
	private String poolName = "memcached";

	private String[] servers = {};
	
	private int poolMultiplier = 3;
	private int initConn = 8;
	private int minConn = 8;
	private int maxConn = 64;

	/**
	 * max idle time for avail sockets ,default 5 minitues
	 */
	private long maxIdle = 1000 * 60 * 5;

	/**
	 * // max idle time for avail sockets, default 30 seconds
	 */
	private long maxBusyTime = 1000 * 30;

	/**
	 * maintenance thread sleep time
	 */
	private long maintSleep = 1000 * 5;

	/**
	 * 3 seconds to block on reads
	 */
	private int socketTimeOut = 1000 * 3;

	/**
	 * 300 milliseconds to block on initial connections. If 0, then will use
	 * blocking connect (default)
	 */
	private int socketConnectTO = 100 * 3;

	/**
	 * turn off auto-failover in event of server down ,default true
	 */
	private boolean failover = true;

	/**
	 * turn off Nagle's algorithm on all sockets in pool, ,default false
	 */
	private boolean nagleAlg = false;

	/**
	 * disable health check of socket on checkout,default true.
	 */
	private boolean aliveCheck = true;

	public void init() {
		SockIOPool pool = SockIOPool.getInstance(poolName);
		pool.setServers(servers);
		pool.setInitConn(initConn);
		pool.setMinConn(minConn);
		pool.setMaxConn(maxConn);

		pool.setSocketConnectTO(socketConnectTO);
		pool.setSocketTO(socketTimeOut);

		pool.setMaintSleep(maintSleep);
		pool.setFailover(failover);
		pool.setNagle(nagleAlg);
		pool.setHashingAlg(SockIOPool.NEW_COMPAT_HASH);
		pool.setAliveCheck(aliveCheck);
		pool.initialize();

		mc = new MemCachedClient(poolName);

	}

	public void shutDown() {
		SockIOPool pool = SockIOPool.getInstance(poolName);
		if (pool != null && pool.isInitialized())
			pool.shutDown();
	}

	/**
	 * 返回memcached状态
	 * */
	public Map<String, Map<String, String>> status() {
		return mc.stats();
	}

	public boolean delete(String key) {
		return mc.delete(key, null, null);
	}

	public boolean delete(String key, Date expiry) {
		return mc.delete(key, null, expiry);
	}

	public boolean delete(String key, Integer hashCode, Date expiry) {
		return mc.delete(key, hashCode, expiry);
	}

	public boolean set(String key, Object value) {
		return mc.set(key, value);
	}

	public boolean set(String key, Object value, Integer hashCode) {
		return mc.set(key, value, hashCode);
	}

	public boolean set(String key, Object value, Date expiry) {
		return mc.set(key, value, expiry);
	}

	public boolean set(String key, Object value, Date expiry, Integer hashCode) {
		return mc.set(key, value, expiry, hashCode);
	}

	public Object get(String key) {
		return mc.get(key);
	}

	public Object[] get(String[] keys) {
		return mc.getMultiArray(keys);
	}

	public boolean flushAll() {
		return mc.flushAll();
	}

	public boolean flushAll(String[] keys) {
		return mc.flushAll(keys);
	}

	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public String[] getServers() {
		return servers;
	}

	public void setServers(String[] servers) {
		this.servers = servers;
	}

	public int getPoolMultiplier() {
		return poolMultiplier;
	}

	public void setPoolMultiplier(int poolMultiplier) {
		this.poolMultiplier = poolMultiplier;
	}

	public int getInitConn() {
		return initConn;
	}

	public void setInitConn(int initConn) {
		this.initConn = initConn;
	}

	public int getMinConn() {
		return minConn;
	}

	public void setMinConn(int minConn) {
		this.minConn = minConn;
	}

	public int getMaxConn() {
		return maxConn;
	}

	public void setMaxConn(int maxConn) {
		this.maxConn = maxConn;
	}

	public long getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(long maxIdle) {
		this.maxIdle = maxIdle;
	}

	public long getMaxBusyTime() {
		return maxBusyTime;
	}

	public void setMaxBusyTime(long maxBusyTime) {
		this.maxBusyTime = maxBusyTime;
	}

	public long getMaintSleep() {
		return maintSleep;
	}

	public void setMaintSleep(long maintSleep) {
		this.maintSleep = maintSleep;
	}

	public int getSocketTimeOut() {
		return socketTimeOut;
	}

	public void setSocketTimeOut(int socketTimeOut) {
		this.socketTimeOut = socketTimeOut;
	}

	public int getSocketConnectTO() {
		return socketConnectTO;
	}

	public void setSocketConnectTO(int socketConnectTO) {
		this.socketConnectTO = socketConnectTO;
	}

	public boolean isFailover() {
		return failover;
	}

	public void setFailover(boolean failover) {
		this.failover = failover;
	}

	public boolean isNagleAlg() {
		return nagleAlg;
	}

	public void setNagleAlg(boolean nagleAlg) {
		this.nagleAlg = nagleAlg;
	}

	public boolean isAliveCheck() {
		return aliveCheck;
	}

	public void setAliveCheck(boolean aliveCheck) {
		this.aliveCheck = aliveCheck;
	}
}
