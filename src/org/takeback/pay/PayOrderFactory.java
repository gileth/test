// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.pay;

import org.slf4j.LoggerFactory;
import cn.beecloud.BeeCloud;
import net.sf.json.JSONObject;
import java.io.IOException;
import org.takeback.thirdparty.support.HttpHelper;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import cn.beecloud.bean.BCException;
import cn.beecloud.BCPay;
import org.apache.commons.lang3.StringUtils;
import cn.beecloud.BCEumeration;
import cn.beecloud.bean.BCOrder;
import org.takeback.thirdparty.support.WxConfig;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;

public class PayOrderFactory implements InitializingBean
{
    public static final Logger LOGGER;
    private static PayOrderFactory instance;
    private byte vcode;
    private String appId;
    private String testSecret;
    private String appSecret;
    private String masterSecret;
    private WxConfig wxConfig;
    
    private PayOrderFactory() throws IllegalAccessException {
        this.vcode = 111;
        if (this.vcode != 111) {
            throw new IllegalAccessException("Illegal access to this constructor.");
        }
        PayOrderFactory.instance = this;
        ++this.vcode;
    }
    
    public static PayOrderFactory getInstance() {
        return PayOrderFactory.instance;
    }
    
    public BCOrder getPayOrder(final String payChannel, final Integer totalFee, final String title, final String identityId) throws PaymentException {
        BCEumeration.PAY_CHANNEL channel;
        try {
            channel = BCEumeration.PAY_CHANNEL.valueOf(payChannel);
        }
        catch (Exception e) {
            throw new PaymentException("Unsupported pay type: " + payChannel, e);
        }
        final String billNo = PayOrderNoGenerator.generator(true);
        BCOrder bcOrder = new BCOrder(channel, totalFee, billNo, title);
        bcOrder.setBillTimeout(360);
        final String returnUrl = this.wxConfig.getGameServerBaseUrl() + "payReturn.jsp";
        bcOrder.setReturnUrl(returnUrl);
        if (StringUtils.isNotEmpty((CharSequence)identityId)) {
            bcOrder.setIdentityId(identityId);
        }
        try {
            bcOrder = BCPay.startBCPay(bcOrder);
            return bcOrder;
        }
        catch (BCException e2) {
            throw new PaymentException("Failed to get pay order.", (Throwable)e2);
        }
    }
    
    public BCOrder getWxJSPayOrder(final Integer totalFee, final String title, final String code) throws PaymentException {
        final BCEumeration.PAY_CHANNEL channel = BCEumeration.PAY_CHANNEL.WX_JSAPI;
        final String billNo = PayOrderNoGenerator.generator(true);
        BCOrder bcOrder = new BCOrder(channel, totalFee, billNo, title);
        bcOrder.setBillTimeout(360);
        final String returnUrl = this.wxConfig.getGameServerBaseUrl() + "payReturn.jsp";
        bcOrder.setReturnUrl(returnUrl);
        bcOrder.setOpenId(this.getWxOpenId(code));
        try {
            bcOrder = BCPay.startBCPay(bcOrder);
            return bcOrder;
        }
        catch (BCException e) {
            throw new PaymentException("Failed to get pay order.", (Throwable)e);
        }
    }
    
    public String getWxAuthorizeUrl(final String title, final double totalFee, final int userId) {
        try {
            final String encodedWSJSAPIRedirectUrl = URLEncoder.encode(this.wxConfig.getGameServerBaseUrl() + "pay/apply/wx?title=" + title + "&totalFee=" + totalFee + "&userId=" + userId, "UTF-8");
            return "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + this.wxConfig.getWxJSAPIAppId() + "&redirect_uri=" + encodedWSJSAPIRedirectUrl + "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String getWxOpenId(final String code) throws PaymentException {
        final String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + this.wxConfig.getWxJSAPIAppId() + "&secret=" + this.wxConfig.getWxJSAPISecret() + "&code=" + code + "&grant_type=authorization_code";
        JSONObject resultObject;
        try {
            resultObject = HttpHelper.getJson(url);
        }
        catch (IOException e) {
            throw new PaymentException("Failed to get open id.", e);
        }
        if (resultObject.containsKey((Object)"errcode")) {
            throw new PaymentException("Failed to get open id, caused by: " + resultObject.get("errmsg"));
        }
        return resultObject.get("openid").toString();
    }
    
    public void afterPropertiesSet() throws Exception {
        BeeCloud.registerApp(this.appId, this.testSecret, this.appSecret, this.masterSecret);
    }
    
    public String getAppId() {
        return this.appId;
    }
    
    public void setAppId(final String appId) {
        this.appId = appId;
    }
    
    public String getTestSecret() {
        return this.testSecret;
    }
    
    public void setTestSecret(final String testSecret) {
        this.testSecret = testSecret;
    }
    
    public String getAppSecret() {
        return this.appSecret;
    }
    
    public void setAppSecret(final String appSecret) {
        this.appSecret = appSecret;
    }
    
    public String getMasterSecret() {
        return this.masterSecret;
    }
    
    public void setMasterSecret(final String masterSecret) {
        this.masterSecret = masterSecret;
    }
    
    public WxConfig getWxConfig() {
        return this.wxConfig;
    }
    
    public void setWxConfig(final WxConfig wxConfig) {
        this.wxConfig = wxConfig;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PayOrderFactory.class);
    }
}
