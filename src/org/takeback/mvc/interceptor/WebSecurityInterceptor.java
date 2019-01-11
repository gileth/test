package org.takeback.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.takeback.util.sc.BrowserUtils;

public class WebSecurityInterceptor implements HandlerInterceptor {

	public static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityInterceptor.class);

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unused")
	@Override
	public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2) throws Exception {
		// TODO Auto-generated method stub
		LOGGER.info("请求路径========>" + arg0.getRequestURL().toString());
		StringBuffer url = arg0.getRequestURL();
		String host = url.delete(url.length() - arg0.getRequestURI().length(), url.length()).append("/").toString();
		String uri = arg0.getRequestURI();
		// 判断是否是微信浏览器
		if (BrowserUtils.isWechat(arg0) && !uri.startsWith("/Jump.html") && !uri.endsWith(".png")) {
			LOGGER.info("微信浏览器跳转========>" + "/Jump.html?bc=" + host + arg0.getRequestURI());
			arg1.sendRedirect("/Jump.html?bc=" + host + arg0.getRequestURI());
			return false;
		}
		return true;
	}

}
