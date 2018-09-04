package com.wondertek.mam.util.backupUtils.redis.redis2.cluster;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class RedisClusterManager {

	private JedisCluster jedisCluster;
	
	public String get(String key){
		return jedisCluster.get(key);
	}
	
	public String set(String key, String value){
		return jedisCluster.set(key, value);
	}
	
	public Long setnx(String key, String value){
		return jedisCluster.setnx(key, value);
	}
	
	public Long del(String key){
		return jedisCluster.del(key);
	}
	
	public Boolean exists(String key){
		return jedisCluster.exists(key);
	}
	
	public Long expire(String key, Integer seconds){
		return jedisCluster.expire(key, seconds);
	}
	
	public Long ttl(String key){
		return jedisCluster.ttl(key);
	}
	
	public Long sadd(String key, String value){
		return jedisCluster.sadd(key, value);
	}
	
	public Set<String> smembers(String key){
		return jedisCluster.smembers(key);
	}
	
	public String spop(String key){
		return jedisCluster.spop(key);
	}
	
	public Long lpush(String key, String value){
		return jedisCluster.lpush(key, value);
	}
	
	public Long rpush(String key, String value){
		return jedisCluster.rpush(key, value);
	}
	
	public String lset(String key, Integer index, String value){
		return jedisCluster.lset(key, index, value);
	}
	
	public Long llen(String key){
		return jedisCluster.llen(key);
	}
	
	public List<String> lrange(String key, Integer start, Integer end){
		return jedisCluster.lrange(key, start, end);
	}
	
	public List<String> lgetAll(String key){
		Long end = jedisCluster.llen(key)-1;
		return jedisCluster.lrange(key, 0, end);
	}
	
	public String rpop(String key){
		return jedisCluster.rpop(key);
	}
	
	public Double zscore(String key, String member){
		return jedisCluster.zscore(key, member);
	}
	
	public Long scard(String key){
		return jedisCluster.scard(key);
	}
	
	public Boolean sismember(String key, String member){
		return jedisCluster.sismember(key, member);
	}
	
	public Set<String> keys(String pattern){
		Set<String> keys = new HashSet<String>();
		Set<Jedis> jedisSet = getJedis();
		for(Jedis jedis : jedisSet) {
			keys.addAll(jedis.keys(pattern));
		}
		return keys;
	}
	
	private Set<Jedis> getJedis() {
		Set<Jedis> jedisSet = new HashSet<Jedis>();
		if(jedisCluster != null && jedisCluster.getClusterNodes() != null) {
			for(String k : jedisCluster.getClusterNodes().keySet()) {
				JedisPool jp = jedisCluster.getClusterNodes().get(k);
				jedisSet.add(jp.getResource());
			}
		}
		return jedisSet;
	}

	public JedisCluster getJedisCluster() {
		return jedisCluster;
	}

	public void setJedisCluster(JedisCluster jedisCluster) {
		this.jedisCluster = jedisCluster;
	}
	
}
