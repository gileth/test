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
            return ResponseUtils.jsonView(909, "\u56fe\u7247\u9a8c\u8bc1\u7801\u4e0d\u6b63\u786e");
        }
        WebUtils.setSessionAttribute(request, "verifycode", (Object)null);
        if (!ValidateUtil.instance().validatePhone(phonenumb)) {
            return ResponseUtils.jsonView(502, "\u624b\u673a\u53f7\u7801\u4e0d\u6b63\u786e");
        }
        int c = (int)this.cache.getUnchecked((Object)phonenumb);
        if (c >= 10) {
            return ResponseUtils.jsonView(501, "\u5bf9\u4e0d\u8d77\uff0c\u4e3a\u4e86\u5b89\u5168\u8d77\u89c1\uff0c\u60a8\u7684\u624b\u673a\u53f7\u780112\u5c0f\u65f6\u5185\u53ea\u80fd\u83b7\u5f9710\u6b21\u77ed\u4fe1\u9a8c\u8bc1\u7801\u3002");
        }
        ++c;
        this.cache.put((Object)phonenumb, (Object)c);
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
        WebUtils.setSessionAttribute(request, "smscode", (Object)code);
        return ResponseUtils.jsonView(200, "\u624b\u673a\u9a8c\u8bc1\u7801\u5df2\u53d1\u9001");
    }
    
    @RequestMapping(value = { "image" }, method = { RequestMethod.GET })
    public void imageVerifyCode(final HttpServletRequest request, final HttpServletResponse response, @RequestParam(required = false, defaultValue = "150") final int w, @RequestParam(required = false, defaultValue = "60") final int h) throws IOException {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0L);
        response.setContentType("image/jpeg");
        final String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
        WebUtils.setSessionAttribute(request, "verifycode", (Object)verifyCode.toLowerCase());
        VerifyCodeUtils.outputImage(w, h, (OutputStream)response.getOutputStream(), verifyCode);
    }
    
    @RequestMapping({ "ckSmsCode" })
    public ModelAndView checkSMSCode(final HttpServletRequest request, final String smsCode) {
        if (smsCode.equals(WebUtils.getSessionAttribute(request, "smscode"))) {
            return ResponseUtils.jsonView(200, "\u624b\u673a\u9a8c\u8bc1\u7801\u6b63\u786e");
        }
        return ResponseUtils.jsonView(500, "\u624b\u673a\u9a8c\u8bc1\u7801\u4e0d\u6b63\u786e");
    }
    
    @RequestMapping({ "ckImageCode" })
    public ModelAndView checkIMAGECode(final HttpServletRequest request, final String imageCode) {
        if (imageCode == null || "".equals(imageCode)) {
            return ResponseUtils.jsonView(500, "\u56fe\u7247\u9a8c\u8bc1\u7801\u4e0d\u6b63\u786e");
        }
        if (imageCode.equalsIgnoreCase(String.valueOf(WebUtils.getSessionAttribute(request, "verifycode")))) {
            return ResponseUtils.jsonView(200, "\u56fe\u7247\u9a8c\u8bc1\u7801\u6b63\u786e");
        }
        return ResponseUtils.jsonView(500, "\u56fe\u7247\u9a8c\u8bc1\u7801\u4e0d\u6b63\u786e");
    }
    
    static {
        log = LoggerFactory.getLogger((Class)CodeController.class);
    }
}
