/*
package com.wondertek.mam.util.redis;

import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;

public class RedisSentinelCfg extends RedisSentinelConfiguration {

	public RedisSentinelCfg(String master, String sentinels) {
		super();
		this.setMaster(master);
		String[] nodes = sentinels.split(",");
		for (String node : nodes) {
			RedisNode rn = new RedisNode(node.split(":")[0], Integer.parseInt(node.split(":")[1]));
			this.addSentinel(rn);
		}
	}

}
*/
