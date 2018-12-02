// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.mvc.interceptor;

import java.util.List;
import org.takeback.mvc.listener.SessionListener;
import org.springframework.web.util.WebUtils;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class LimitOneClientOnlineInterceptor extends HandlerInterceptorAdapter
{
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        final Object uid = WebUtils.getSessionAttribute(request, "$uid");
        if (uid != null && uid instanceof Integer) {
            final int userId = (int)uid;
            final List<String> sessionIds = SessionListener.getUser(userId);
            if (sessionIds != null && sessionIds.size() > 1) {
                final String sid = WebUtils.getSessionId(request);
                if (sid.equals(sessionIds.get(0))) {
                    request.getSession().invalidate();
                }
            }
        }
        return true;
    }
}
