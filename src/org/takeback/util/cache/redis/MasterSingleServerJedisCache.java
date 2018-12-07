package org.takeback.util.cache.redis;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.takeback.util.SerializeUtil;
import org.takeback.util.cache.redis.assist.Config_SingleJedisCache;
import org.takeback.util.cache.redis.assist.JedisCacheHelper;

import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.SortingParams;

/**
 * 
 * 单server端redis客户端，项目中在JredisUtil 类中 new过一次 可以 全局使用
 * @author xiongguohui
 * 
 */
public class MasterSingleServerJedisCache {
	
	private static Logger logger = Logger.getLogger(MasterSingleServerJedisCache.class);
	
	private JedisPool jedisPool = null;

	/**
	 * 构造函数(从配置文件中加载配置项)初始化jedis池，项目中new一次后 全局使用
	 * 
	 * @throws Exception
	 */
	public MasterSingleServerJedisCache() throws Exception {
		JedisPool jedisPool = JedisCacheHelper
				.initJedisPool(new Config_SingleJedisCache());
		this.jedisPool = jedisPool;
	}

	/**
	 * 构造函数 初始化jedis池，项目中new一次后 全局使用
	 * 
	 * @param config
	 * @throws Exception
	 */
	public MasterSingleServerJedisCache(Config_SingleJedisCache config)
			throws Exception {
		JedisPool jedisPool = JedisCacheHelper.initJedisPool(config);
		this.jedisPool = jedisPool;
	}

	
	public <T> T get(String key,Class<T> t){
		if (get(key.getBytes()) != null) {
			Object obj = SerializeUtil.unserialize(get(key.getBytes()));
			return t.cast(obj);
		} else {
			return null;
		}
	}
	
	
	public boolean set(String key,Object obj) {
		logger.info("key:"+key);
		if(key==null||obj==null){return false;}
		boolean isok =set(key.getBytes(), SerializeUtil.serialize(obj));
		return isok;
	}
	
	/* ==========================Jedis链接池的管理====================== */
	/**
	 * 同步从池中获取Jedis
	 * 
	 * @return
	 */
	private synchronized Jedis borrowResource() {
		Jedis jedis = jedisPool.getResource();
		return jedis;
	}

	/**
	 * 归还Jedis给池
	 * 
	 * @param jedis
	 */
	private void returnResource(Jedis jedis) {
		try {
			jedisPool.returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("return redis resource error !", e);
		}
	}

	/**
	 * 归还不可用的Jedis给池
	 * 
	 * @param jedis
	 */
	private void returnBrokenResource(Jedis jedis) {
		try {
			jedisPool.returnBrokenResource(jedis);
		} catch (Exception e) {
			logger.error("returnBrokenResource", e);
		}
	}

	/**
	 * 销毁池
	 */
	public void destroy() {
		try {
			if (jedisPool != null) {
				jedisPool.destroy();
			}
		} catch (Exception e) {
			logger.error("destroy redis error !", e);
		}
	}

	/* ==========================Key(键)操作====================== */
	/**
	 * 删除给定的一个key
	 * 
	 * 返回值：被删除key的数量
	 * 
	 * @param key
	 * @return
	 */
	public long del(String key) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.del(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("del error !", e);
		}
		return result;
	}

	/**
	 * 查找所有符合给定模式 pattern 的 key 。 KEYS * 匹配数据库中所有 key 。 KEYS h?llo 匹配 hello ，
	 * hallo 和 hxllo 等。 KEYS h*llo 匹配 hllo 和 heeeeello 等。 KEYS h[ae]llo 匹配 hello
	 * 和 hallo ，但不匹配 hillo 。
	 * 
	 * 特殊符号用 \ 隔开
	 * 
	 * 返回值： 符合给定模式的 key 列表。
	 * 
	 * @param pattern
	 * @return
	 */
	public Set<String> keys(String pattern) {
		Set<String> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.keys(pattern);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("keys error !", e);
		}
		return result;
	}

	/**
	 * 以秒为单位，返回给定 key 的剩余生存时间(TTL, time to live)
	 * 
	 * 返回值： 当 key 不存在时，返回 -2 。 当 key 存在但没有设置剩余生存时间时，返回 -1 。 否则，以秒为单位，返回 key
	 * 的剩余生存时间。
	 * 
	 * 
	 * @param key
	 * @return
	 */
	public long ttl(String key) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.ttl(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.ttl falid", e);
		}
		return result;
	}

	/**
	 * 检查给定 key 是否存在
	 * 
	 * 返回值： 若 key 存在，返回 1 ，否则返回 0 。
	 * 
	 * @param key
	 * @return
	 */
	public boolean exists(String key) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.exists(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.exists falid", e);
		}
		return result;
	}

	/**
	 * 检查给定 key 是否存在
	 * 
	 * 返回值： 若 key 存在，返回 1 ，否则返回 0 。
	 * 
	 * @param key
	 * @return
	 */
	public boolean exists(byte[] key) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.exists(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.exists falid", e);
		}
		return result;
	}

	/**
	 * 返回 key 所储存的值的类型;
	 * 
	 * 返回值： none (key不存在) string (字符串) list (列表) set (集合) zset (有序集) hash (哈希表)
	 * 
	 * @param key
	 * @return
	 */
	public String type(String key) {
		String result = "";
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.type(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.type falid", e);
		}
		return result;
	}

	/**
	 * 返回 key 所储存的值的类型;
	 * 
	 * 返回值： none (key不存在) string (字符串) list (列表) set (集合) zset (有序集) hash (哈希表)
	 * 
	 * @param key
	 * @return
	 */
	public String type(byte[] key) {
		String result = "";
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.type(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.type falid", e);
		}
		return result;
	}

	/**
	 * 为给定 key 设置或更新生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除；
	 * 
	 * 返回值： 设置成功返回 1 。 当 key 不存在或者不能为 key 设置生存时间时(比如在低于 2.1.3 版本的 Redis 中你尝试更新
	 * key 的生存时间)，返回 0
	 * 
	 * @param key
	 * @param seconds
	 * @return
	 */
	public long expire(String key, int seconds) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.expire(key, seconds);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.expire falid", e);
		}
		return result;
	}

	/**
	 * 为给定 key 设置或更新生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除；
	 * 
	 * 返回值： 设置成功返回 1 。 当 key 不存在或者不能为 key 设置生存时间时(比如在低于 2.1.3 版本的 Redis 中你尝试更新
	 * key 的生存时间)，返回 0
	 * 
	 * @param key
	 * @param seconds
	 * @return
	 */
	public long expire(byte[] key, int seconds) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.expire(key, seconds);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.expire falid", e);
		}
		return result;
	}

	/**
	 * 为给定 key 设置或更新过期时间；
	 * 
	 * 返回值： 如果生存时间设置成功，返回 1 。 当 key 不存在或没办法设置生存时间，返回 0 。
	 * 
	 * 
	 * @param key
	 * @param unixTime
	 * @return
	 */
	public long expireAt(String key, Date expiry) {
		long result = -10000;
		Jedis jedis = null;
		long unixTime = expiry.getTime() / 1000;
		try {
			jedis = borrowResource();
			// 时间参数是 UNIX 时间戳(unix
			// timestamp)；是从1970年1月1日（UTC/GMT的午夜）开始所经过的秒数，不考虑闰秒。
			result = jedis.expireAt(key, unixTime);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.expireAt falid", e);
		}
		return result;
	}

	/**
	 * 为给定 key 设置或更新过期时间；
	 * 
	 * 返回值： 如果生存时间设置成功，返回 1 。 当 key 不存在或没办法设置生存时间，返回 0 。
	 * 
	 * @param key
	 * @param unixTime
	 * @return
	 */
	public long expireAt(byte[] key, Date expiry) {
		long result = -10000;
		Jedis jedis = null;
		long unixTime = expiry.getTime() / 1000;
		try {
			jedis = borrowResource();
			// 时间参数是 UNIX 时间戳(unix
			// timestamp)；是从1970年1月1日（UTC/GMT的午夜）开始所经过的秒数，不考虑闰秒。
			result = jedis.expireAt(key, unixTime);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.expireAt falid", e);
		}
		return result;
	}

	/* ==========================对value操作====================== */
	/**
	 * 将字符串值 value 关联到 key 。 如果 key 已经持有其他值， SET 就覆写旧值，无视类型。
	 * 对于某个原本带有生存时间（TTL）的键来说， 当 SET 命令成功在这个键上执行时， 这个键原有的 TTL 将被清除。
	 * 
	 * 返回 OK
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean set(String key, String value) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			String status = jedis.set(key, value);
			if ("OK".equalsIgnoreCase(status)) {
				result = true;
			}
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.set falid", e);
		}
		return result;
	}

	/**
	 * 将字符串值 value 关联到 key 。 如果 key 已经持有其他值， SET 就覆写旧值，无视类型。
	 * 对于某个原本带有生存时间（TTL）的键来说， 当 SET 命令成功在这个键上执行时， 这个键原有的 TTL 将被清除。
	 * 
	 * 返回 OK
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean set(byte[] key, byte[] value) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			String status = jedis.set(key, value);
			if ("OK".equalsIgnoreCase(status)) {
				result = true;
			}
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.set falid", e);
		}
		return result;
	}

	/**
	 * 将 key 的值设为 value ，当且仅当 key 不存在。 若给定的 key 已经存在，则 SETNX 不做任何动作。 SETNX 是『SET
	 * if Not eXists』(如果不存在，则 SET)的简写。
	 * 
	 * 返回值： 设置成功，返回 1 。 设置失败，返回 0 。
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public long setnx(String key, String value) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.setnx(key, value);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.setnx falid", e);
		}
		return result;
	}

	/**
	 * 将 key 的值设为 value ，当且仅当 key 不存在。 若给定的 key 已经存在，则 SETNX 不做任何动作。 SETNX 是『SET
	 * if Not eXists』(如果不存在，则 SET)的简写。
	 * 
	 * 返回值： 设置成功，返回 1 。 设置失败，返回 0 。
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Long setnx(byte[] key, byte[] value) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.setnx(key, value);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.setnx falid", e);
		}
		return result;
	}

	/**
	 * 将值 value 关联到 key ，并将 key 的生存时间设为 seconds (以秒为单位)。 如果 key 已经存在， SETEX
	 * 命令将覆写旧值。
	 * 
	 * 返回值： 设置成功时返回 OK 。 当 seconds 参数不合法时，返回一个错误。
	 * 
	 * @param key
	 * @param seconds
	 * @param value
	 * @return
	 */
	public boolean setex(String key, int seconds, String value) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			String status = jedis.setex(key, seconds, value);
			if ("OK".equalsIgnoreCase(status)) {
				result = true;
			}
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.setex falid", e);
		}
		return result;
	}

	/**
	 * 将值 value 关联到 key ，并将 key 的生存时间设为 seconds (以秒为单位)。 如果 key 已经存在， SETEX
	 * 命令将覆写旧值。
	 * 
	 * 返回值： 设置成功时返回 OK 。 当 seconds 参数不合法时，返回一个错误。
	 * 
	 * @param key
	 * @param seconds
	 * @param value
	 * @return
	 */
	public boolean setex(byte[] key, int seconds, byte[] value) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			String status = jedis.setex(key, seconds, value);
			if ("OK".equalsIgnoreCase(status)) {
				result = true;
			}
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.setex falid", e);
		}
		return result;
	}

	/**
	 * 用 value 参数覆写(overwrite)给定 key 所储存的字符串值，从偏移量 offset 开始。 不存在的 key
	 * 当作空白字符串处理。
	 * 
	 * 返回值： 被 SETRANGE 修改之后，字符串的长度。
	 * 
	 * @param key
	 * @param offset
	 * @param value
	 * @return
	 */
	public long setrange(String key, long offset, String value) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.setrange(key, offset, value);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.setrange falid", e);
		}
		return result;
	}

	/**
	 * 同时设置一个或多个 key-value 对。
	 * 
	 * 如果某个给定 key 已经存在，那么 MSET 会用新值覆盖原来的旧值，如果这不是你所希望的效果，请考虑使用 MSETNX 命令：它只会在所有给定
	 * key 都不存在的情况下进行设置操作。
	 * 
	 * MSET 是一个原子性(atomic)操作，所有给定 key 都会在同一时间内被设置，某些给定 key 被更新而另一些给定 key
	 * 没有改变的情况，不可能发生。
	 * 
	 * 返回值： 总是返回 OK (因为 MSET 不可能失败)
	 * 
	 * @param keysvalues
	 * @return
	 */
	public boolean mset(String... keysvalues) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			String status = jedis.mset(keysvalues);
			if ("OK".equalsIgnoreCase(status)) {
				result = true;
			}
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.mset falid", e);
		}
		return result;
	}

	/**
	 * 同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在。
	 * 
	 * 即使只有一个给定 key 已存在， MSETNX 也会拒绝执行所有给定 key 的设置操作。
	 * 
	 * MSETNX 是原子性的，因此它可以用作设置多个不同 key 表示不同字段(field)的唯一性逻辑对象(unique logic
	 * object)，所有字段要么全被设置，要么全不被设置。
	 * 
	 * 返回值： 当所有 key 都成功设置，返回 1 。 如果所有给定 key 都设置失败(至少有一个 key 已经存在)，那么返回 0 。
	 * 
	 * @param keysvalues
	 * @return
	 */
	public Long msetnx(String... keysvalues) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.msetnx(keysvalues);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.msetnx falid", e);
		}
		return result;
	}

	/**
	 * 如果 key 已经存在并且是一个字符串， APPEND 命令将 value 追加到 key 原来的值的末尾。 如果 key 不存在， APPEND
	 * 就简单地将给定 key 设为 value ，就像执行 SET key value 一样。
	 * 
	 * 返回值： 追加 value 之后， key 中字符串的长度。
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public long append(String key, String value) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.append(key, value);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.append falid", e);
		}
		return result;
	}

	/**
	 * 如果 key 已经存在并且是一个字符串， APPEND 命令将 value 追加到 key 原来的值的末尾。 如果 key 不存在， APPEND
	 * 就简单地将给定 key 设为 value ，就像执行 SET key value 一样。
	 * 
	 * 返回值： 追加 value 之后， key 中字符串的长度。
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public long append(byte[] key, byte[] value) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.append(key, value);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.append falid", e);
		}
		return result;
	}

	/**
	 * 返回 key 所关联的字符串值。 如果 key 不存在那么返回特殊值 nil 。 假如 key 储存的值不是字符串类型，返回一个错误，因为 GET
	 * 只能用于处理字符串值。
	 * 
	 * 返回值： 当 key 不存在时，返回 nil ，否则，返回 key 的值。 如果 key 不是字符串类型，那么返回一个错误。
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.get(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.get falid", e);
		}
		return result;
	}

	/**
	 * 返回 key 所关联的字符串值。 如果 key 不存在那么返回特殊值 nil 。 假如 key 储存的值不是字符串类型，返回一个错误，因为 GET
	 * 只能用于处理字符串值。
	 * 
	 * 返回值： 当 key 不存在时，返回 nil ，否则，返回 key 的值。 如果 key 不是字符串类型，那么返回一个错误。
	 * 
	 * @param key
	 * @return
	 */
	public byte[] get(byte[] key) {
		byte[] result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.get(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.get falid", e);
		}
		return result;
	}

	/**
	 * 返回 key 中字符串值的子字符串，字符串的截取范围由 start 和 end 两个偏移量决定(包括 start 和 end 在内)。
	 * 负数偏移量表示从字符串最后开始计数， -1 表示最后一个字符， -2 表示倒数第二个，以此类推。
	 * 
	 * 返回值： 截取得出的子字符串。
	 * 
	 * @param key
	 * @param startOffset
	 * @param endOffset
	 * @return
	 */
	public String getrange(String key, long startOffset, long endOffset) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.getrange(key, startOffset, endOffset);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.getrange falid", e);
		}
		return result;
	}

	/**
	 * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)。 当 key 存在但不是字符串类型时，返回一个错误。
	 * 
	 * 返回值： 返回给定 key 的旧值。 当 key 没有旧值时，也即是， key 不存在时，返回 nil 。
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public String getSet(String key, String value) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.getSet(key, value);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.getSet falid", e);
		}
		return result;
	}

	/**
	 * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)。 当 key 存在但不是字符串类型时，返回一个错误。
	 * 
	 * 返回值： 返回给定 key 的旧值。 当 key 没有旧值时，也即是， key 不存在时，返回 nil 。
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public byte[] getSet(byte[] key, byte[] value) {
		byte[] result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.getSet(key, value);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.getSet falid", e);
		}
		return result;
	}

	/**
	 * 将 key 中储存的数字值减一。 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECR 操作。
	 * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在 64 位(bit)有符号数字表示之内。
	 * 
	 * 返回值： 执行 DECR 命令之后 key 的值。
	 * 
	 * @param key
	 * @return
	 */
	public long decr(String key) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.decr(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.decr falid", e);
		}
		return result;
	}

	/**
	 * 将 key 中储存的数字值减一。 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECR 操作。
	 * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在 64 位(bit)有符号数字表示之内。
	 * 
	 * 返回值： 执行 DECR 命令之后 key 的值。
	 * 
	 * @param key
	 * @return
	 */
	public long decr(byte[] key) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.decr(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.decr falid", e);
		}
		return result;
	}

	/**
	 * 将 key 所储存的值减去减量 decrement 。 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECRBY
	 * 操作。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在 64 位(bit)有符号数字表示之内。
	 * 
	 * 返回值： 减去 decrement 之后， key 的值。
	 * 
	 * @param key
	 * @param integer
	 * @return
	 */
	public long decrBy(String key, long integer) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.decrBy(key, integer);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.decrBy falid", e);
		}
		return result;
	}

	/**
	 * 将 key 所储存的值减去减量 decrement 。 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECRBY
	 * 操作。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在 64 位(bit)有符号数字表示之内。
	 * 
	 * 返回值： 减去 decrement 之后， key 的值。
	 * 
	 * @param key
	 * @param integer
	 * @return
	 */
	public long decrBy(byte[] key, long integer) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.decrBy(key, integer);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.decrBy falid", e);
		}
		return result;
	}

	/**
	 * 将 key 中储存的数字值增一。 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCR 操作。
	 * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在 64 位(bit)有符号数字表示之内。
	 * 
	 * 返回值： 执行 INCR 命令之后 key 的值。
	 * 
	 * @param key
	 * @return
	 */
	public long incr(String key) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.incr(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.incr falid", e);
		}
		return result;
	}

	/**
	 * 将 key 中储存的数字值增一。 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCR 操作。
	 * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在 64 位(bit)有符号数字表示之内。
	 * 
	 * 返回值： 执行 INCR 命令之后 key 的值。
	 * 
	 * @param key
	 * @return
	 */
	public long incr(byte[] key) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.incr(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.incr falid", e);
		}
		return result;
	}

	/**
	 * 将 key 所储存的值加上增量 increment 。 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY
	 * 命令。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在 64 位(bit)有符号数字表示之内。
	 * 
	 * 返回值： 加上 increment 之后， key 的值。
	 * 
	 * @param key
	 * @param integer
	 * @return
	 */
	public long incrBy(String key, long integer) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.incrBy(key, integer);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.incrBy falid", e);
		}
		return result;
	}

	/**
	 * 将 key 所储存的值加上增量 increment 。 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY
	 * 命令。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在 64 位(bit)有符号数字表示之内。
	 * 
	 * 返回值： 加上 increment 之后， key 的值。
	 * 
	 * @param key
	 * @param integer
	 * @return
	 */
	public long incrBy(byte[] key, long integer) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.incrBy(key, integer);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.incrBy falid", e);
		}
		return result;
	}

	/**
	 * 排序，排序默认以数字作为对象，值被解释为双精度浮点数，然后进行比较；
	 * 
	 * 返回键值从小到大排序的结果
	 * 
	 * @param key
	 * @return
	 */
	public List<String> sort(String key) {
		List<String> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.sort(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.sort falid", e);
		}
		return result;
	}

	/**
	 * 排序，排序默认以数字作为对象，值被解释为双精度浮点数，然后进行比较；
	 * 
	 * 返回键值从小到大排序的结果
	 * 
	 * @param key
	 * @return
	 */
	public List<byte[]> sort(byte[] key) {
		List<byte[]> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.sort(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.sort falid", e);
		}
		return result;
	}

	/**
	 * 排序，按SortingParams中的规则排序；
	 * 
	 * 返回排序后的结果
	 * 
	 * @param key
	 * @param sortingParameters
	 * @return
	 */
	public List<String> sort(String key, SortingParams sortingParameters) {
		List<String> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.sort(key, sortingParameters);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.sort falid", e);
		}
		return result;
	}

	/**
	 * 排序，按SortingParams中的规则排序；
	 * 
	 * 返回排序后的结果
	 * 
	 * @param key
	 * @param sortingParameters
	 * @return
	 */
	public List<byte[]> sort(byte[] key, SortingParams sortingParameters) {
		List<byte[]> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.sort(key, sortingParameters);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.sort falid", e);
		}
		return result;
	}

	/* ==========================对Hash(哈希表)操作====================== */
	/**
	 * 将哈希表 key 中的域 field 的值设为 value 。 如果 key 不存在，一个新的哈希表被创建并进行 HSET 操作。 如果域
	 * field 已经存在于哈希表中，旧值将被覆盖。
	 * 
	 * 返回值： 如果 field 是哈希表中的一个新建域，并且值设置成功，返回 1 。 如果哈希表中域 field 已经存在且旧值已被新值覆盖，返回 0
	 * 。
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public long hset(String key, String field, String value) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.hset(key, field, value);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hset falid", e);
		}
		return result;
	}

	/**
	 * 将哈希表 key 中的域 field 的值设为 value 。 如果 key 不存在，一个新的哈希表被创建并进行 HSET 操作。 如果域
	 * field 已经存在于哈希表中，旧值将被覆盖。
	 * 
	 * 返回值： 如果 field 是哈希表中的一个新建域，并且值设置成功，返回 1 。 如果哈希表中域 field 已经存在且旧值已被新值覆盖，返回 0
	 * 。
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public long hset(byte[] key, byte[] field, byte[] value) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.hset(key, field, value);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hset falid", e);
		}
		return result;
	}

	/**
	 * 将哈希表 key 中的域 field 的值设置为 value ，当且仅当域 field 不存在。 若域 field 已经存在，该操作无效。 如果
	 * key 不存在，一个新哈希表被创建并执行 HSETNX 命令。
	 * 
	 * 返回值： 设置成功，返回 1 。 如果给定域已经存在且没有操作被执行，返回 0 。
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public long hsetnx(String key, String field, String value) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.hsetnx(key, field, value);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hsetnx falid", e);
		}
		return result;
	}

	/**
	 * 将哈希表 key 中的域 field 的值设置为 value ，当且仅当域 field 不存在。 若域 field 已经存在，该操作无效。 如果
	 * key 不存在，一个新哈希表被创建并执行 HSETNX 命令。
	 * 
	 * 返回值： 设置成功，返回 1 。 如果给定域已经存在且没有操作被执行，返回 0 。
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public long hsetnx(byte[] key, byte[] field, byte[] value) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.hsetnx(key, field, value);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hsetnx falid", e);
		}
		return result;
	}

	/**
	 * 同时将多个 field-value (域-值)对设置到哈希表 key 中。 此命令会覆盖哈希表中已存在的域。 如果 key
	 * 不存在，一个空哈希表被创建并执行 HMSET 操作。
	 * 
	 * 返回值： 如果命令执行成功，返回 OK 。 当 key 不是哈希表(hash)类型时，返回一个错误。
	 * 
	 * @param key
	 * @param hash
	 * @return
	 */
	public boolean hmset(String key, Map<String, String> hash) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			String status = jedis.hmset(key, hash);
			if ("OK".equalsIgnoreCase(status)) {
				result = true;
			}
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hmset falid", e);
		}
		return result;
	}

	/**
	 * 同时将多个 field-value (域-值)对设置到哈希表 key 中。 此命令会覆盖哈希表中已存在的域。 如果 key
	 * 不存在，一个空哈希表被创建并执行 HMSET 操作。
	 * 
	 * 返回值： 如果命令执行成功，返回 OK 。 当 key 不是哈希表(hash)类型时，返回一个错误。
	 * 
	 * @param key
	 * @param hash
	 * @return
	 */
	public boolean hmset(byte[] key, Map<byte[], byte[]> hash) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			String status = jedis.hmset(key, hash);
			if ("OK".equalsIgnoreCase(status)) {
				result = true;
			}
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hmset falid", e);
		}
		return result;
	}

	/**
	 * 获取哈希表 key 中给定域 field 的值。
	 * 
	 * 返回值： 给定域的值。 当给定域不存在或是给定 key 不存在时，返回 nil 。
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public String hget(String key, String field) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.hget(key, field);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hget falid", e);
		}
		return result;
	}

	/**
	 * 获取哈希表 key 中给定域 field 的值。
	 * 
	 * 返回值： 给定域的值。 当给定域不存在或是给定 key 不存在时，返回 nil 。
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public byte[] hget(byte[] key, byte[] field) {
		byte[] result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.hget(key, field);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hget falid", e);
		}
		return result;
	}

	/**
	 * 返回哈希表 key 中，一个或多个给定域的值。 如果给定的域不存在于哈希表，那么返回一个 nil 值。 因为不存在的 key
	 * 被当作一个空哈希表来处理，所以对一个不存在的 key 进行 HMGET 操作将返回一个只带有 nil 值的表。
	 * 
	 * 返回值： 一个包含多个给定域的关联值的表，表值的排列顺序和给定域参数的请求顺序一样。
	 * 
	 * @param key
	 * @param fields
	 * @return
	 */
	public List<String> hmget(String key, String... fields) {
		List<String> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.hmget(key, fields);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hmget falid", e);
		}
		return result;
	}

	/**
	 * 返回哈希表 key 中，一个或多个给定域的值。 如果给定的域不存在于哈希表，那么返回一个 nil 值。 因为不存在的 key
	 * 被当作一个空哈希表来处理，所以对一个不存在的 key 进行 HMGET 操作将返回一个只带有 nil 值的表。
	 * 
	 * 返回值： 一个包含多个给定域的关联值的表，表值的排列顺序和给定域参数的请求顺序一样。
	 * 
	 * @param key
	 * @param fields
	 * @return
	 */
	public List<byte[]> hmget(byte[] key, byte[]... fields) {
		List<byte[]> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.hmget(key, fields);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hmget falid", e);
		}
		return result;
	}

	/**
	 * 返回哈希表 key 中，所有的域和值。 在返回值里，紧跟每个域名(field
	 * name)之后是域的值(value)，所以返回值的长度是哈希表大小的两倍。
	 * 
	 * 返回值： 以列表形式返回哈希表的域和域的值。 若 key 不存在，返回空列表。
	 * 
	 * @param key
	 * @return
	 */
	public Map<String, String> hgetAll(String key) {
		Map<String, String> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.hgetAll(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hgetAll falid", e);
		}
		return result;
	}

	/**
	 * 返回哈希表 key 中，所有的域和值。 在返回值里，紧跟每个域名(field
	 * name)之后是域的值(value)，所以返回值的长度是哈希表大小的两倍。
	 * 
	 * 返回值： 以列表形式返回哈希表的域和域的值。 若 key 不存在，返回空列表。
	 * 
	 * @param key
	 * @return
	 */
	public Map<byte[], byte[]> hgetAll(byte[] key) {
		Map<byte[], byte[]> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.hgetAll(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hgetAll falid", e);
		}
		return result;
	}

	/**
	 * 删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
	 * 
	 * 返回值: 被成功移除的域的数量，不包括被忽略的域。
	 * 
	 * @param key
	 * @param fields
	 * @return
	 */
	public long hdel(String key, String... fields) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.hdel(key, fields);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hdel falid", e);
		}
		return result;
	}

	/**
	 * 删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
	 * 
	 * 返回值: 被成功移除的域的数量，不包括被忽略的域。
	 * 
	 * @param key
	 * @param fields
	 * @return
	 */
	public long hdel(byte[] key, byte[]... fields) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.hdel(key, fields);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hdel falid", e);
		}
		return result;
	}

	/**
	 * 返回哈希表 key 中域的数量。
	 * 
	 * 返回值： 哈希表中域的数量。 当 key 不存在时，返回 0 。
	 * 
	 * @param key
	 * @return
	 */
	public long hlen(String key) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.hlen(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hlen falid", e);
		}
		return result;
	}

	/**
	 * 返回哈希表 key 中域的数量。
	 * 
	 * 返回值： 哈希表中域的数量。 当 key 不存在时，返回 0 。
	 * 
	 * @param key
	 * @return
	 */
	public long hlen(byte[] key) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.hlen(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hlen falid", e);
		}
		return result;
	}

	/**
	 * 查看哈希表 key 中，给定域 field 是否存在。
	 * 
	 * 返回值： 如果哈希表含有给定域，返回 1 。 如果哈希表不含有给定域，或 key 不存在，返回 0 。
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public boolean hexists(String key, String field) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.hexists(key, field);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hexists falid", e);
		}
		return result;
	}

	/**
	 * 查看哈希表 key 中，给定域 field 是否存在。
	 * 
	 * 返回值： 如果哈希表含有给定域，返回 1 。 如果哈希表不含有给定域，或 key 不存在，返回 0 。
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public boolean hexists(byte[] key, byte[] field) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.hexists(key, field);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hexists falid", e);
		}
		return result;
	}

	/**
	 * 为哈希表 key 中的域 field 的值加上增量 increment 。 增量也可以为负数，相当于对给定域进行减法操作。 如果 key
	 * 不存在，一个新的哈希表被创建并执行 HINCRBY 命令。 如果域 field 不存在，那么在执行命令前，域的值被初始化为 0 。
	 * 对一个储存字符串值的域 field 执行 HINCRBY 命令将造成一个错误。 本操作的值被限制在 64 位(bit)有符号数字表示之内。
	 * 
	 * 返回值： 执行 HINCRBY 命令之后，哈希表 key 中域 field 的值。
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public long hincrBy(String key, String field, long value) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.hincrBy(key, field, value);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hincrBy falid", e);
		}
		return result;
	}

	/**
	 * 为哈希表 key 中的域 field 的值加上增量 increment 。 增量也可以为负数，相当于对给定域进行减法操作。 如果 key
	 * 不存在，一个新的哈希表被创建并执行 HINCRBY 命令。 如果域 field 不存在，那么在执行命令前，域的值被初始化为 0 。
	 * 对一个储存字符串值的域 field 执行 HINCRBY 命令将造成一个错误。 本操作的值被限制在 64 位(bit)有符号数字表示之内。
	 * 
	 * 返回值： 执行 HINCRBY 命令之后，哈希表 key 中域 field 的值。
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public long hincrBy(byte[] key, byte[] field, long value) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.hincrBy(key, field, value);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hincrBy falid", e);
		}
		return result;
	}

	/**
	 * 返回哈希表 key 中的所有域。
	 * 
	 * 返回值： 一个包含哈希表中所有域的表。 当 key 不存在时，返回一个空表。
	 * 
	 * @param key
	 * @return
	 */
	public Set<String> hkeys(String key) {
		Set<String> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.hkeys(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hkeys falid", e);
		}
		return result;
	}

	/**
	 * 返回哈希表 key 中的所有域。
	 * 
	 * 返回值： 一个包含哈希表中所有域的表。 当 key 不存在时，返回一个空表。
	 * 
	 * @param key
	 * @return
	 */
	public Set<byte[]> hkeys(byte[] key) {
		Set<byte[]> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.hkeys(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hkeys falid", e);
		}
		return result;
	}

	/**
	 * 返回哈希表 key 中所有域的值。
	 * 
	 * 返回值： 一个包含哈希表中所有值的表。 当 key 不存在时，返回一个空表。
	 * 
	 * @param key
	 * @return
	 */
	public List<String> hvals(String key) {
		List<String> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.hvals(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hvals falid", e);
		}
		return result;
	}

	/**
	 * 返回哈希表 key 中所有域的值。
	 * 
	 * 返回值： 一个包含哈希表中所有值的表。 当 key 不存在时，返回一个空表。
	 * 
	 * @param key
	 * @return
	 */
	public Collection<byte[]> hvals(byte[] key) {
		Collection<byte[]> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.hvals(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.hvals falid", e);
		}
		return result;
	}

	/* ==========================对Set(集合)操作====================== */
	/**
	 * 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略。 假如 key 不存在，则创建一个只包含
	 * member 元素作成员的集合。 当 key 不是集合类型时，返回一个错误。
	 * 
	 * 返回值: 被添加到集合中的新元素的数量，不包括被忽略的元素。
	 * 
	 * @param key
	 * @param members
	 * @return
	 */
	public long sadd(String key, String... members) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.sadd(key, members);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.sadd falid", e);
		}
		return result;
	}

	/**
	 * 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略。 假如 key 不存在，则创建一个只包含
	 * member 元素作成员的集合。 当 key 不是集合类型时，返回一个错误。
	 * 
	 * 返回值: 被添加到集合中的新元素的数量，不包括被忽略的元素。
	 * 
	 * @param key
	 * @param member
	 * @return
	 */
	public long sadd(byte[] key, byte[]... members) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.sadd(key, members);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.sadd falid", e);
		}
		return result;
	}

	/**
	 * 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略。 当 key 不是集合类型，返回一个错误。
	 * 
	 * 返回值: 被成功移除的元素的数量，不包括被忽略的元素。
	 * 
	 * @param key
	 * @param members
	 * @return
	 */
	public long srem(String key, String... members) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.srem(key, members);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.srem falid", e);
		}
		return result;
	}

	/**
	 * 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略。 当 key 不是集合类型，返回一个错误。
	 * 
	 * 返回值: 被成功移除的元素的数量，不包括被忽略的元素。
	 * 
	 * @param key
	 * @param member
	 * @return
	 */
	public long srem(byte[] key, byte[]... members) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.srem(key, members);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.srem falid", e);
		}
		return result;
	}

	/**
	 * 返回集合 key 中的所有成员。 不存在的 key 被视为空集合。
	 * 
	 * 返回值: 集合中的所有成员。
	 * 
	 * @param key
	 * @return
	 */
	public Set<String> smembers(String key) {
		Set<String> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.smembers(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.smembers falid", e);
		}
		return result;
	}

	/**
	 * 返回集合 key 中的所有成员。 不存在的 key 被视为空集合。
	 * 
	 * 返回值: 集合中的所有成员。
	 * 
	 * @param key
	 * @return
	 */
	public Set<byte[]> smembers(byte[] key) {
		Set<byte[]> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.smembers(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.smembers falid", e);
		}
		return result;
	}

	/**
	 * 判断 member 元素是否集合 key 的成员。
	 * 
	 * 返回值: 如果 member 元素是集合的成员，返回 1 。 如果 member 元素不是集合的成员，或 key 不存在，返回 0 。
	 * 
	 * @param key
	 * @param member
	 * @return
	 */
	public boolean sismember(String key, String member) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.sismember(key, member);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.sismember falid", e);
		}
		return result;
	}

	/**
	 * 判断 member 元素是否集合 key 的成员。
	 * 
	 * 返回值: 如果 member 元素是集合的成员，返回 1 。 如果 member 元素不是集合的成员，或 key 不存在，返回 0 。
	 * 
	 * @param key
	 * @param member
	 * @return
	 */
	public boolean sismember(byte[] key, byte[] member) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.sismember(key, member);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.sismember falid", e);
		}
		return result;
	}

	/**
	 * 返回集合 key 的基数(集合中元素的数量)。
	 * 
	 * 返回值： 集合的基数。 当 key 不存在时，返回 0 。
	 * 
	 * @param key
	 * @return
	 */
	public long scard(String key) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.scard(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.scard falid", e);
		}
		return result;
	}

	/**
	 * 返回集合 key 的基数(集合中元素的数量)。
	 * 
	 * 返回值： 集合的基数。 当 key 不存在时，返回 0 。
	 * 
	 * @param key
	 * @return
	 */
	public long scard(byte[] key) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.scard(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.scard falid", e);
		}
		return result;
	}

	/**
	 * 移除并返回集合中的一个随机元素。
	 * 
	 * 返回值: 被移除的随机元素。 当 key 不存在或 key 是空集时，返回 nil 。
	 * 
	 * @param key
	 * @return
	 */
	public String spop(String key) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.spop(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.spop falid", e);
		}
		return result;
	}

	/**
	 * 移除并返回集合中的一个随机元素。
	 * 
	 * 返回值: 被移除的随机元素。 当 key 不存在或 key 是空集时，返回 nil 。
	 * 
	 * @param key
	 * @return
	 */
	public byte[] spop(byte[] key) {
		byte[] result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.spop(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.spop falid", e);
		}
		return result;
	}

	/**
	 * 如果命令执行时，只提供了 key 参数，那么返回集合中的一个随机元素。该操作和 SPOP 相似，但 SPOP 将随机元素从集合中移除并返回，而
	 * SRANDMEMBER 则仅仅返回随机元素，而不对集合进行任何改动。
	 * 
	 * 返回值: 只提供 key 参数时，返回一个元素；如果集合为空，返回 nil 。 如果提供了 count
	 * 参数，那么返回一个数组；如果集合为空，返回空数组。
	 * 
	 * @param key
	 * @return
	 */
	public String srandmember(String key) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.srandmember(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.srandmember falid", e);
		}
		return result;
	}

	/**
	 * 如果命令执行时，只提供了 key 参数，那么返回集合中的一个随机元素。该操作和 SPOP 相似，但 SPOP 将随机元素从集合中移除并返回，而
	 * SRANDMEMBER 则仅仅返回随机元素，而不对集合进行任何改动。
	 * 
	 * 返回值: 只提供 key 参数时，返回一个元素；如果集合为空，返回 nil 。 如果提供了 count
	 * 参数，那么返回一个数组；如果集合为空，返回空数组。
	 * 
	 * @param key
	 * @return
	 */
	public byte[] srandmember(byte[] key) {
		byte[] result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.srandmember(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.srandmember falid", e);
		}
		return result;
	}

	/* ==========================对List(列表)操作====================== */
	/**
	 * 将一个或多个值 value 插入到列表 key 的表头 如果有多个 value 值，那么各个 value
	 * 值按从左到右的顺序依次插入到表头LPUSH mylist a b c ，列表的值将是 c b a;
	 * 
	 * 返回值： 执行 LPUSH 命令后，列表的长度。
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public long lpush(String key, String... values) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.lpush(key, values);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.lpush falid", e);
		}
		return result;
	}

	/**
	 * 将一个或多个值 value 插入到列表 key 的表头 如果有多个 value 值，那么各个 value
	 * 值按从左到右的顺序依次插入到表头LPUSH mylist a b c ，列表的值将是 c b a;
	 * 
	 * 返回值： 执行 LPUSH 命令后，列表的长度。
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public long lpush(byte[] key, byte[]... values) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.lpush(key, values);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.lpush falid", e);
		}
		return result;
	}

	/**
	 * 将值 value 插入到列表 key 的表头，当且仅当 key 存在并且是一个列表。当 key 不存在时， LPUSHX 命令什么也不做。
	 * 
	 * 返回值： LPUSHX 命令执行之后，表的长度。
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public long lpushx(String key, String value) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.lpushx(key, value);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.lpushx falid", e);
		}
		return result;
	}

	/**
	 * 将值 value 插入到列表 key 的表头，当且仅当 key 存在并且是一个列表。当 key 不存在时， LPUSHX 命令什么也不做。
	 * 
	 * 返回值： LPUSHX 命令执行之后，表的长度。
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public long lpushx(byte[] key, byte[] value) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.lpushx(key, value);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.lpushx falid", e);
		}
		return result;
	}

	/**
	 * 将一个或多个值 value 插入到列表 key 的表尾(最右边)。 如果有多个 value 值，那么各个 value
	 * 值按从左到右的顺序依次插入到表尾：比如对一个空列表 mylist 执行 RPUSH mylist a b c ，得出的结果列表为 a b c。如果
	 * key 不存在，一个空列表会被创建并执行 RPUSH 操作。 当 key 存在但不是列表类型时，返回一个错误。
	 * 
	 * 返回值： 执行 RPUSH 操作后，表的长度。
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public long rpush(String key, String... values) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.rpush(key, values);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.rpush falid", e);
		}
		return result;
	}

	/**
	 * 将一个或多个值 value 插入到列表 key 的表尾(最右边)。 如果有多个 value 值，那么各个 value
	 * 值按从左到右的顺序依次插入到表尾：比如对一个空列表 mylist 执行 RPUSH mylist a b c ，得出的结果列表为 a b c。如果
	 * key 不存在，一个空列表会被创建并执行 RPUSH 操作。 当 key 存在但不是列表类型时，返回一个错误。
	 * 
	 * 返回值： 执行 RPUSH 操作后，表的长度。
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public long rpush(byte[] key, byte[]... values) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.rpush(key, values);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.rpush falid", e);
		}
		return result;
	}

	/**
	 * 将值 value 插入到列表 key 的表尾，当且仅当 key 存在并且是一个列表。 和 RPUSH 命令相反，当 key 不存在时，
	 * RPUSHX 命令什么也不做。
	 * 
	 * 返回值： RPUSHX 命令执行之后，表的长度。
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public long rpushx(String key, String value) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.rpushx(key, value);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.rpushx falid", e);
		}
		return result;
	}

	/**
	 * 将值 value 插入到列表 key 的表尾，当且仅当 key 存在并且是一个列表。 和 RPUSH 命令相反，当 key 不存在时，
	 * RPUSHX 命令什么也不做。
	 * 
	 * 返回值： RPUSHX 命令执行之后，表的长度。
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public long rpushx(byte[] key, byte[] value) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.rpushx(key, value);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.rpushx falid", e);
		}
		return result;
	}

	/**
	 * 移除并返回列表 key 的头元素。
	 * 
	 * 返回值： 列表的头元素。 当 key 不存在时，返回 nil 。
	 * 
	 * @param key
	 * @return
	 */
	public String lpop(String key) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.lpop(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.lpop falid", e);
		}
		return result;
	}

	/**
	 * 移除并返回列表 key 的头元素。
	 * 
	 * 返回值： 列表的头元素。 当 key 不存在时，返回 nil 。
	 * 
	 * @param key
	 * @return
	 */
	public byte[] lpop(byte[] key) {
		byte[] result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.lpop(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.lpop falid", e);
		}
		return result;
	}

	/**
	 * 移除并返回列表 key 的尾元素。
	 * 
	 * 返回值： 列表的尾元素。 当 key 不存在时，返回 nil 。
	 * 
	 * @param key
	 * @return
	 */
	public String rpop(String key) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.rpop(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.rpop falid", e);
		}
		return result;
	}

	/**
	 * 移除并返回列表 key 的尾元素。
	 * 
	 * 返回值： 列表的尾元素。 当 key 不存在时，返回 nil 。
	 * 
	 * @param key
	 * @return
	 */
	public byte[] rpop(byte[] key) {
		byte[] result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.rpop(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.rpop falid", e);
		}
		return result;
	}

	/**
	 * 返回列表 key 的长度。 如果 key 不存在，则 key 被解释为一个空列表，返回 0 . 如果 key 不是列表类型，返回一个错误。
	 * 
	 * 返回值： 列表 key 的长度。
	 * 
	 * @param key
	 * @return
	 */
	public long llen(String key) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.llen(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.llen falid", e);
		}
		return result;
	}

	/**
	 * 返回列表 key 的长度。 如果 key 不存在，则 key 被解释为一个空列表，返回 0 . 如果 key 不是列表类型，返回一个错误。
	 * 
	 * 返回值： 列表 key 的长度。
	 * 
	 * @param key
	 * @return
	 */
	public long llen(byte[] key) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.llen(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.llen falid", e);
		}
		return result;
	}

	/**
	 * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定。
	 * 
	 * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
	 * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。 stop 下标也在 LRANGE
	 * 命令的取值范围之内(闭区间)。超出范围的下标值不会引起错误。
	 * 
	 * 返回值: 一个列表，包含指定区间内的元素。
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public List<String> lrange(String key, long start, long end) {
		List<String> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.lrange(key, start, end);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.lrange falid", e);
		}
		return result;
	}

	/**
	 * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定。
	 * 
	 * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
	 * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。 stop 下标也在 LRANGE
	 * 命令的取值范围之内(闭区间)。超出范围的下标值不会引起错误。
	 * 
	 * 返回值: 一个列表，包含指定区间内的元素。
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public List<byte[]> lrange(byte[] key, int start, int end) {
		List<byte[]> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.lrange(key, start, end);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.lrange falid", e);
		}
		return result;
	}

	/**
	 * 根据参数 count 的值，移除列表中与参数 value 相等的元素。
	 * 
	 * count 的值可以是以下几种： count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count 。 count
	 * < 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值。 count = 0 : 移除表中所有与
	 * value 相等的值。
	 * 
	 * 返回值： 被移除元素的数量。 因为不存在的 key 被视作空表(empty list)，所以当 key 不存在时， LREM 命令总是返回 0 。
	 * 
	 * @param key
	 * @param count
	 * @param value
	 * @return
	 */
	public long lrem(String key, long count, String value) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.lrem(key, count, value);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.lrem falid", e);
		}
		return result;
	}

	/**
	 * 根据参数 count 的值，移除列表中与参数 value 相等的元素。
	 * 
	 * count 的值可以是以下几种： count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count 。 count
	 * < 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值。 count = 0 : 移除表中所有与
	 * value 相等的值。
	 * 
	 * 返回值： 被移除元素的数量。 因为不存在的 key 被视作空表(empty list)，所以当 key 不存在时， LREM 命令总是返回 0 。
	 * 
	 * @param key
	 * @param count
	 * @param value
	 * @return
	 */
	public long lrem(byte[] key, int count, byte[] value) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.lrem(key, count, value);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.lrem falid", e);
		}
		return result;
	}

	/**
	 * 将列表 key 下标为 index 的元素的值设置为 value 。
	 * 
	 * 当 index 参数超出范围，或对一个空列表( key 不存在)进行 LSET 时，返回一个错误。
	 * 
	 * 返回值： 操作成功返回 ok ，否则返回错误信息。
	 * 
	 * @param key
	 * @param index
	 * @param value
	 * @return
	 */
	public boolean lset(String key, long index, String value) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			String status = jedis.lset(key, index, value);
			if ("OK".equalsIgnoreCase(status)) {
				result = true;
			}
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.lset falid", e);
		}
		return result;
	}

	/**
	 * 将列表 key 下标为 index 的元素的值设置为 value 。
	 * 
	 * 当 index 参数超出范围，或对一个空列表( key 不存在)进行 LSET 时，返回一个错误。
	 * 
	 * 返回值： 操作成功返回 ok ，否则返回错误信息。
	 * 
	 * @param key
	 * @param index
	 * @param value
	 * @return
	 */
	public boolean lset(byte[] key, int index, byte[] value) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			String status = jedis.lset(key, index, value);
			if ("OK".equalsIgnoreCase(status)) {
				result = true;
			}
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.lset falid", e);
		}
		return result;
	}

	/**
	 * 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。 举个例子，执行命令 LTRIM list
	 * 0 2 ，表示只保留列表 list 的前三个元素，其余元素全部删除。
	 * 
	 * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
	 * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。 当 key
	 * 不是列表类型时，返回一个错误。超出范围的下标值不会引起错误。
	 * 
	 * 返回值: 命令执行成功时，返回 ok 。
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public boolean ltrim(String key, long start, long end) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			String status = jedis.ltrim(key, start, end);
			if ("OK".equalsIgnoreCase(status)) {
				result = true;
			}
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.ltrim falid", e);
		}
		return result;
	}

	/**
	 * 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。 举个例子，执行命令 LTRIM list
	 * 0 2 ，表示只保留列表 list 的前三个元素，其余元素全部删除。
	 * 
	 * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
	 * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。 当 key
	 * 不是列表类型时，返回一个错误。超出范围的下标值不会引起错误。
	 * 
	 * 返回值: 命令执行成功时，返回 ok 。
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public boolean ltrim(byte[] key, int start, int end) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			String status = jedis.ltrim(key, start, end);
			if ("OK".equalsIgnoreCase(status)) {
				result = true;
			}
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.ltrim falid", e);
		}
		return result;
	}

	/**
	 * 返回列表 key 中，下标为 index 的元素。
	 * 
	 * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
	 * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。 如果 key 不是列表类型，返回一个错误。
	 * 
	 * 返回值: 列表中下标为 index 的元素。 如果 index 参数的值不在列表的区间范围内(out of range)，返回 nil 。
	 * 
	 * @param key
	 * @param index
	 * @return
	 */
	public String lindex(String key, long index) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.lindex(key, index);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.lindex falid", e);
		}
		return result;
	}

	/**
	 * 返回列表 key 中，下标为 index 的元素。
	 * 
	 * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
	 * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。 如果 key 不是列表类型，返回一个错误。
	 * 
	 * 返回值: 列表中下标为 index 的元素。 如果 index 参数的值不在列表的区间范围内(out of range)，返回 nil 。
	 * 
	 * @param key
	 * @param index
	 * @return
	 */
	public byte[] lindex(byte[] key, int index) {
		byte[] result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.lindex(key, index);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.lindex falid", e);
		}
		return result;
	}

	/**
	 * 将值 value 插入到列表 key 当中，位于值 pivot 之前或之后。
	 * 
	 * 当 pivot 不存在于列表 key 时，不执行任何操作。 当 key 不存在时， key 被视为空列表，不执行任何操作。 如果 key
	 * 不是列表类型，返回一个错误。
	 * 
	 * 返回值: 如果命令执行成功，返回插入操作完成之后，列表的长度。 如果没有找到 pivot ，返回 -1 。 如果 key 不存在或为空列表，返回
	 * 0 。
	 * 
	 * @param key
	 * @param where
	 * @param pivot
	 * @param value
	 * @return
	 */
	public long linsert(String key, BinaryClient.LIST_POSITION where,
			String pivot, String value) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.linsert(key, where, pivot, value);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.linsert falid", e);
		}
		return result;
	}

	/**
	 * 将值 value 插入到列表 key 当中，位于值 pivot 之前或之后。
	 * 
	 * 当 pivot 不存在于列表 key 时，不执行任何操作。 当 key 不存在时， key 被视为空列表，不执行任何操作。 如果 key
	 * 不是列表类型，返回一个错误。
	 * 
	 * 返回值: 如果命令执行成功，返回插入操作完成之后，列表的长度。 如果没有找到 pivot ，返回 -1 。 如果 key 不存在或为空列表，返回
	 * 0 。
	 * 
	 * @param key
	 * @param where
	 * @param pivot
	 * @param value
	 * @return
	 */
	public long linsert(byte[] key, BinaryClient.LIST_POSITION where,
			byte[] pivot, byte[] value) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.linsert(key, where, pivot, value);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.linsert falid", e);
		}
		return result;
	}

	/* ==========================对Sorted set(有序集)操作====================== */
	/**
	 * 将一个member 元素及其 score 值加入到有序集 key 当中。
	 * 
	 * 如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值，并通过重新插入这个 member 元素，来保证该
	 * member 在正确的位置上。 score 值可以是整数值或双精度浮点数。 如果 key 不存在，则创建一个空的有序集并执行 ZADD 操作。 当
	 * key 存在但不是有序集类型时，返回一个错误。
	 * 
	 * 返回值: 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
	 * 
	 * @param key
	 * @param score
	 * @param member
	 * @return
	 */
	public long zadd(String key, double score, String member) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zadd(key, score, member);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zadd falid", e);
		}
		return result;
	}

	/**
	 * 将一个member 元素及其 score 值加入到有序集 key 当中。
	 * 
	 * 如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值，并通过重新插入这个 member 元素，来保证该
	 * member 在正确的位置上。 score 值可以是整数值或双精度浮点数。 如果 key 不存在，则创建一个空的有序集并执行 ZADD 操作。 当
	 * key 存在但不是有序集类型时，返回一个错误。
	 * 
	 * 返回值: 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
	 * 
	 * @param key
	 * @param score
	 * @param member
	 * @return
	 */
	public long zadd(byte[] key, double score, byte[] member) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zadd(key, score, member);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zadd falid", e);
		}
		return result;
	}

	/**
	 * 将多个member 元素及其 score 值加入到有序集 key 当中。
	 * 
	 * 如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值，并通过重新插入这个 member 元素，来保证该
	 * member 在正确的位置上。 score 值可以是整数值或双精度浮点数。 如果 key 不存在，则创建一个空的有序集并执行 ZADD 操作。 当
	 * key 存在但不是有序集类型时，返回一个错误。
	 * 
	 * 返回值: 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
	 * 
	 * @param key
	 * @param scoreMembers
	 * @return
	 */
	public long zadd(String key, Map< String,Double> scoreMembers) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zadd(key, scoreMembers);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zadd falid", e);
		}
		return result;
	}

	/**
	 * 将多个member 元素及其 score 值加入到有序集 key 当中。
	 * 
	 * 如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值，并通过重新插入这个 member 元素，来保证该
	 * member 在正确的位置上。 score 值可以是整数值或双精度浮点数。 如果 key 不存在，则创建一个空的有序集并执行 ZADD 操作。 当
	 * key 存在但不是有序集类型时，返回一个错误。
	 * 
	 * 返回值: 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
	 * 
	 * @param key
	 * @param scoreMembers
	 * @return
	 */
	public long zadd(byte[] key, Map< byte[],Double> scoreMembers) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zadd(key, scoreMembers);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zadd falid", e);
		}
		return result;
	}

	/**
	 * 移除有序集 key 中的一个或多个成员，不存在的成员将被忽略。 当 key 存在但不是有序集类型时，返回一个错误。
	 * 
	 * 返回值: 被成功移除的成员的数量，不包括被忽略的成员。
	 * 
	 * @param key
	 * @param members
	 * @return
	 */
	public long zrem(String key, String... members) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zrem(key, members);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zrem falid", e);
		}
		return result;
	}

	/**
	 * 移除有序集 key 中的一个或多个成员，不存在的成员将被忽略。 当 key 存在但不是有序集类型时，返回一个错误。
	 * 
	 * 返回值: 被成功移除的成员的数量，不包括被忽略的成员。
	 * 
	 * @param key
	 * @param member
	 * @return
	 */
	public long zrem(byte[] key, byte[]... members) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zrem(key, members);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zrem falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 的集合中的数量。
	 * 
	 * 返回值: 当 key 存在且是有序集类型时，返回有序集的基数。 当 key 不存在时，返回 0 。
	 * 
	 * @param key
	 * @return
	 */
	public long zcard(String key) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zcard(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zcard falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 的集合中的数量。
	 * 
	 * 返回值: 当 key 存在且是有序集类型时，返回有序集的基数。 当 key 不存在时，返回 0 。
	 * 
	 * @param key
	 * @return
	 */
	public long zcard(byte[] key) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zcard(key);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zcard falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中， score 值在 min 和 max 之间(默认包括 score 值等于 min 或 max )的成员的数量。
	 * 
	 * 返回值: score 值在 min 和 max 之间的成员的数量。
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public long zcount(String key, double min, double max) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zcount(key, min, max);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zcount falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中， score 值在 min 和 max 之间(默认包括 score 值等于 min 或 max )的成员的数量。
	 * 
	 * 返回值: score 值在 min 和 max 之间的成员的数量。
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public long zcount(byte[] key, double min, double max) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zcount(key, min, max);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zcount falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中， member的score值在minmember和maxmember的score值之间(默认包括
	 * member的score值等于minmember或maxmemberscore值)的成员的数量。
	 * 
	 * 返回值: member的score值在minmember和maxmember的score值之间的成员的数量。
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public long zcount(String key, String minmember, String maxmember) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zcount(key, minmember, maxmember);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zcount falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中， member的score值在minmember和maxmember的score值之间(默认包括
	 * member的score值等于minmember或maxmemberscore值)的成员的数量。
	 * 
	 * 返回值: member的score值在minmember和maxmember的score值之间的成员的数量。
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public long zcount(byte[] key, byte[] minmember, byte[] maxmember) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zcount(key, minmember, maxmember);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zcount falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中，成员 member 的 score 值。如果 member 元素不是有序集 key 的成员，或 key 不存在，返回
	 * nil 。
	 * 
	 * 返回值: member 成员的 score 值，以字符串形式表示。
	 * 
	 * @param key
	 * @param member
	 * @return
	 */
	public double zscore(String key, String member) {
		double result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zscore(key, member);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zscore falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中，成员 member 的 score 值。如果 member 元素不是有序集 key 的成员，或 key 不存在，返回
	 * nil 。
	 * 
	 * 返回值: member 成员的 score 值，以字符串形式表示。
	 * 
	 * @param key
	 * @param member
	 * @return
	 */
	public double zscore(byte[] key, byte[] member) {
		double result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zscore(key, member);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zscore falid", e);
		}
		return result;
	}

	/**
	 * 为有序集 key 的成员 member 的 score 值加上增量 increment 。
	 * 
	 * 可以通过传递一个负数值 increment ，让 score 减去相应的值，比如 ZINCRBY key -5 member ，就是让
	 * member 的 score 值减去 5 。 当 key 不存在，或 member 不是 key 的成员时， ZINCRBY key
	 * increment member 等同于 ZADD key increment member 。 当 key 不是有序集类型时，返回一个错误。
	 * score 值可以是整数值或双精度浮点数。
	 * 
	 * 返回值: member 成员的新 score 值，以字符串形式表示。
	 * 
	 * @param key
	 * @param score
	 * @param member
	 * @return
	 */
	public double zincrby(String key, double score, String member) {
		double result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zincrby(key, score, member);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zincrby falid", e);
		}
		return result;
	}

	/**
	 * 为有序集 key 的成员 member 的 score 值加上增量 increment 。
	 * 
	 * 可以通过传递一个负数值 increment ，让 score 减去相应的值，比如 ZINCRBY key -5 member ，就是让
	 * member 的 score 值减去 5 。 当 key 不存在，或 member 不是 key 的成员时， ZINCRBY key
	 * increment member 等同于 ZADD key increment member 。 当 key 不是有序集类型时，返回一个错误。
	 * score 值可以是整数值或双精度浮点数。
	 * 
	 * 返回值: member 成员的新 score 值，以字符串形式表示。
	 * 
	 * @param key
	 * @param score
	 * @param member
	 * @return
	 */
	public double zincrby(byte[] key, double score, byte[] member) {
		double result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zincrby(key, score, member);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zincrby falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中，指定区间内的成员。 其中成员的位置按 score 值递增(从小到大)来排序。 具有相同 score
	 * 值的成员按字典序(lexicographical order )来排列。如果你需要成员按 score 值递减(从大到小)来排列，请使用
	 * ZREVRANGE 命令。
	 * 
	 * 下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。
	 * 你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推。 超出范围的下标并不会引起错误。
	 * 
	 * 返回值: 指定区间内，带有 score 值(可选)的有序集成员的列表。
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public Set<String> zrange(String key, long start, long end) {
		Set<String> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zrange(key, start, end);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zrange falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中，指定区间内的成员。 其中成员的位置按 score 值递增(从小到大)来排序。 具有相同 score
	 * 值的成员按字典序(lexicographical order )来排列。如果你需要成员按 score 值递减(从大到小)来排列，请使用
	 * ZREVRANGE 命令。
	 * 
	 * 下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。
	 * 你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推。 超出范围的下标并不会引起错误。
	 * 
	 * 返回值: 指定区间内，带有 score 值(可选)的有序集成员的列表。
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public Set<byte[]> zrange(byte[] key, int start, int end) {
		Set<byte[]> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zrange(key, start, end);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zrange falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score
	 * 值递增(从小到大)次序排列。 具有相同 score 值的成员按字典序(lexicographical
	 * order)来排列(该属性是有序集提供的，不需要额外的计算)。
	 * 
	 * 返回值: 指定区间内，带有 score 值(可选)的有序集成员的列表。
	 * 
	 * @param key
	 * @param max
	 * @param min
	 * @return
	 */
	public Set<String> zrangeByScore(String key, double min, double max) {
		Set<String> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zrangeByScore(key, min, max);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zrangeByScore falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score
	 * 值递增(从小到大)次序排列。 具有相同 score 值的成员按字典序(lexicographical
	 * order)来排列(该属性是有序集提供的，不需要额外的计算)。
	 * 
	 * 返回值: 指定区间内，带有 score 值(可选)的有序集成员的列表。
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public Set<byte[]> zrangeByScore(byte[] key, double min, double max) {
		Set<byte[]> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zrangeByScore(key, min, max);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zrangeByScore falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score
	 * 值递增(从小到大)次序排列。 具有相同 score 值的成员按字典序(lexicographical
	 * order)来排列(该属性是有序集提供的，不需要额外的计算)。
	 * 
	 * 可选的 LIMIT 参数指定返回结果的数量及区间(就像SQL中的 SELECT LIMIT offset, count )，注意当 offset
	 * 很大时，定位 offset 的操作可能需要遍历整个有序集，此过程最坏复杂度为 O(N) 时间。
	 * 
	 * 返回值: 指定区间内，带有 score 值(可选)的有序集成员的列表。
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @param offset
	 * @param count
	 * @return
	 */
	public Set<String> zrangeByScore(String key, double min, double max,
			int offset, int count) {
		Set<String> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zrangeByScore(key, min, max, offset, count);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zrangeByScore falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score
	 * 值递增(从小到大)次序排列。 具有相同 score 值的成员按字典序(lexicographical
	 * order)来排列(该属性是有序集提供的，不需要额外的计算)。
	 * 
	 * 可选的 LIMIT 参数指定返回结果的数量及区间(就像SQL中的 SELECT LIMIT offset, count )，注意当 offset
	 * 很大时，定位 offset 的操作可能需要遍历整个有序集，此过程最坏复杂度为 O(N) 时间。
	 * 
	 * 返回值: 指定区间内，带有 score 值(可选)的有序集成员的列表。
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @param offset
	 * @param count
	 * @return
	 */
	public Set<byte[]> zrangeByScore(byte[] key, double min, double max,
			int offset, int count) {
		Set<byte[]> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zrangeByScore(key, min, max, offset, count);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zrangeByScore falid",e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中，所有 member的score 值介于 minmember 和 maxmember 的score 值之间(包括等于
	 * minmember 或 maxmember 的score 值 )的成员。有序集成员按 score 值递增(从小到大)次序排列。 具有相同
	 * score 值的成员按字典序(lexicographical order)来排列(该属性是有序集提供的，不需要额外的计算)。
	 * 
	 * 返回值: 指定区间内，带有 score 值(可选)的有序集成员的列表。
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public Set<String> zrangeByScore(String key, String minmember,
			String maxmember) {
		Set<String> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zrangeByScore(key, minmember, maxmember);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zrangeByScore falid",e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中，所有 member的score 值介于 minmember 和 maxmember 的score 值之间(包括等于
	 * minmember 或 maxmember 的score 值 )的成员。有序集成员按 score 值递增(从小到大)次序排列。 具有相同
	 * score 值的成员按字典序(lexicographical order)来排列(该属性是有序集提供的，不需要额外的计算)。
	 * 
	 * 可选的 LIMIT 参数指定返回结果的数量及区间(就像SQL中的 SELECT LIMIT offset, count )，注意当 offset
	 * 很大时，定位 offset 的操作可能需要遍历整个有序集，此过程最坏复杂度为 O(N) 时间。
	 * 
	 * 返回值: 指定区间内，带有 score 值(可选)的有序集成员的列表。
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @param offset
	 * @param count
	 * @return
	 */
	public Set<String> zrangeByScore(String key, String minmember,
			String maxmember, int offset, int count) {
		Set<String> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zrangeByScore(key, minmember, maxmember, offset,
					count);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zrangeByScore falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score
	 * 值递减(从大到小)的次序排列。具有相同 score 值的成员按字典序的逆序(reverse lexicographical order )排列。
	 * 
	 * 返回值: 指定区间内，带有 score 值(可选)的有序集成员的列表。
	 * 
	 * @param key
	 * @param max
	 * @param min
	 * @return
	 */
	public Set<String> zrevrangeByScore(String key, double max, double min) {
		Set<String> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zrevrangeByScore(key, max, min);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zrevrangeByScore falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score
	 * 值递减(从大到小)的次序排列。具有相同 score 值的成员按字典序的逆序(reverse lexicographical order )排列。
	 * 
	 * 返回值: 指定区间内，带有 score 值(可选)的有序集成员的列表。
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public Set<byte[]> zrevrangeByScore(byte[] key, double max, double min) {
		Set<byte[]> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zrevrangeByScore(key, max, min);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zrevrangeByScore falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score
	 * 值递减(从大到小)的次序排列。具有相同 score 值的成员按字典序的逆序(reverse lexicographical order )排列。
	 * 
	 * 可选的 LIMIT 参数指定返回结果的数量及区间(就像SQL中的 SELECT LIMIT offset, count )，注意当 offset
	 * 很大时，定位 offset 的操作可能需要遍历整个有序集，此过程最坏复杂度为 O(N) 时间。
	 * 
	 * 返回值: 指定区间内，带有 score 值(可选)的有序集成员的列表。
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @param offset
	 * @param count
	 * @return
	 */
	public Set<String> zrevrangeByScore(String key, double max, double min,
			int offset, int count) {
		Set<String> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zrevrangeByScore(key, max, min, offset, count);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zrevrangeByScore falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score
	 * 值递减(从大到小)的次序排列。具有相同 score 值的成员按字典序的逆序(reverse lexicographical order )排列。
	 * 
	 * 可选的 LIMIT 参数指定返回结果的数量及区间(就像SQL中的 SELECT LIMIT offset, count )，注意当 offset
	 * 很大时，定位 offset 的操作可能需要遍历整个有序集，此过程最坏复杂度为 O(N) 时间。
	 * 
	 * 返回值: 指定区间内，带有 score 值(可选)的有序集成员的列表。
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @param offset
	 * @param count
	 * @return
	 */
	public Set<byte[]> zrevrangeByScore(byte[] key, double max, double min,
			int offset, int count) {
		Set<byte[]> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zrevrangeByScore(key, max, min, offset, count);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zrevrangeByScore falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中，所有 member的score 值介于 的score 值之间(包括等于 maxmember 和 minmember
	 * 的score 值 )的成员。有序集成员按 score 值递增(从大到小)次序排列。 具有相同 score 值的成员按字典逆序(reverse
	 * lexicographical order )来排列(该属性是有序集提供的，不需要额外的计算)。
	 * 
	 * 返回值: 指定区间内，带有 score 值(可选)的有序集成员的列表。
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public Set<String> zrevrangeByScore(String key, String maxmember,
			String minmember) {
		Set<String> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zrevrangeByScore(key, maxmember, minmember);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zrevrangeByScore falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中，所有 member的score 值介于 的score 值之间(包括等于 maxmember 和 minmember
	 * 的score 值 )的成员。有序集成员按 score 值递增(从大到小)次序排列。 具有相同 score 值的成员按字典逆序(reverse
	 * lexicographical order )来排列(该属性是有序集提供的，不需要额外的计算)。
	 * 
	 * 可选的 LIMIT 参数指定返回结果的数量及区间(就像SQL中的 SELECT LIMIT offset, count )，注意当 offset
	 * 很大时，定位 offset 的操作可能需要遍历整个有序集，此过程最坏复杂度为 O(N) 时间。
	 * 
	 * 返回值: 指定区间内，带有 score 值(可选)的有序集成员的列表。
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @param offset
	 * @param count
	 * @return
	 */
	public Set<String> zrevrangeByScore(String key, String maxmember,
			String minmember, int offset, int count) {
		Set<String> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zrevrangeByScore(key, maxmember, minmember, offset,
					count);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zrevrangeByScore falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递增(从小到大)顺序排列。 排名以 0 为底，也就是说，
	 * score 值最小的成员排名为 0 。
	 * 
	 * 返回值: 如果 member 是有序集 key 的成员，返回 member 的排名。 如果 member 不是有序集 key 的成员，返回 nil
	 * 。
	 * 
	 * @param key
	 * @param member
	 * @return
	 */
	public long zrank(String key, String member) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zrank(key, member);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zrank falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递增(从小到大)顺序排列。 排名以 0 为底，也就是说，
	 * score 值最小的成员排名为 0 。
	 * 
	 * 返回值: 如果 member 是有序集 key 的成员，返回 member 的排名。 如果 member 不是有序集 key 的成员，返回 nil
	 * 。
	 * 
	 * @param key
	 * @param member
	 * @return
	 */
	public long zrank(byte[] key, byte[] member) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zrank(key, member);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zrank falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递减(从大到小)排序。 排名以 0 为底，也就是说， score
	 * 值最大的成员排名为 0 。
	 * 
	 * 返回值: 如果 member 是有序集 key 的成员，返回 member 的排名。 如果 member 不是有序集 key 的成员，返回 nil
	 * 。
	 * 
	 * @param key
	 * @param member
	 * @return
	 */
	public long zrevrank(String key, String member) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zrevrank(key, member);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zrevrank falid", e);
		}
		return result;
	}

	/**
	 * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递减(从大到小)排序。 排名以 0 为底，也就是说， score
	 * 值最大的成员排名为 0 。
	 * 
	 * 返回值: 如果 member 是有序集 key 的成员，返回 member 的排名。 如果 member 不是有序集 key 的成员，返回 nil
	 * 。
	 * 
	 * @param key
	 * @param member
	 * @return
	 */
	public long zrevrank(byte[] key, byte[] member) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zrevrank(key, member);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zrevrank falid", e);
		}
		return result;
	}

	/**
	 * 移除有序集 key 中，指定排名(rank)区间内的所有成员。
	 * 
	 * 区间分别以下标参数 start 和 stop 指出，包含 start 和 stop 在内。 下标参数 start 和 stop 都以 0
	 * 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。 你也可以使用负数下标，以 -1 表示最后一个成员， -2
	 * 表示倒数第二个成员，以此类推。
	 * 
	 * 返回值: 被移除成员的数量。
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public long zremrangeByRank(String key, long start, long end) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zremrangeByRank(key, start, end);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zremrangeByRank falid", e);
		}
		return result;
	}

	/**
	 * 移除有序集 key 中，指定排名(rank)区间内的所有成员。
	 * 
	 * 区间分别以下标参数 start 和 stop 指出，包含 start 和 stop 在内。 下标参数 start 和 stop 都以 0
	 * 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。 你也可以使用负数下标，以 -1 表示最后一个成员， -2
	 * 表示倒数第二个成员，以此类推。
	 * 
	 * 返回值: 被移除成员的数量。
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public long zremrangeByRank(byte[] key, int start, int end) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zremrangeByRank(key, start, end);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zremrangeByRank falid", e);
		}
		return result;
	}

	/**
	 * 移除有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
	 * 
	 * 返回值: 被移除成员的数量。
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public long zremrangeByScore(String key, double start, double end) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zremrangeByScore(key, start, end);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zremrangeByScore falid",e);
		}
		return result;
	}

	/**
	 * 移除有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
	 * 
	 * 返回值: 被移除成员的数量。
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public long zremrangeByScore(byte[] key, double start, double end) {
		long result = -10000;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.zremrangeByScore(key, start, end);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.zremrangeByScore falid", e);
		}
		return result;
	}

	/**
	 * 执行lua脚本
	 * 
	 * @param script
	 * @param keyCount
	 * @param params
	 * @return
	 */
	public Object eval(String script, int keyCount, String... params) {
		Object result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.eval(script, keyCount, params);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.eval falid", e);
		}
		return result;
	}

	/**
	 * 执行lua脚本
	 * 
	 * @param script
	 * @param keys
	 * @param args
	 * @return
	 */
	public Object eval(String script, List<String> keys, List<String> args) {
		Object result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.eval(script, keys, args);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.eval falid", e);
		}
		return result;
	}

	/**
	 * 执行lua脚本
	 * 
	 * @param sha1
	 *            脚本校验码
	 * @param keyCount
	 * @param params
	 * @return
	 */
	public Object evalsha(String sha1, int keyCount, String... params) {
		Object result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.evalsha(sha1, keyCount, params);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.evalsha falid", e);
		}
		return result;
	}

	/**
	 * 执行lua脚本
	 * 
	 * @param sha1
	 * @param keys
	 * @param args
	 * @return
	 */
	public Object evalsha(String sha1, List<String> keys, List<String> args) {
		Object result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.evalsha(sha1, keys, args);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.evalsha falid", e);
		}
		return result;
	}

	/**
	 * 判断脚本是否存在
	 * 
	 * @param sha1
	 * @return
	 */
	public Boolean scriptExists(String sha1) {
		Boolean result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.scriptExists(sha1);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.scriptExists falid", e);
		}
		return result;
	}

	/**
	 * 判断脚本是否存在
	 * 
	 * @param sha1
	 * @return
	 */
	public List<Boolean> scriptExists(String... sha1) {
		List<Boolean> result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.scriptExists(sha1);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.scriptExists falid", e);
		}
		return result;
	}

	/**
	 * 加载脚本
	 * 
	 * @param script
	 * @return
	 */
	public String scriptLoad(String script) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.scriptLoad(script);
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.scriptLoad falid", e);
		}
		return result;
	}

	/**
	 * 杀掉正在运行的脚本
	 * 
	 * @return
	 */
	public String scriptKill() {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.scriptKill();//new String(jedis.scriptKill(), "UTF-8");
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.scriptKill falid", e);
		}
		return result;
	}

	/**
	 * 清除所有脚本
	 * 
	 * @return
	 */
	public String scriptFlush() {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			result = jedis.scriptFlush();//new String(jedis.scriptFlush(), "UTF-8");
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.scriptFlush falid", e);
		}
		return result;
	}

	/* ==========================Server(服务器)的管理====================== */
	/**
	 * 清空整个 Redis 服务器的数据(删除所有数据库的所有 key )。 此命令从不失败。
	 * 
	 * 返回值： 总是返回 OK 。
	 * 
	 * @param host
	 * @param port
	 * @return
	 */
	public boolean flushAll() {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = borrowResource();
			String status = jedis.flushAll();
			if ("OK".equalsIgnoreCase(status)) {
				result = true;
			}
			returnResource(jedis);
		} catch (Exception e) {
			returnBrokenResource(jedis);
			logger.error("SingleServerJedisCache.flushAll falid", e);
		}
		return result;
	}
	
	
	 private static final String LOCK_SUCCESS = "OK";
	 private static final String SET_IF_NOT_EXIST = "NX";
	 private static final String SET_WITH_EXPIRE_TIME = "PX";
	 
	 /**
	     * 尝试获取分布式锁
	     * @param jedis Redis客户端
	     * @param lockKey 锁
	     * @param requestId 请求标识
	     * @param expireTime 超期时间
	     * @return 是否获取成功
	     */
	    public   boolean tryGetDistributedLock(String lockKey, String requestId, int expireTime) {
	    	Jedis jedis = null;
	    	try{
	    		jedis = borrowResource();
		        String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
		        returnResource(jedis);
		        if (LOCK_SUCCESS.equals(result)) {
		            return true;
		        }
		        return false;
	    	} catch (Exception e) {
				returnBrokenResource(jedis);
				logger.error("MasterSingleServerJedisCache.tryGetDistributedLock falid", e);
				return false;
			}
	    }
	    
	    private static final Long RELEASE_SUCCESS = 1L;

	    /**
	     * 释放分布式锁
	     * @param jedis Redis客户端
	     * @param lockKey 锁
	     * @param requestId 请求标识
	     * @return 是否释放成功
	     */
	    public   boolean releaseDistributedLock(String lockKey, String requestId) {
	    	Jedis jedis = null;
	    	try{
	    		jedis = borrowResource();
		        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
		        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
		        returnResource(jedis);
		        if (RELEASE_SUCCESS.equals(result)) {
		            return true;
		        }
		        return false;
			} catch (Exception e) {
				returnBrokenResource(jedis);
				logger.error("MasterSingleServerJedisCache.releaseDistributedLock falid", e);
				return false;
			}
	    }
}
