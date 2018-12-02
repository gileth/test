// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.mvc.resolver;

import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class ExceptionResolver implements HandlerExceptionResolver
{
    public ModelAndView resolveException(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final Object o, final Exception e) {
        System.out.println("e----------------------------");
        e.printStackTrace();
        return null;
    }
}
