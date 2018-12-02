// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.verification.message.support;

import org.slf4j.LoggerFactory;
import java.util.Map;
import org.takeback.util.httpclient.HttpClientUtils;
import org.takeback.util.MD5StringUtil;
import java.util.LinkedHashMap;
import org.slf4j.Logger;

public class MomingMessageProcessor extends DefaultMessageProcessor
{
    private static final Logger log;
    private String user;
    private String password;
    private String api;
    private String send_action;
    private String balance_action;
    
    public MomingMessageProcessor() {
        this.api = "http://api.momingsms.com/";
        this.send_action = "send";
        this.balance_action = "getBalance";
    }
    
    @Override
    public String sendSMS(final String phoneNumber, final String content) {
        final LinkedHashMap<String, String> ps = new LinkedHashMap<String, String>();
        ps.put("action", this.send_action);
        ps.put("username", this.user);
        ps.put("password", MD5StringUtil.MD5Encode(this.password));
        ps.put("phone", phoneNumber);
        ps.put("content", content);
        ps.put("encode", "utf8");
        final String result = HttpClientUtils.post(this.api, ps);
        if (!"100".equals(result)) {
            MomingMessageProcessor.log.error("send content {} to {} failed, responseCode is {}", new Object[] { content, phoneNumber, result });
        }
        return content;
    }
    
    public String getUser() {
        return this.user;
    }
    
    public void setUser(final String user) {
        this.user = user;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public String getApi() {
        return this.api;
    }
    
    public void setApi(final String api) {
        this.api = api;
    }
    
    static {
        log = LoggerFactory.getLogger((Class)MomingMessageProcessor.class);
    }
}
