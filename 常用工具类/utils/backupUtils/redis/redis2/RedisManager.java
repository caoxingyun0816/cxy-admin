package com.wondertek.mam.util.backupUtils.redis.redis2;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wondertek.mobilevideo.core.util.redis.RedisConnectFactory;
import redis.clients.jedis.DebugParams;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisMonitor;
import redis.clients.jedis.JedisPubSub;

public class RedisManager {

	private RedisConnectFactory redisConnectFactory;
	
	public Jedis getJedis(){
		return redisConnectFactory.getInstance();
	}
	
	public void releaseJedis(Jedis jedis){
		redisConnectFactory.returnResource(jedis);
	}
	
	public String auth(String password) {
		Jedis jedis=getJedis();
		String result=jedis.auth(password);
		releaseJedis(jedis);
		return result;
	}

	public String ping() {
		Jedis jedis=getJedis();
		String result=jedis.ping();
		releaseJedis(jedis);
		return result;
	}

	public String select(int index) {
		Jedis jedis=getJedis();
		String result=jedis.select(index);
		releaseJedis(jedis);
		return result;
	}

	public String echo(String string) {
		Jedis jedis=getJedis();
		String result=jedis.echo(string);
		releaseJedis(jedis);
		return result;
	}

	public String quit() {
		Jedis jedis=getJedis();
		String result=jedis.quit();
		releaseJedis(jedis);
		return result;
	}

	public Long hset(String key, String field, String value) {
		Jedis jedis=getJedis();
		Long result=jedis.hset(key, field, value);
		releaseJedis(jedis);
		return result;
	}

	public Long hsetnx(String key, String field, String value) {
		Jedis jedis=getJedis();
		Long result=jedis.hsetnx(key, field, value);
		releaseJedis(jedis);
		return result;
	}

	public String hmset(String key, Map<String, String> hash) {
		Jedis jedis=getJedis();
		String result=jedis.hmset(key, hash);
		releaseJedis(jedis);
		return result;
	}

	public String hget(String key, String field) {
		Jedis jedis=getJedis();
		String result=jedis.hget(key, field);
		releaseJedis(jedis);
		return result;
	}

	public List<String> hmget(String key, String... fields) {
		Jedis jedis=getJedis();
		List<String> result=jedis.hmget(key, fields);
		releaseJedis(jedis);
		return result;
	}

	public Map<String, String> hgetAll(String key) {
		Jedis jedis=getJedis();
		Map<String, String> result=jedis.hgetAll(key);
		releaseJedis(jedis);
		return result;
	}

	public Long hdel(String key, String field) {
		Jedis jedis=getJedis();
		Long result=jedis.hdel(key, field);
		releaseJedis(jedis);
		return result;
	}

	public Long hlen(String key) {
		Jedis jedis=getJedis();
		Long result=jedis.hlen(key);
		releaseJedis(jedis);
		return result;
	}

	public Boolean hexists(String key, String field) {
		Jedis jedis=getJedis();
		Boolean result=jedis.hexists(key, field);
		releaseJedis(jedis);
		return result;
	}

	public Long hincrBy(String key, String field, long value) {
		Jedis jedis=getJedis();
		Long result=jedis.hincrBy(key, field, value);
		releaseJedis(jedis);
		return result;
	}

	public Set<String> hkeys(String key) {
		Jedis jedis=getJedis();
		Set<String> result=jedis.hkeys(key);
		releaseJedis(jedis);
		return result;
	}

	public List<String> hvals(String key) {
		Jedis jedis=getJedis();
		List<String>  result=jedis.hvals(key);
		releaseJedis(jedis);
		return result;
	}
	
	public boolean exists(String key) {
		Jedis jedis=getJedis();
		boolean result=jedis.exists(key);
		releaseJedis(jedis);
		return result;
	}

	public Long del(String[] keys) {
		Jedis jedis=getJedis();
		Long result=jedis.del(keys);
		releaseJedis(jedis);
		return result;
	}

	public String randomKey() {
		Jedis jedis=getJedis();
		String result=jedis.randomKey();
		releaseJedis(jedis);
		return result;
	}

	public String rename(String oldkey, String newkey) {
		Jedis jedis=getJedis();
		String result=jedis.rename(oldkey, newkey);
		releaseJedis(jedis);
		return result;
	}

	public Long dbsize() {
		Jedis jedis=getJedis();
		Long result=jedis.dbSize();
		releaseJedis(jedis);
		return result;
	}

	public String fhushAll() {
		Jedis jedis=getJedis();
		String result=jedis.flushAll();
		releaseJedis(jedis);
		return result;
	}

	public List<String> sort(String key) {
		Jedis jedis=getJedis();
		 List<String> result=jedis.sort(key);
		releaseJedis(jedis);
		return result;
	}

	public Long move(String key, int dbIndex) {
		Jedis jedis=getJedis();
		Long result=jedis.move(key, dbIndex);
		releaseJedis(jedis);
		return result;
	}

	public Long renamenx(String oldKey, String newKey) {
		Jedis jedis=getJedis();
		Long result=jedis.renamenx(oldKey, newKey);
		releaseJedis(jedis);
		return result;
	}

	public String type(String key) {
		Jedis jedis=getJedis();
		String result=jedis.type(key);
		releaseJedis(jedis);
		return result;
	}

	public Long expire(String key, int seconds) {
		Jedis jedis=getJedis();
		Long result=jedis.expire(key, seconds);
		releaseJedis(jedis);
		return result;
	}

	public Long expireAt(String key, Long unixTime) {
		Jedis jedis=getJedis();
		Long result=jedis.expireAt(key, unixTime);
		releaseJedis(jedis);
		return result;
	}

	public Long lpush(String key, String... value) {
		Jedis jedis=getJedis();
		Long result=jedis.lpush(key, value);
		releaseJedis(jedis);
		return result;
	}

	public Long rpush(String key, String value) {
		Jedis jedis=getJedis();
		Long result=jedis.rpush(key, value);
		releaseJedis(jedis);
		return result;
	}

	public Long llen(String key) {
		Jedis jedis=getJedis();
		Long result=jedis.llen(key);
		releaseJedis(jedis);
		return result;
	}

	public List<String> lrange(String key, long start, long end) {
		Jedis jedis=getJedis();
		List<String> result=jedis.lrange(key, start, end);
		releaseJedis(jedis);
		return result;
	}

	public String ltrim(String key, long start, long end) {
		Jedis jedis=getJedis();
		String result=jedis.ltrim(key, start, end);
		releaseJedis(jedis);
		return result;
	}

	public Long lpushx(String key, String value) {
		Jedis jedis=getJedis();
		Long result=jedis.lpushx(key, value);
		releaseJedis(jedis);
		return result;
	}

	public Long rpushx(String key, String value) {
		Jedis jedis=getJedis();
		Long result=jedis.rpushx(key, value);
		releaseJedis(jedis);
		return result;
	}

	public String lpop(String key) {
		Jedis jedis=getJedis();
		String result=jedis.lpop(key);
		releaseJedis(jedis);
		return result;
	}

	public String rpop(String key) {
		Jedis jedis=getJedis();
		String result=jedis.rpop(key);
		releaseJedis(jedis);
		return result;
	}

	public Long lrem(String key, long count, String value) {
		Jedis jedis=getJedis();
		Long result=jedis.lrem(key, count, value);
		releaseJedis(jedis);
		return result;
	}

	public String lset(String key, long index, String value) {
		Jedis jedis=getJedis();
		String result=jedis.lset(key, index, value);
		releaseJedis(jedis);
		return result;
	}

	public String lindex(String key, long index) {
		Jedis jedis=getJedis();
		String result=jedis.lindex(key, index);
		releaseJedis(jedis);
		return result;
	}

	public String rpoplpush(String srcKey, String dstKey) {
		Jedis jedis=getJedis();
		String result=jedis.rpoplpush(srcKey, dstKey);
		releaseJedis(jedis);
		return result;
	}

	public String brpoplpush(String source, String destination, int timeout) {
		Jedis jedis=getJedis();
		String result=jedis.brpoplpush(source, destination, timeout);
		releaseJedis(jedis);
		return result;
	}
	
	public Long publish(String channel, String message) {
		Jedis jedis=getJedis();
		Long result=jedis.publish(channel, message);
		releaseJedis(jedis);
		return result;
	}

	public void subscribe(JedisPubSub jedisPubSub, String... channels) {
		Jedis jedis=getJedis();
		jedis.subscribe(jedisPubSub, channels);
		releaseJedis(jedis);
	}

	public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
		Jedis jedis=getJedis();
		jedis.psubscribe(jedisPubSub, patterns);
		releaseJedis(jedis);
	}
	
	public Long sadd(String key, String member) {
		Jedis jedis=getJedis();
		Long result=jedis.sadd(key, member);
		releaseJedis(jedis);
		return result;
	}

	public Long srem(String key, String member) {
		Jedis jedis=getJedis();
		Long result=jedis.srem(key, member);
		releaseJedis(jedis);
		return result;
	}

	public Set<String> smembers(String key) {
		Jedis jedis=getJedis();
		Set<String> result=jedis.smembers(key);
		releaseJedis(jedis);
		return result;
	}

	public Boolean sismember(String key, String member) {
		Jedis jedis=getJedis();
		Boolean result=jedis.sismember(key, member);
		releaseJedis(jedis);
		return result;
	}

	public Long scard(String key) {
		Jedis jedis=getJedis();
		Long result=jedis.scard(key);
		releaseJedis(jedis);
		return result;
	}

	public Long smove(String srckey, String dstkey, String member) {
		Jedis jedis=getJedis();
		Long result=jedis.smove(srckey, dstkey, member);
		releaseJedis(jedis);
		return result;
	}

	public String spop(String key) {
		Jedis jedis=getJedis();
		String result=jedis.spop(key);
		releaseJedis(jedis);
		return result;
	}

	public String srandmember(String key) {
		Jedis jedis=getJedis();
		String result=jedis.srandmember(key);
		releaseJedis(jedis);
		return result;
	}

	public Set<String> sinter(String... keys) {
		Jedis jedis=getJedis();
		Set<String> result=jedis.sinter(keys);
		releaseJedis(jedis);
		return result;
	}

	public Long sinterstore(String dstkey, String... keys) {
		Jedis jedis=getJedis();
		Long result=jedis.sinterstore(dstkey, keys);
		releaseJedis(jedis);
		return result;
	}

	public Set<String> sunion(String... keys) {
		Jedis jedis=getJedis();
		 Set<String> result=jedis.sunion(keys);
		releaseJedis(jedis);
		return result;
	}

	public Long sunionstore(String dstkey, String... keys) {
		Jedis jedis=getJedis();
		Long result=jedis.sunionstore(dstkey, keys);
		releaseJedis(jedis);
		return result;
	}

	public Set<String> sdiff(String... keys) {
		Jedis jedis=getJedis();
		Set<String> result=jedis.sdiff(keys);
		releaseJedis(jedis);
		return result;
	}

	public Long sdiffstore(String dstkey, String... keys) {
		Jedis jedis=getJedis();
		Long result=jedis.sdiffstore(dstkey, keys);
		releaseJedis(jedis);
		return result;
	}

	public String bgrewriteaof() {
		Jedis jedis=getJedis();
		String result=jedis.bgrewriteaof();
		releaseJedis(jedis);
		return result;
	}

	public String bgsave() {
		Jedis jedis=getJedis();
		String result=jedis.bgsave();
		releaseJedis(jedis);
		return result;
	}

	public Long lastsave() {
		Jedis jedis=getJedis();
		Long result=jedis.lastsave();
		releaseJedis(jedis);
		return result;
	}

	public String slaveof(String host, int port) {
		Jedis jedis=getJedis();
		String result=jedis.slaveof(host, port);
		releaseJedis(jedis);
		return result;
	}

	public String flushAll() {
		Jedis jedis=getJedis();
		String result=jedis.flushAll();
		releaseJedis(jedis);
		return result;
	}

	public String flushDB() {
		Jedis jedis=getJedis();
		String result=jedis.flushDB();
		releaseJedis(jedis);
		return result;
	}

	public String shutdown() {
		Jedis jedis=getJedis();
		String result=jedis.shutdown();
		releaseJedis(jedis);
		return result;
	}

	public String info() {
		Jedis jedis=getJedis();
		String result=jedis.info();
		releaseJedis(jedis);
		return result;
	}

	public List<String> configGet(String pattern) {
		Jedis jedis=getJedis();
		List<String> result=jedis.configGet(pattern);
		releaseJedis(jedis);
		return result;
	}

	public String configSet(String parameter, String value) {
		Jedis jedis=getJedis();
		String result=jedis.configSet(parameter, value);
		releaseJedis(jedis);
		return result;
	}

	public String configResetStat() {
		Jedis jedis=getJedis();
		String result=jedis.configResetStat();
		releaseJedis(jedis);
		return result;
	}

	public String debug(DebugParams params) {
		Jedis jedis=getJedis();
		String result=jedis.debug(params);
		releaseJedis(jedis);
		return result;
	}

	public void monitor(JedisMonitor jedisMonitor) {
		Jedis jedis=getJedis();
		jedis.monitor(jedisMonitor);
		releaseJedis(jedis);
	}

	public void sync() {
		Jedis jedis=getJedis();
		jedis.sync();
		releaseJedis(jedis);
	}
	
	public Long zadd(String key, double score, String member) {
		Jedis jedis=getJedis();
		Long result=jedis.zadd(key, score, member);
		releaseJedis(jedis);
		return result;
	}

	public Long zrem(String key, String member) {
		Jedis jedis=getJedis();
		Long result=jedis.zrem(key, member);
		releaseJedis(jedis);
		return result;
	}

	public Long zcard(String key) {
		Jedis jedis=getJedis();
		Long result=jedis.zcard(key);
		releaseJedis(jedis);
		return result;
	}

	public Long zcount(String key, double min, double max) {
		Jedis jedis=getJedis();
		Long result=jedis.zcount(key, min, max);
		releaseJedis(jedis);
		return result;
	}

	public Double zscore(String key, String member) {
		Jedis jedis=getJedis();
		Double result=jedis.zscore(key, member);
		releaseJedis(jedis);
		return result;
	}

	public Double zincrby(String key, double score, String member) {
		Jedis jedis=getJedis();
		Double result=jedis.zincrby(key, score, member);
		releaseJedis(jedis);
		return result;
	}

	public Set<String> zrange(String key, int start, int end) {
		Jedis jedis=getJedis();
		Set<String>  result=jedis.zrange(key, start, end);
		releaseJedis(jedis);
		return result;
	}

	public Set<String> zrevrange(String key, int start, int end) {
		Jedis jedis=getJedis();
		Set<String> result=jedis.zrevrange(key, start, end);
		releaseJedis(jedis);
		return result;
	}

	public Set<String> zrangeByScore(String key, double min, double max) {
		Jedis jedis=getJedis();
		Set<String> result=jedis.zrangeByScore(key, min, max);
		releaseJedis(jedis);
		return result;
	}

	public Long zrank(String key, String member) {
		Jedis jedis=getJedis();
		Long result=jedis.zrank(key, member);
		releaseJedis(jedis);
		return result;
	}

	public Long zrevrank(String key, String member) {
		Jedis jedis=getJedis();
		Long result=jedis.zrevrank(key, member);
		releaseJedis(jedis);
		return result;
	}

	public Long zremrangeByRank(String key, int start, int end) {
		Jedis jedis=getJedis();
		Long result=jedis.zremrangeByRank(key, start, end);
		releaseJedis(jedis);
		return result;
	}

	public Long zremrangeByScore(String key, double start, double end) {
		Jedis jedis=getJedis();
		Long result=jedis.zremrangeByScore(key, start, end);
		releaseJedis(jedis);
		return result;
	}

	public Long zinterstore(String dstkey, String... sets) {
		Jedis jedis=getJedis();
		Long result=jedis.zinterstore(dstkey, sets);
		releaseJedis(jedis);
		return result;
	}

	public Long zunionstore(String dstkey, String... sets) {
		Jedis jedis=getJedis();
		Long result=jedis.zunionstore(dstkey, sets);
		releaseJedis(jedis);
		return result;
	}
	
	public void set(String key, String value) {
		Jedis jedis=getJedis();
		jedis.set(key, value);
		releaseJedis(jedis);
	}

	public String get(String key) {
		Jedis jedis=getJedis();
		String result=jedis.get(key);
		releaseJedis(jedis);
		return result;
	}

	public void setnx(String key, String value) {
		Jedis jedis=getJedis();
		jedis.setnx(key, value);
		releaseJedis(jedis);
	}

	public String getSet(String key, String value) {
		Jedis jedis=getJedis();
		String result=jedis.getSet(key, value);
		releaseJedis(jedis);
		return result;
	}

	public List<String> mget(String[] keys) {
		Jedis jedis=getJedis();
		List<String> result=jedis.mget(keys);
		releaseJedis(jedis);
		return result;
	}

	public void mset(String... keysvalues) {
		Jedis jedis=getJedis();
		jedis.mset(keysvalues);
		releaseJedis(jedis);
	}

	public void msetnx(String... keysvalues) {
		Jedis jedis=getJedis();
		jedis.msetnx(keysvalues);
		releaseJedis(jedis);
	}

	public Long incrBy(String key, Integer integer) {
		Jedis jedis=getJedis();
		Long result=jedis.incrBy(key, integer);
		releaseJedis(jedis);
		return result;
	}

	public Long strlen(String key) {
		Jedis jedis=getJedis();
		Long result=jedis.strlen(key);
		releaseJedis(jedis);
		return result;
	}

	public Long decrBy(String key, Integer integer) {
		Jedis jedis=getJedis();
		Long result=jedis.decrBy(key, integer);
		releaseJedis(jedis);
		return result;
	}

	public Long incr(String key) {
		Jedis jedis=getJedis();
		Long result=jedis.incr(key);
		releaseJedis(jedis);
		return result;
	}

	public Long decr(String key) {
		Jedis jedis=getJedis();
		Long result=jedis.decr(key);
		releaseJedis(jedis);
		return result;
	}

	public Long append(String key, String value) {
		Jedis jedis=getJedis();
		Long result=jedis.append(key, value);
		releaseJedis(jedis);
		return result;
	}

	public String subStr(String key, int Start, int end) {
		Jedis jedis=getJedis();
		String result=jedis.substr(key, Start, end);
		releaseJedis(jedis);
		return result;
	}

	public String setex(String key, int second, String value) {
		Jedis jedis=getJedis();
		String result=jedis.setex(key, second, value);
		releaseJedis(jedis);
		return result;
	}

	public Long setRange(String key, long offset, String value) {
		Jedis jedis=getJedis();
		Long result=jedis.setrange(key, offset, value);
		releaseJedis(jedis);
		return result;
	}

	public String getRange(String key, long StartOffset, long endOffset) {
		Jedis jedis=getJedis();
		String result=jedis.getrange(key, StartOffset, endOffset);
		releaseJedis(jedis);
		return result;
	}
	
	public String watch(String... keys) {
		Jedis jedis=getJedis();
		String result=jedis.watch(keys);
		releaseJedis(jedis);
		return result;
	}

	public String unwatch() {
		Jedis jedis=getJedis();
		String result=jedis.unwatch();
		releaseJedis(jedis);
		return result;
	}

	public void multi() {
		Jedis jedis=getJedis();
		jedis.multi();
		releaseJedis(jedis);
	}

	public RedisConnectFactory getRedisConnectFactory() {
		return redisConnectFactory;
	}

	public void setRedisConnectFactory(RedisConnectFactory redisConnectFactory) {
		this.redisConnectFactory = redisConnectFactory;
	}
}
