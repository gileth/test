package org.takeback.util.cache.redis;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.takeback.chat.entity.PubUser;
import org.takeback.chat.store.user.User;
import org.takeback.chat.utils.Const;

import com.alibaba.fastjson.JSON;
import com.vcr.datasource.model.entity.Member;

/**
 * 缓存工具
 */
@Component
public class CacheUtils {
    // 日志
    private static final Logger log =  LoggerFactory.getLogger(CacheUtils.class);

    public <T> boolean setRedisCache(String key, int expires, T value) {
        if (StringUtils.isEmpty(key)) return false;
        try {
            String json = JSON.toJSONString(value);
            if (expires <= 0) {
            	return JRedisUtil.set(key, json);
            } else {
            	return JRedisUtil.set(key, json, expires);
            }
        } catch (Exception e) {
            log.error(" #setRedisCache -- redis# ", key, e);
            return false;
        }
    }

    public <T> T getRedisCache(String key, Class clz) {
    	try{
            String json = JRedisUtil.get(key);
            if (StringUtils.isNotBlank(json)) {
                return (T) JSON.parseObject(json, clz);
            } else {
                log.error(" #getRedisCache -- redis 未获取到数据 key=" + key);
                return null;
            }
        } catch (Exception ex) {
            log.error(" #getRedisCache -- redis 获取数据异常#" , key, ex);
            return null;
        }
    }

    public Member getUser(int id) {
    	try {
    		return getRedisCache(Const.USER_CACHE_KEY_PREFIX + id, Member.class);
    	}catch (Exception e) {
    		log.error(e.getMessage(), e);
		}
    	return null;
    }
    
    public boolean setUser(int id, PubUser user) {
    	try {
    		return setRedisCache(Const.USER_CACHE_KEY_PREFIX + id, 0, buildMember(user));
    	}catch (Exception e) {
    		log.error(e.getMessage(), e);
		}
    	return false;
    }

    public boolean updateUser(int id, PubUser user) {
    	try {
    		Member member = getUser(id);
        	return setRedisCache(Const.USER_CACHE_KEY_PREFIX + id, 0, buildMember(user, member));
    	}catch (Exception e) {
			log.error(e.getMessage(), e);
		}
    	return false;
    }
    
	private Member buildMember(PubUser user) {
		Member member = new Member();
		member.setUid(user.getId().longValue());
		member.setNickname(user.getNickName());
		member.setAccount(user.getUserId());
		member.setAvatar(user.getHeadImg());
		member.setBalance(user.getMoney());
		member.setMobile(StringUtils.isBlank(user.getMobile()) ? "" : user.getMobile());
		return member;
	}
	
	private Member buildMember(PubUser user, Member member) {
		if (member == null) {
			member = new Member();
		}
		log.info("更新用户缓存,更新前:" + member);
		member.setAvatar(user.getHeadImg());
		member.setBalance(user.getMoney());
		member.setMobile(StringUtils.isBlank(user.getMobile()) ? "" : user.getMobile());
		log.info("更新用户缓存,更新后:" + member);
		return member;
	}
    
	
	
}
