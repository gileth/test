// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.mvc.controller;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.OutputStream;
import org.takeback.verification.image.VerifyCodeUtils;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;
import org.joda.time.DateTime;
import org.apache.commons.lang3.StringUtils;
import org.takeback.util.valid.ValidateUtil;
import org.takeback.mvc.ResponseUtils;
import org.springframework.web.util.WebUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import com.google.common.cache.CacheLoader;
import java.util.concurrent.TimeUnit;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.verification.message.MessageProcessor;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping({ "code" })
public class CodeController
{
    private static final Logger log;
    public static final String SMS_CODE = "smscode";
    public static final String VERIFY_CODE = "verifycode";
    private LoadingCache<String, Integer> cache;
    private static final int maxCodeRequest = 10;
    @Autowired
    private MessageProcessor messageProcessor;
    
    public CodeController() {
        this.cache = (LoadingCache<String, Integer>)CacheBuilder.newBuilder().maximumSize(500L).expireAfterWrite(12L, TimeUnit.HOURS).build((CacheLoader)new CacheLoader<String, Integer>() {
            public Integer load(final String k) throws Exception {
                return 0;
            }
        });
    }
    
    @RequestMapping(value = { "phone" }, method = { RequestMethod.POST })
    public ModelAndView shortMessage(final HttpServletRequest request, final String phonenumb, final String verifycode, @RequestParam(required = false) final String type) {
        if (!verifycode.equalsIgnoreCase((String)WebUtils.getSessionAttribute(request, "verifycode"))) {
            return ResponseUtils.jsonView(909, "图片验证码不正确");
        }
        WebUtils.setSessionAttribute(request, "verifycode", null);
        if (!ValidateUtil.instance().validatePhone(phonenumb)) {
            return ResponseUtils.jsonView(502, "手机号码不正确");
        }
        int c = (int)this.cache.getUnchecked(phonenumb);
        if (c >= 10) {
            return ResponseUtils.jsonView(501, "对不起，为了安全起见，您的手机号码12小时内只能获得10次短信验证码。");
        }
        ++c;
        this.cache.put(phonenumb,c);
        String code = null;
        if (StringUtils.isEmpty((CharSequence)type)) {
            code = this.messageProcessor.sendCode(phonenumb, "1");
        }
        else {
            code = this.messageProcessor.sendCode(phonenumb, type);
        }
        String host = request.getHeader("host");
        if (!StringUtils.isEmpty((CharSequence)host)) {
            host = host.toLowerCase();
            if (host.startsWith("127.0.0.1") || host.startsWith("localhost") || host.startsWith("wengshankj.oicp.net") || host.startsWith("192.168.3.200")) {
                code = "8888";
            }
        }
        CodeController.log.info("send code [{}] to {} at {}", new Object[] { code, phonenumb, new DateTime().toString("yyyy-MM-dd HH:mm:ss") });
        WebUtils.setSessionAttribute(request, "smscode", code);
        return ResponseUtils.jsonView(200, "手机验证码已发送");
    }
    
    @RequestMapping(value = { "image" }, method = { RequestMethod.GET })
    public void imageVerifyCode(final HttpServletRequest request, final HttpServletResponse response, @RequestParam(required = false, defaultValue = "150") final int w, @RequestParam(required = false, defaultValue = "60") final int h) throws IOException {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0L);
        response.setContentType("image/jpeg");
        final String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
        WebUtils.setSessionAttribute(request, "verifycode", verifyCode.toLowerCase());
        VerifyCodeUtils.outputImage(w, h, (OutputStream)response.getOutputStream(), verifyCode);
    }
    
    @RequestMapping({ "ckSmsCode" })
    public ModelAndView checkSMSCode(final HttpServletRequest request, final String smsCode) {
        if (smsCode.equals(WebUtils.getSessionAttribute(request, "smscode"))) {
            return ResponseUtils.jsonView(200, "手机验证码正确");
        }
        return ResponseUtils.jsonView(500, "手机验证码不正确");
    }
    
    @RequestMapping({ "ckImageCode" })
    public ModelAndView checkIMAGECode(final HttpServletRequest request, final String imageCode) {
        if (imageCode == null || "".equals(imageCode)) {
            return ResponseUtils.jsonView(500, "图片验证码不正确");
        }
        if (imageCode.equalsIgnoreCase(String.valueOf(WebUtils.getSessionAttribute(request, "verifycode")))) {
            return ResponseUtils.jsonView(200, "图片验证码正确");
        }
        return ResponseUtils.jsonView(500, "图片验证码不正确");
    }
    
    static {
        log = LoggerFactory.getLogger((Class)CodeController.class);
    }
}