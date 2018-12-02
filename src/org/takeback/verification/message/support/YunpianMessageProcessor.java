// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.verification.message.support;

import org.slf4j.LoggerFactory;
import org.takeback.util.converter.ConversionUtils;
import java.util.Map;
import org.takeback.util.httpclient.HttpClientUtils;
import java.util.LinkedHashMap;
import org.slf4j.Logger;

public class YunpianMessageProcessor extends DefaultMessageProcessor
{
    private static final Logger log;
    private String apiKey;
    private String URI_GET_USER_INFO;
    private String URI_SEND_SMS;
    private String URI_TPL_SEND_SMS;
    private String URI_SEND_VOICE;
    private String ENCODING;
    
    public YunpianMessageProcessor() {
        this.URI_GET_USER_INFO = "http://yunpian.com/v1/user/get.json";
        this.URI_SEND_SMS = "http://yunpian.com/v1/sms/send.json";
        this.URI_TPL_SEND_SMS = "http://yunpian.com/v1/sms/tpl_send.json";
        this.URI_SEND_VOICE = "http://yunpian.com/v1/voice/send.json";
        this.ENCODING = "UTF-8";
    }
    
    @Override
    public String sendSMS(final String phoneNumber, final String content) {
        final LinkedHashMap<String, String> ps = new LinkedHashMap<String, String>();
        ps.put("apikey", this.apiKey);
        ps.put("text", content);
        ps.put("mobile", phoneNumber);
        final String result = HttpClientUtils.post(this.URI_SEND_SMS, ps);
        final Map<String, Object> map = ConversionUtils.convert(result,Map.class);
        if (0 != (int)map.get("code")) {
            YunpianMessageProcessor.log.error("send content {} to {} failed, responseCode is {}", new Object[] { content, phoneNumber, result });
        }
        return content;
    }
    
    public String getApiKey() {
        return this.apiKey;
    }
    
    public void setApiKey(final String apiKey) {
        this.apiKey = apiKey;
    }
    
    public String getURI_GET_USER_INFO() {
        return this.URI_GET_USER_INFO;
    }
    
    public void setURI_GET_USER_INFO(final String URI_GET_USER_INFO) {
        this.URI_GET_USER_INFO = URI_GET_USER_INFO;
    }
    
    public String getURI_SEND_SMS() {
        return this.URI_SEND_SMS;
    }
    
    public void setURI_SEND_SMS(final String URI_SEND_SMS) {
        this.URI_SEND_SMS = URI_SEND_SMS;
    }
    
    public String getURI_TPL_SEND_SMS() {
        return this.URI_TPL_SEND_SMS;
    }
    
    public void setURI_TPL_SEND_SMS(final String URI_TPL_SEND_SMS) {
        this.URI_TPL_SEND_SMS = URI_TPL_SEND_SMS;
    }
    
    public String getURI_SEND_VOICE() {
        return this.URI_SEND_VOICE;
    }
    
    public void setURI_SEND_VOICE(final String URI_SEND_VOICE) {
        this.URI_SEND_VOICE = URI_SEND_VOICE;
    }
    
    public String getENCODING() {
        return this.ENCODING;
    }
    
    public void setENCODING(final String ENCODING) {
        this.ENCODING = ENCODING;
    }
    
    static {
        log = LoggerFactory.getLogger((Class)YunpianMessageProcessor.class);
    }
}
