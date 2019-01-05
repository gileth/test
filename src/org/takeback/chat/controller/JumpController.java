package org.takeback.chat.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.takeback.util.sc.BrowserUtils;

@Controller
public class JumpController {
	public static final Logger LOGGER = LoggerFactory.getLogger(JumpController.class);

	@RequestMapping(value = { "/Jump.html" }, method = { RequestMethod.GET })
	public String jump(HttpServletRequest request,HttpServletResponse response) {
		try {
			String url = request.getParameter("bc");
			if(BrowserUtils.isWechat(request)) {
				LOGGER.info("bc="+url);
				return "wxjump";
			}
			response.sendRedirect(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
