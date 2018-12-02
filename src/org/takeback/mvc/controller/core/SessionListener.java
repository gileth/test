// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.mvc.controller.core;

import com.google.common.collect.Maps;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import java.util.Map;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener, HttpSessionAttributeListener
{
    public static final String Anonymous = "Anonymous";
    private static Map<String, Object> users;
    
    public static Map<String, Object> getUsers() {
        return SessionListener.users;
    }
    
    public void sessionCreated(final HttpSessionEvent se) {
        final HttpSession session = se.getSession();
        final Object uid = (session.getAttribute("$token") == null) ? "Anonymous" : session.getAttribute("$token");
        SessionListener.users.put(session.getId(), uid);
    }
    
    public void sessionDestroyed(final HttpSessionEvent se) {
        final HttpSession session = se.getSession();
        SessionListener.users.remove(session.getId());
    }
    
    public void attributeAdded(final HttpSessionBindingEvent se) {
        final HttpSession session = se.getSession();
        if (session.getAttribute("$token") != null) {
            SessionListener.users.put(session.getId(), session.getAttribute("$token"));
        }
    }
    
    public void attributeRemoved(final HttpSessionBindingEvent se) {
    }
    
    public void attributeReplaced(final HttpSessionBindingEvent se) {
        this.attributeAdded(se);
    }
    
    static {
        SessionListener.users = (Map<String, Object>)Maps.newConcurrentMap();
    }
}
