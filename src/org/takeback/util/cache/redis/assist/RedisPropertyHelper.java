package org.takeback.util.cache.redis.assist;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * @author xiongguohui
 *  访问配置文件
 */
public class RedisPropertyHelper {
	private static Logger logger = Logger.getLogger(RedisPropertyHelper.class);
	private static final String CONFIG_FIle = "redis.properties";
	private static Properties properties = null;
	
	static {
		try {
			String configHome = System.getenv("configHome");
        	if(StringUtils.isBlank(configHome)){
        		configHome = System.getProperty("configHome");
        		if(StringUtils.isBlank(configHome)){
        			if(StringUtils.isBlank(configHome)){
        				ClassLoader cl = RedisPropertyHelper.class.getClassLoader();
        				URL url = cl.getResource("");
        				File file = new File(url.toURI());
        	        	String classpath = file.getAbsolutePath();
        				configHome = classpath + File.separator;
        			}
        		}
        	}
        	Resource resource  = new FileSystemResource(configHome+CONFIG_FIle);
			properties = PropertiesLoaderUtils.loadProperties(resource);
			System.out.println("properties : " + properties);
		} catch (IOException e) {
		    logger.info(" load properties error...",e);
		} catch (URISyntaxException e) {
			 logger.info(" URISyntaxException error...",e);
			e.printStackTrace();
		}
	}
	public static String getValue(String key) {
		return properties.getProperty(key);
	}
}
