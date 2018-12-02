// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.mvc.controller;

import org.slf4j.LoggerFactory;
import org.takeback.util.converter.ConversionUtils;
import org.takeback.util.ReflectUtil;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import java.lang.reflect.Method;
import org.takeback.util.exception.CodedBaseRuntimeException;
import java.util.List;
import org.takeback.util.context.ContextUtils;
import org.takeback.core.user.AccountCenter;
import org.springframework.web.util.WebUtils;
import org.takeback.util.ApplicationContextHolder;
import org.takeback.mvc.ResponseUtils;
import org.apache.commons.lang3.StringUtils;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RestController;

@RestController("jsonRequester")
public class JsonRequester
{
    private static final Logger log;
    private static final String SERVICE = "service";
    private static final String METHOD = "method";
    private static final String PARAMETERS = "parameters";
    
    @RequestMapping(value = { "/**/*.jsonRequest" }, method = { RequestMethod.POST }, headers = { "content-type=application/json" })
    public Map<String, Object> handle(@RequestBody final Map<String, Object> request, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) {
        final String service = request.get("service");
        final String method = request.get("method");
        if (StringUtils.isEmpty((CharSequence)service) || StringUtils.isEmpty((CharSequence)method)) {
            return ResponseUtils.createBody(400, "missing service or method.");
        }
        if (!ApplicationContextHolder.containBean(service)) {
            return ResponseUtils.createBody(401, "service is not defined in spring.");
        }
        final String uid = (String)WebUtils.getSessionAttribute(httpServletRequest, "$uid");
        final Long urt = (Long)WebUtils.getSessionAttribute(httpServletRequest, "$urt");
        if (uid == null || urt == null) {
            return ResponseUtils.createBody(403, "notLogon");
        }
        ContextUtils.put("$urt", AccountCenter.getUser(uid).getUserRoleToken(urt));
        ContextUtils.put("$httpRequest", httpServletRequest);
        ContextUtils.put("$httpResponse", httpServletResponse);
        final Object s = ApplicationContextHolder.getBean(service);
        final Object b = request.get("parameters");
        Object[] parameters = null;
        if (b != null) {
            if (b instanceof List) {
                final List<?> ps = (List<?>)b;
                final int len = ps.size();
                parameters = new Object[len];
                for (int i = 0; i < len; ++i) {
                    parameters[i] = ps.get(i);
                }
            }
            else {
                parameters = new Object[] { b };
            }
        }
        Method m = null;
        Object r = null;
        try {
            if (parameters == null) {
                m = s.getClass().getDeclaredMethod(method, (Class<?>[])new Class[0]);
                r = m.invoke(s, new Object[0]);
            }
            else {
                final Method[] declaredMethods;
                final Method[] ms = declaredMethods = s.getClass().getDeclaredMethods();
                for (final Method mm : declaredMethods) {
                    if (method.equals(mm.getName())) {
                        if (this.isCompatible(mm, parameters)) {
                            m = mm;
                            break;
                        }
                    }
                }
                r = m.invoke(s, this.convertToParameters(m.getParameterTypes(), parameters));
            }
            return ResponseUtils.createBody(r);
        }
        catch (CodedBaseRuntimeException e) {
            JsonRequester.log.error("execute service[" + service + "] with " + method + " failed.", (Throwable)e);
            return ResponseUtils.createBody(e.getCode(), e.getMessage());
        }
        catch (Exception e2) {
            JsonRequester.log.error("execute service[" + service + "] with " + method + " failed.", (Throwable)e2);
            if (e2.getCause() != null) {
                return ResponseUtils.createBody(402, e2.getCause().getMessage());
            }
            return ResponseUtils.createBody(402, "execute service[" + service + "] with " + method + " failed.");
        }
        finally {
            ContextUtils.clear();
        }
    }
    
    private boolean isCompatible(final Method m, final Object[] parameters) {
        boolean r = true;
        final Class<?>[] cls = m.getParameterTypes();
        if (parameters == null) {
            if (cls.length != 0) {
                r = false;
            }
        }
        else if (parameters.length != cls.length) {
            r = false;
        }
        else {
            for (int i = 0; i < cls.length; ++i) {
                if (!ReflectUtil.isCompatible(cls[i], parameters[i])) {
                    r = false;
                    break;
                }
            }
        }
        return r;
    }
    
    private Object[] convertToParameters(final Class<?>[] parameterTypes, final Object[] args) {
        final Object[] parameters = new Object[parameterTypes.length];
        int i = 0;
        for (final Class<?> type : parameterTypes) {
            parameters[i] = ConversionUtils.convert(args[i], type);
            ++i;
        }
        return parameters;
    }
    
    public String a() {
        return "a";
    }
    
    public int a(final int a, final String b) {
        return a;
    }
    
    public static void main(final String[] args) throws NoSuchMethodException, SecurityException {
        final Method m = JsonRequester.class.getDeclaredMethod("a", (Class<?>[])new Class[0]);
        System.out.println(m.getParameterTypes().length);
    }
    
    static {
        log = LoggerFactory.getLogger((Class)JsonRequester.class);
    }
}
