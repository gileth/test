package org.takeback.util.sc;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class BrowserUtils {
	
	public static boolean isWechat(HttpServletRequest request) {
		String userAgent = request.getHeader("User-Agent");
		if(StringUtils.isBlank(userAgent)) {
			return false;
		}
		String ua = request.getHeader("User-Agent").toLowerCase();
		if (ua.indexOf("micromessenger") > -1) {
			return true;// 微信
		}
		return false;// 非微信手机浏览器
	}
	
}
