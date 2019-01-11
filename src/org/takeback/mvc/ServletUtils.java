// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.mvc;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.WebUtils;
import javax.servlet.http.HttpServletRequest;

public class ServletUtils
{
    public static final String TOKEN = "$token";
    
    public static String getClientIP(final HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getRemoteAddr();
        }
        if (StringUtils.isNotBlank(ip) && ip.contains(",")) {
        	ip = ip.substring(0, ip.indexOf(","));
        }
        return ip;
    }
    
    public static boolean isLogonExpired(final HttpServletRequest request) {
        return WebUtils.getSessionAttribute(request, "$uid") == null || WebUtils.getSessionAttribute(request, "$urt") == null;
    }
}
