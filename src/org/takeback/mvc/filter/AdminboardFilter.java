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
            pw.println("<h2 align='center'>初始化</h2>");
            pw.println("<form action=initSystem method=post>");
            pw.println("管理员帐号:<input type=text name=username><br>");
            pw.println("管理员密码:<input type=password name=passwd><br>");
            pw.println("重复密码:<input type=password name=repasswd><br>");
            pw.println("<input type=submit value=确定><br>");
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