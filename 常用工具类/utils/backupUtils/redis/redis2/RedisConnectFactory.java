package com.wondertek.mam.util.backupUtils.redis.redis2;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConnectFactory {

	private JedisPoolConfig config = new JedisPoolConfig();  
	
	private String serverIp="192.168.1.38";
	private int port=6379;
	private JedisPool pool;  
	
	public void init(){
        pool = new JedisPool(config,serverIp,port); 
	}
	
	public Jedis getInstance(){
		return pool.getResource();
	}
	
	public void returnResource(Jedis jedis){
		pool.returnResource(jedis);
	}
	
	public JedisPoolConfig getConfig() {
		return config;
	}

	public void setConfig(JedisPoolConfig config) {
		this.config = config;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	
}
