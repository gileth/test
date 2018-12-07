package org.takeback.util.cache.redis.assist;

 
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;
/**
 * 
 * @author xiongguohui
 * 某一个redis slave的访问
 * 
 */
public class Config_SingleJedisCache {
	private static Logger logger = Logger.getLogger(Config_SingleJedisCache.class);
	/* redis服务端节点；多个节点之间用空格分隔； 例如：host1:6379 host2:6379 */
	private String server;
	/* 操作超时时间 */
	private int timeout = 3000;
	// ===========以下为apache common pool的配置项=========
	// 最大分配的对象数
	private int maxActive = 400;
	// 最大能够保持idel状态的对象数
	private int maxIdle = 5;
	// 当池内没有返回对象时，最大等待时间
	private int maxWait = 5000;
	// 表示idle object evitor两次扫描之间要sleep的毫秒数
	private int timeBetweenEvictionRunsMillis = 15000;
	// 表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐
	private int minEvictableIdleTimeMillis = 20000;
	// 每次检查几个对象。如果这个值不是正数，则每次检查的对象数是检查时池内对象的总数乘以这个值的负倒数再向上取整的结果
	private int numTestsPerEvictionRun = -1;
	// 在borrow一个jedis实例时，是否提前进行alidate操作
	private boolean testOnBorrow = true;
	// 在return给pool时，是否提前进行validate操作
	private boolean testOnReturn = false;
	// 如果为true，表示有一个idle object evitor线程对idle
	// object进行扫描，如果validate失败，此object会被从pool中drop掉
	private boolean testWhileIdle = true;
	// 表示当pool中的jedis实例都被allocated完时，pool要采取的操作；默认有三种:
	// WHEN_EXHAUSTED_FAIL（表示无jedis实例时，直接抛出NoSuchElementException）、
	// WHEN_EXHAUSTED_BLOCK（则表示阻塞住，或者达到maxWait时抛出JedisConnectionException）、
	// WHEN_EXHAUSTED_GROW（则表示新建一个jedis实例，也就说设置的maxActive无用）；
	private byte whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;

	/**
	 * 构造函数，从配置文件中加载配置项
	 */
	public Config_SingleJedisCache() {
		this.server = RedisPropertyHelper.getValue("singleserver");
		logger.info("#### Config_SingleJedisCache server####" + server);
		if (StringUtils.isNumeric(RedisPropertyHelper.getValue("timeout"))) {
			this.timeout = Integer.parseInt(RedisPropertyHelper.getValue("timeout"));
	 	}
		if (StringUtils.isNumeric(RedisPropertyHelper.getValue("maxActive"))) {
			this.maxActive = Integer.parseInt(RedisPropertyHelper.getValue("maxActive"));
		}
		if (StringUtils.isNumeric(RedisPropertyHelper.getValue("maxIdle"))) {
			this.maxIdle = Integer.parseInt(RedisPropertyHelper.getValue("maxIdle"));
		}
		if (StringUtils.isNumeric(RedisPropertyHelper.getValue("maxWait"))) {
			this.maxWait = Integer.parseInt(RedisPropertyHelper.getValue("maxWait"));
		}
	}

	/**
	 * @param server
	 *            单服务端的redis服务端节点；例如：host1:6379
	 */
	public Config_SingleJedisCache(String server) {
		this.server = server;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(int maxWait) {
		this.maxWait = maxWait;
	}

	public int getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}

	public void setTimeBetweenEvictionRunsMillis(
			int timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	public int getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}

	public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	public int getNumTestsPerEvictionRun() {
		return numTestsPerEvictionRun;
	}

	public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
		this.numTestsPerEvictionRun = numTestsPerEvictionRun;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public boolean isTestOnReturn() {
		return testOnReturn;
	}

	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public boolean isTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public byte getWhenExhaustedAction() {
		return whenExhaustedAction;
	}

	public void setWhenExhaustedAction(byte whenExhaustedAction) {
		this.whenExhaustedAction = whenExhaustedAction;
	}

}
