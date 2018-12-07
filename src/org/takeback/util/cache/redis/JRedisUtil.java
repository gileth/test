package org.takeback.util.cache.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.takeback.util.SerializeUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.SortingParams;


 


/**
 * @author xiongguohui
 * 操作redis的具体方法类
 */
public class JRedisUtil {
	private static Logger logger = Logger.getLogger(JRedisUtil.class);
	private static MasterSingleServerJedisCache masterSingleServerJedisCache = null;
	
	private static SlaveJedisCache slaveJedisCache = null;
	
	static{
		try {
			masterSingleServerJedisCache = new MasterSingleServerJedisCache();
			slaveJedisCache = new SlaveJedisCache();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return
	 */
	public static MasterSingleServerJedisCache getMasterSingleServerJedisCache(){
		 
		return masterSingleServerJedisCache;
	}
	
	/**
	 * @return
	 */
	public static SlaveJedisCache getSlaveJedisCache(){
		
		return slaveJedisCache;
	}
	
	/**
	 * 
	 * 往redis写入对象
	 * @param key
	 * @param obj
	 * @return
	 * 
	 */
	public static boolean writObjectToeCacheWithKey(String key,Object obj){
		logger.info("key:"+key);
		if(key==null||obj==null){return false;}
		boolean isok =masterSingleServerJedisCache.set(key.getBytes(), SerializeUtil.serialize(obj));
		return isok;
	}
	
	/**
	 * 从
	 * @param <T>
	 * @param key
	 * @param t
	 * @return
	 */
	public static<T> T getObjectFromMasterCache(String key ,Class<T> t){
		if (masterSingleServerJedisCache.get(key.getBytes()) != null) {
			Object obj = SerializeUtil.unserialize(masterSingleServerJedisCache.get(key.getBytes()));
			return t.cast(obj);
		} else {
			return null;
		}
	}
	
	/**
	 * @param <T>
	 * @param key
	 * @param t
	 * @return
	 */
	public static<T> T getObjectFromSlaveCache(String key ,Class<T> t){
		if (slaveJedisCache.get(key.getBytes()) != null) {
			Object obj = SerializeUtil.unserialize(slaveJedisCache.get(key.getBytes()));
			return t.cast(obj);
		} else {
			return null;
		}
	}
	
	
	public static long append(String key, String value) {
		return slaveJedisCache.append(key, value);
	}
	
	public static List<String> sort(String key, SortingParams sortingParameters) {
		return slaveJedisCache.sort(key, sortingParameters);
	}
	
	
	public static long lpush(String key, String... values) {
		return slaveJedisCache.lpush(key, values);
	}
	
	public static List<String> lrange(String key, long start, long end) {
		return slaveJedisCache.lrange(key, start, end);
	}
	
	public static List<String> hmget(String key, String... fields) {
		return slaveJedisCache.hmget(key, fields);
	}
	
	public static long hset(String key, String field, String value) {
		return slaveJedisCache.hset(key, field, value);
	}
	
	/**
	 * 设置一个key的过期时间（单位：秒）
	 * 
	 * @param key
	 *            key值
	 * @param seconds
	 *            多少秒后过期
	 * @return 1：设置了过期时间 0：没有设置过期时间/不能设置过期时间
	 */
	public static long expire(String key, int seconds) {
		if (key == null || key.equals("")) {
			return 0;
		}
		
		try {
			return slaveJedisCache.expire(key, seconds);
		} catch (Exception ex) {
			logger.error("EXPIRE error[key=" + key + " seconds=" + seconds + "]" + ex.getMessage(), ex);
			
		} 
		return 0;
	}

	/**
	 * 设置一个key在某个时间点过期
	 * 
	 * @param key
	 *            key值
	 * @param unixTimestamp
	 *            unix时间戳，从1970-01-01 00:00:00开始到现在的秒数
	 * @return 1：设置了过期时间 0：没有设置过期时间/不能设置过期时间
	 */
	public static long expireAt(String key, int unixTimestamp) {
		if (key == null || key.equals("")) {
			return 0;
		}
		
		try {
			return slaveJedisCache.expire(key, unixTimestamp);
		} catch (Exception ex) {
			logger.error("EXPIRE error[key=" + key + " unixTimestamp=" + unixTimestamp + "]" + ex.getMessage(), ex);
			
		} 
		return 0;
	}


	/**
	 * 添加到Set中（同时设置过期时间）
	 * 
	 * @param key
	 *            key值
	 * @param seconds
	 *            过期时间 单位s
	 * @param value
	 * @return
	 */
	public static boolean addSet(String key, int seconds, String... value) {
		boolean result = addSet(key, value);
		if (result) {
			long i = expire(key, seconds);
			return i == 1;
		}
		return false;
	}

	/**
	 * 添加到Set中
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean addSet(String key, String... value) {
		if (key == null || value == null) {
			return false;
		}
		
		try {
			slaveJedisCache.sadd(key, value);
			return true;
		} catch (Exception ex) {
			logger.error("setList error.", ex);
			
		} 
		return false;
	}

	/**
	 * @param key
	 * @param value
	 * @return 判断值是否包含在set中
	 */
	public static boolean containsInSet(String key, String value) {
		if (key == null || value == null) {
			return false;
		}
		
		try {
			return slaveJedisCache.sismember(key, value);
		} catch (Exception ex) {
			logger.error("setList error.", ex);
			
		} 
		return false;
	}

	/**
	 * 获取Set
	 * 
	 * @param key
	 * @return
	 */
	public static Set<String> getSet(String key) {
		
		try {
			return slaveJedisCache.smembers(key);
		} catch (Exception ex) {
			logger.error("getList error.", ex);
			
		} 
		return null;
	}

	/**
	 * 从set中删除value
	 * 
	 * @param key
	 * @return
	 */
	public static boolean removeSetValue(String key, String... value) {
		
		try {
			slaveJedisCache.srem(key, value);
			return true;
		} catch (Exception ex) {
			logger.error("getList error.", ex);
			
		} 
		return false;
	}

	/**
	 * 从list中删除value 默认count 1
	 * 
	 * @param key
	 * @param values
	 *            值list
	 * @return
	 */
	public static int removeListValue(String key, List<String> values) {
		return removeListValue(key, 1, values);
	}

	/**
	 * 从list中删除value
	 * 
	 * @param key
	 * @param count
	 * @param values
	 *            值list
	 * @return
	 */
	public static int removeListValue(String key, long count, List<String> values) {
		int result = 0;
		if (values != null && values.size() > 0) {
			for (String value : values) {
				if (removeListValue(key, count, value)) {
					result++;
				}
			}
		}
		return result;
	}

	/**
	 * 从list中删除value
	 * 
	 * @param key
	 * @param count
	 *            要删除个数
	 * @param value
	 * @return
	 */
	public static boolean removeListValue(String key, long count, String value) {
		
		try {
			slaveJedisCache.lrem(key, count, value);
			return true;
		} catch (Exception ex) {
			logger.error("getList error.", ex);
			
		} 
		return false;
	}

	/**
	 * 截取List
	 * 
	 * @param key
	 * @param start
	 *            起始位置
	 * @param end
	 *            结束位置
	 * @return
	 */
	public static List<String> rangeList(String key, long start, long end) {
		if (key == null || key.equals("")) {
			return null;
		}
		try {
			return slaveJedisCache.lrange(key, start, end);
		} catch (Exception ex) {
			logger.error("rangeList 出错[key=" + key + " start=" + start + " end=" + end + "]" + ex.getMessage(), ex);
		} 
		return null;
	}

	/**
	 * 检查List长度
	 * 
	 * @param key
	 * @return
	 */
	public static long countList(String key) {
		if (key == null) {
			return 0;
		}
		try {
			return slaveJedisCache.llen(key);
		} catch (Exception ex) {
			logger.error("countList error.", ex);
		} 
		return 0;
	}

	/**
	 * 添加到List中（同时设置过期时间）
	 * 
	 * @param key
	 *            key值
	 * @param seconds
	 *            过期时间 单位s
	 * @param value
	 * @return
	 */
	public static boolean addList(String key, int seconds, String... value) {
		boolean result = addList(key, value);
		if (result) {
			long i = expire(key, seconds);
			return i == 1;
		}
		return false;
	}

	/**
	 * 添加到List
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean addList(String key, String... value) {
		if (key == null || value == null) {
			return false;
		}
		try {
			slaveJedisCache.lpush(key, value);
			return true;
		} catch (Exception ex) {
			logger.error("setList error.", ex);
		} 
		return false;
	}

	/**
	 * 添加到List(只新增)
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean addList(String key, List<String> list) {
		if (key == null || list == null || list.size() == 0) {
			return false;
		}
		for (String value : list) {
			addList(key, value);
		}
		return true;
	}

	/**
	 * 获取List
	 * 
	 * @param key
	 * @return
	 */
	public static List<String> getList(String key) {
		try {
			return slaveJedisCache.lrange(key, 0, -1);
		} catch (Exception ex) {
			logger.error("getList error.", ex);
		} 
		return null;
	}

	/**
	 * 设置HashSet对象
	 * 
	 * @param domain
	 *            域名
	 * @param key
	 *            键值
	 * @param value
	 *            Json String or String value
	 * @return
	 */
	public static boolean setHSet(String domain, String key, String value) {
		if (value == null)
			return false;
		try {
			slaveJedisCache.hset(domain, key, value);
			return true;
		} catch (Exception ex) {
			logger.error("setHSet error.", ex);
		} 
		return false;
	}

	/**
	 * 获得HashSet对象
	 * 
	 * @param domain
	 *            域名
	 * @param key
	 *            键值
	 * @return Json String or String value
	 */
	public static String getHSet(String domain, String key) {
		try {
			return slaveJedisCache.hget(domain, key);
		} catch (Exception ex) {
			logger.error("getHSet error.", ex);
		} 
		return null;
	}

	/**
	 * 删除HashSet对象
	 * 
	 * @param domain
	 *            域名
	 * @param key
	 *            键值
	 * @return 删除的记录数
	 */
	public static long delHSet(String domain, String key) {
		long count = 0;
		try {
			count = slaveJedisCache.hdel(domain, key);
		} catch (Exception ex) {
			logger.error("delHSet error.", ex);
		} 
		return count;
	}

	/**
	 * 删除HashSet对象
	 * 
	 * @param domain
	 *            域名
	 * @param key
	 *            键值
	 * @return 删除的记录数
	 */
	public static long delHSet(String domain, String... key) {
		long count = 0;
		try {
			count = slaveJedisCache.hdel(domain, key);
		} catch (Exception ex) {
			logger.error("delHSet error.", ex);
		} 
		return count;
	}

	/**
	 * 判断key是否存在
	 * 
	 * @param domain
	 *            域名
	 * @param key
	 *            键值
	 * @return
	 */
	public static boolean existsHSet(String domain, String key) {
		boolean isExist = false;
		try {
			isExist = slaveJedisCache.hexists(domain, key);
		} catch (Exception ex) {
			logger.error("existsHSet error.", ex);
		} 
		return isExist;
	}



	/**
	 * 返回 domain 指定的哈希集中所有字段的value值
	 * 
	 * @param domain
	 * @return
	 */
	public static List<String> hvals(String domain) {
		List<String> retList = null;
		try {
			retList = slaveJedisCache.hvals(domain);
		} catch (Exception ex) {
			logger.error("hvals error.", ex);
		} 
		return retList;
	}

	/**
	 * 返回 domain 指定的哈希集中所有字段的key值
	 * 
	 * @param domain
	 * @return
	 */
	public static Set<String> hkeys(String domain) {
		Set<String> retList = null;
		try {
			retList = slaveJedisCache.hkeys(domain);
		} catch (Exception ex) {
			logger.error("hkeys error.", ex);
		} 
		return retList;
	}

	/**
	 * 返回 domain 指定的哈希key值总数
	 * 
	 * @param domain
	 * @return
	 */
	public static long lenHset(String domain) {
		long retList = 0;
		try {
			retList = slaveJedisCache.hlen(domain);
		} catch (Exception ex) {
			logger.error("hkeys error.", ex);
		} 
		return retList;
	}

	/**
	 * 设置排序集合
	 * 
	 * @param key
	 * @param score
	 * @param value
	 * @return
	 */
	public static boolean setSortedSet(String key, long score, String value) {
		try {
			slaveJedisCache.zadd(key, score, value);
			return true;
		} catch (Exception ex) {
			logger.error("setSortedSet error.", ex);
		} 
		return false;
	}

	/**
	 * 获得排序集合
	 * 
	 * @param key
	 * @param startScore
	 * @param endScore
	 * @param orderByDesc
	 * @return
	 */
	public static Set<String> getSoredSet(String key, long startScore, long endScore, boolean orderByDesc) {
		try {
			if (orderByDesc) {
				return slaveJedisCache.zrevrangeByScore(key, endScore, startScore);
			} else {
				return slaveJedisCache.zrangeByScore(key, startScore, endScore);
			}
		} catch (Exception ex) {
			logger.error("getSoredSet error.", ex);
		} 
		return null;
	}

	/**
	 * 计算排序长度
	 * 
	 * @param key
	 * @param startScore
	 * @param endScore
	 * @return
	 */
	public static long countSoredSet(String key, long startScore, long endScore) {
		try {
			Long count = slaveJedisCache.zcount(key, startScore, endScore);
			return count == null ? 0L : count;
		} catch (Exception ex) {
			logger.error("countSoredSet error.", ex);
		} 
		return 0L;
	}

	/**
	 * 删除排序集合
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean delSortedSet(String key, String value) {
		try {
			long count = slaveJedisCache.zrem(key, value);
			return count > 0;
		} catch (Exception ex) {
			logger.error("delSortedSet error.", ex);
		} 
		return false;
	}


	/**
	 * 获得排序打分
	 * 
	 * @param key
	 * @return
	 */
	public static Double getScore(String key, String member) {
		try {
			return slaveJedisCache.zscore(key, member);
		} catch (Exception ex) {
			logger.error("getSoredSet error.", ex);
		} 
		return null;
	}

	public static boolean set(String key, String value, int second) {
		try {
			slaveJedisCache.setex(key, second, value);
			return true;
		} catch (Exception ex) {
			logger.error("set error.", ex);
		}
		return false;
	}

	public static boolean set(String key, String value) {
		try {
			slaveJedisCache.set(key, value);
			return true;
		} catch (Exception ex) {
			logger.error("set error.", ex);
		}
		return false;
	}

	public static String get(String key, String defaultValue) {
		try {
			return slaveJedisCache.get(key) == null ? defaultValue : slaveJedisCache.get(key);
		} catch (Exception ex) {
			logger.error("get error.", ex);
		} 
		return defaultValue;
	}

	public static String get(String key) {
		try {
			return slaveJedisCache.get(key);
		} catch (Exception ex) {
			logger.error("get error.", ex);
		} 
		return null;
	}

	public static boolean del(String key) {
		try {
			slaveJedisCache.del(key);
			return true;
		} catch (Exception ex) {
			logger.error("del error.", ex);
		}
		return false;
	}

	public static long incr(String key) {
		try {
			return slaveJedisCache.incr(key);
		} catch (Exception ex) {
			logger.error("incr error.", ex);
		}
		return 0;
	}

	public static long decr(String key) {
		try {
			return slaveJedisCache.decr(key);
		} catch (Exception ex) {
			logger.error("incr error.", ex);
		} 
		return 0;
	}

	public static boolean exists(String key) {
		try {
			return slaveJedisCache.exists(key);
		} catch (Exception ex) {
			logger.error("incr error.", ex);
		}
		return false;
	}
	
	
}
