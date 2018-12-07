package org.takeback.util.cache.redis.assist;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

/**
 * @author xiongguohui
 * 读取redis配置类
 */
public class JedisCacheHelper {
	private static Logger logger = Logger.getLogger(JedisCacheHelper.class);
	public static ShardedJedisPool initShardedJedisPool(Config_JedisCache config)
			throws Exception {
		List<JedisShardInfo> jedisShardList=createJedisShardInfo(config);
		// JedisPoolConfig
		JedisPoolConfig pooconfig = new JedisPoolConfig();
		//pooconfig.setMaxActive(config.getMaxActive());
		//pooconfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		pooconfig.setMaxIdle(config.getMaxIdle());
		//pooconfig.setMaxWait(config.getMaxWait());
		pooconfig.setTimeBetweenEvictionRunsMillis(config
				.getTimeBetweenEvictionRunsMillis());
		pooconfig.setMinEvictableIdleTimeMillis(config
				.getMinEvictableIdleTimeMillis());
		pooconfig.setNumTestsPerEvictionRun(config.getNumTestsPerEvictionRun());
		pooconfig.setTestOnBorrow(config.isTestOnBorrow());
		pooconfig.setTestOnReturn(config.isTestOnReturn());
		pooconfig.setTestWhileIdle(config.isTestWhileIdle());
		//pooconfig.setWhenExhaustedAction(config.getWhenExhaustedAction());
		 
		ShardedJedisPool shardedJedisPool = new ShardedJedisPool(pooconfig,
				jedisShardList);
		return shardedJedisPool;
	}
	
	public static List<JedisShardInfo> createJedisShardInfo(Config_JedisCache config) throws Exception{
		String[] servers = config.getServers().split(" ");
		if (servers == null || servers.length <= 0) {
			throw new Exception("redis servers is null");
		}
		// 处理权重
		int[] weights = null;
		if (config.getWeights() != null
				&& config.getWeights().trim().length() > 0) {
			String[] weights_tmp = config.getWeights().split(",");
			if (weights_tmp != null && weights_tmp.length > 0) {
				weights = new int[weights_tmp.length];
				for (int i = 0; i < weights_tmp.length; i++) {
					weights[i] = Integer.parseInt(weights_tmp[i]);
				}
			}
		}
		// jedisShardList
		List<JedisShardInfo> jedisShardList = new LinkedList<JedisShardInfo>();
		// 有权重设置
		if (weights != null && weights.length == servers.length) {
			for (int i = 0; i < servers.length; i++) {
				String[] adress = servers[i].trim().split(":");
				String host = adress[0];
				int port = Integer.parseInt(adress[1]);
				JedisShardInfo jedisShardInfo = new JedisShardInfo(host, port,
						config.getTimeout(),config.getTimeout(), weights[i]);
				jedisShardList.add(jedisShardInfo);
			}
		}
		// 无权重设置
		else {
			for (int i = 0; i < servers.length; i++) {
				String[] adress = servers[i].trim().split(":");
				String host = adress[0];
				int port = Integer.parseInt(adress[1]);
				JedisShardInfo jedisShardInfo = new JedisShardInfo(host, port,
						config.getTimeout());
				jedisShardList.add(jedisShardInfo);
			}
		}
		return jedisShardList;
	}

	public static JedisPool initJedisPool(Config_SingleJedisCache config)
			throws Exception {
		if (StringUtils.isBlank(config.getServer())) {
			throw new Exception("redis server is null");
		}
		// JedisPoolConfig
		JedisPoolConfig pooconfig = new JedisPoolConfig();
	//	pooconfig.setMaxActive(config.getMaxActive());
		pooconfig.setMaxIdle(config.getMaxIdle());
	//	pooconfig.setMaxWait(config.getMaxWait());
		pooconfig.setTimeBetweenEvictionRunsMillis(config
				.getTimeBetweenEvictionRunsMillis());
		pooconfig.setMinEvictableIdleTimeMillis(config
				.getMinEvictableIdleTimeMillis());
		pooconfig.setNumTestsPerEvictionRun(config.getNumTestsPerEvictionRun());
		pooconfig.setTestOnBorrow(config.isTestOnBorrow());
		pooconfig.setTestOnReturn(config.isTestOnReturn());
		pooconfig.setTestWhileIdle(config.isTestWhileIdle());
		//pooconfig.setWhenExhaustedAction(config.getWhenExhaustedAction());

		String[] adress = config.getServer().trim().split(":");
		String host = adress[0];
		int port = Integer.parseInt(adress[1]);
		logger.info("######host###### " + host +" ####port####" + port);
		JedisPool jedisPool = new JedisPool(pooconfig, host, port,
				config.getTimeout());
		return jedisPool;
	}
}
