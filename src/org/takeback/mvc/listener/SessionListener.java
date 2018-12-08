// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.mvc.listener;

import com.google.common.collect.Maps;
import org.slf4j.LoggerFactory;
import org.takeback.util.SerializeUtil;
import org.takeback.util.cache.redis.JRedisUtil;
import org.takeback.util.cache.redis.MasterSingleServerJedisCache;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionBindingEvent;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener, HttpSessionAttributeListener
{
    private static final Logger log;
    
    private  static  MasterSingleServerJedisCache sessions = JRedisUtil.getMasterSingleServerJedisCache();
    
    private static  int  OnlineNumber = 0;
    
   // private static Map<Integer, List<String>> users;
    
/*    public static Map<Integer, List<String>> getUsers() {
     
        return Collections.unmodifiableMap((Map<? extends Integer, ? extends List<String>>)SessionListener.users);
    }
    */
    public static List<String> getUser(final int uid) {
        //return getUsers().get(uid);
    	return sessions.get(String.valueOf(uid), List.class);
    }
    
    public static int getOnlineNumber() {
     /*   int c = 0;
        for (final List<String> sids : SessionListener.users.values()) {
            c += sids.size();
        }*/
        return OnlineNumber;
    }
    
    public static boolean isOnline(final Integer uid) {
        //return SessionListener.users.get(uid) != null;
    	return sessions.get(String.valueOf(uid), List.class) !=null;
    }
    
    private void login(final int uid, final String sid) {
        //List<String> sids = SessionListener.users.get(uid);
    	List<String> sids = sessions.get(String.valueOf(uid),List.class);
        if (sids == null) {
            sids = new CopyOnWriteArrayList<String>();
           // SessionListener.users.put(uid, sids);
            sessions.set(String.valueOf(uid),sids);
            OnlineNumber++;
        }else {
        	
        	OnlineNumber ++;
        }
        if (!sids.contains(sid)) {
            sids.add(sid);
        }
        SessionListener.log.info("user {} login with session id {}, he login from {} place, online users is {} now", new Object[] { uid, sid, sids.size(), getOnlineNumber() });
    }
    
    private void logout(final int uid, final String sid) {
        final List<String> sids = sessions.get(String.valueOf(uid), List.class) ;//SessionListener.users.get(uid);
        if (sids != null) {
            sids.remove(sid);
            if (sids.size() == 0) {
               // SessionListener.users.remove(uid);
            	sessions.del(String.valueOf(uid));
            }
            OnlineNumber --;
        }
        SessionListener.log.info("user {} left, online users is {} now",uid, getOnlineNumber());
    }
    
    public void attributeAdded(final HttpSessionBindingEvent se) {
        if ("$uid".equals(se.getName())) {
            final int uid = (int)se.getValue();
            final String sid = se.getSession().getId();
            this.login(uid, sid);
        }
    }
    
    public void attributeReplaced(final HttpSessionBindingEvent se) {
        this.attributeAdded(se);
    }
    
    public void sessionDestroyed(final HttpSessionEvent se) {
    }
    
    public void sessionCreated(final HttpSessionEvent se) {
    }
    
    public void attributeRemoved(final HttpSessionBindingEvent se) {
        if ("$uid".equals(se.getName())) {
            final int uid = (int)se.getValue();
            final String sid = se.getSession().getId();
            this.logout(uid, sid);
        }
    }
     
    static {
        log = LoggerFactory.getLogger(SessionListener.class);
        
        //SessionListener.users = new ConcurrentHashMap<Integer, List<String>>();
    }
}
