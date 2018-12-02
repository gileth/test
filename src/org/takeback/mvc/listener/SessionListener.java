// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.mvc.listener;

import com.google.common.collect.Maps;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionBindingEvent;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener, HttpSessionAttributeListener
{
    private static final Logger log;
    private static Map<Integer, List<String>> users;
    
    public static Map<Integer, List<String>> getUsers() {
        return Collections.unmodifiableMap((Map<? extends Integer, ? extends List<String>>)SessionListener.users);
    }
    
    public static List<String> getUser(final int uid) {
        return getUsers().get(uid);
    }
    
    public static int getOnlineNumber() {
        int c = 0;
        for (final List<String> sids : SessionListener.users.values()) {
            c += sids.size();
        }
        return c;
    }
    
    public static boolean isOnline(final Integer uid) {
        return SessionListener.users.get(uid) != null;
    }
    
    private void login(final int uid, final String sid) {
        List<String> sids = SessionListener.users.get(uid);
        if (sids == null) {
            sids = (List<String>)Lists.newCopyOnWriteArrayList();
            SessionListener.users.put(uid, sids);
        }
        if (!sids.contains(sid)) {
            sids.add(sid);
        }
        SessionListener.log.info("user {} login with session id {}, he login from {} place, online users is {} now", new Object[] { uid, sid, sids.size(), getOnlineNumber() });
    }
    
    private void logout(final int uid, final String sid) {
        final List<String> sids = SessionListener.users.get(uid);
        if (sids != null) {
            sids.remove(sid);
            if (sids.size() == 0) {
                SessionListener.users.remove(uid);
            }
        }
        SessionListener.log.info("user {} left, online users is {} now", (Object)uid, (Object)getOnlineNumber());
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
        log = LoggerFactory.getLogger((Class)SessionListener.class);
        SessionListener.users = (Map<Integer, List<String>>)Maps.newConcurrentMap();
    }
}
