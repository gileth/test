// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.mvc.filter;

import java.io.IOException;
import javax.servlet.ServletException;
import java.io.PrintWriter;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.filter.OncePerRequestFilter;

public class AdminboardFilter extends OncePerRequestFilter
{
    public static boolean systemInitialized;
    
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        if (!AdminboardFilter.systemInitialized) {
            response.setContentType("text/html;charset=gbk");
            final PrintWriter pw = response.getWriter();
            pw.println("<html>");
            pw.println("<body>");
            pw.println("<h2 align='center'>\u521d\u59cb\u5316</h2>");
            pw.println("<form action=initSystem method=post>");
            pw.println("\u7ba1\u7406\u5458\u5e10\u53f7:<input type=text name=username><br>");
            pw.println("\u7ba1\u7406\u5458\u5bc6\u7801:<input type=password name=passwd><br>");
            pw.println("\u91cd\u590d\u5bc6\u7801:<input type=password name=repasswd><br>");
            pw.println("<input type=submit value=\u786e\u5b9a><br>");
            pw.println("</form>");
            pw.println("<body/>");
            pw.println("<html/>");
        }
        else {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
    }
    
    static {
        AdminboardFilter.systemInitialized = true;
    }
}
