// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.mvc;

import org.takeback.util.JSONUtils;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import java.util.HashMap;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import java.util.Map;
import org.springframework.web.servlet.ModelAndView;

public class ResponseUtils
{
    public static final String RES_CODE = "code";
    public static final String RES_MSG = "msg";
    public static final String RES_BODY = "body";
    public static final int DEFAULT_CODE = 200;
    public static final String DEFAULT_MSG = "success";
    public static final String REDIRECT = "redirect:";
    
    public static ModelAndView modelView(final String viewName, final String k, final Object v) {
        return new ModelAndView(viewName, k, v);
    }
    
    public static ModelAndView modelView(final String viewName, final Map<String, Object> data) {
        return new ModelAndView(viewName, (Map)data);
    }
    
    public static ModelAndView modelView(final String viewName) {
        return new ModelAndView(viewName);
    }
    
    public static ModelAndView redirectView(final String viewName) {
        final RedirectView rv = new RedirectView(viewName);
        return new ModelAndView((View)rv);
    }
    
    public static ModelAndView redirectView(final String viewName, final String k, final Object v) {
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put(k, v);
        return redirectView(viewName, data);
    }
    
    public static ModelAndView redirectView(final String viewName, final Map<String, Object> data) {
        final RedirectView rv = new RedirectView(viewName);
        rv.setAttributesMap((Map)data);
        return new ModelAndView((View)rv);
    }
    
    public static ModelAndView jsonView(final Map<String, Object> data) {
        final MappingJackson2JsonView jv = new MappingJackson2JsonView();
        jv.setObjectMapper(JSONUtils.getMapper());
        jv.setAttributesMap((Map)data);
        final ModelAndView mv = new ModelAndView((View)jv);
        return mv;
    }
    
    public static ModelAndView jsonView(final int code, final String msg) {
        return jsonView(createBody(code, msg));
    }
    
    public static ModelAndView jsonView(final Object body) {
        return jsonView(createBody(200, "success", body));
    }
    
    public static ModelAndView jsonView(final int code, final String msg, final Object body) {
        return jsonView(createBody(code, msg, body));
    }
    
    public static Map<String, Object> createBody(final int code, final String msg, final Object body) {
        final Map<String, Object> res = new HashMap<String, Object>();
        res.put("code", code);
        res.put("msg", msg);
        if (body != null) {
            res.put("body", body);
        }
        return res;
    }
    
    public static Map<String, Object> createBody(final Object body) {
        return createBody(200, "success", body);
    }
    
    public static Map<String, Object> createBody(final int code, final String msg) {
        return createBody(code, msg, null);
    }
}
