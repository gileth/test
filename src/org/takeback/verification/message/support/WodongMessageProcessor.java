// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.verification.message.support;

import org.slf4j.LoggerFactory;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import java.util.Map;
import org.takeback.util.httpclient.HttpClientUtils;
import java.util.LinkedHashMap;
import org.slf4j.Logger;

public class WodongMessageProcessor extends DefaultMessageProcessor
{
    private static final Logger log;
    private String API_URL;
    private String API_URL_GBK;
    private String userid;
    private String account;
    private String password;
    
    public WodongMessageProcessor() {
        this.API_URL = "http://115.29.242.32:8888/sms.aspx";
        this.API_URL_GBK = "http://115.29.242.32:8888/smsGBK.aspx";
    }
    
    @Override
    public String sendSMS(final String phoneNumber, final String content) {
        final LinkedHashMap<String, String> ps = new LinkedHashMap<String, String>();
        ps.put("action", "send");
        ps.put("userid", this.userid);
        ps.put("account", this.account);
        ps.put("password", this.password);
        ps.put("mobile", phoneNumber);
        ps.put("content", content);
        final String result = HttpClientUtils.post(this.API_URL, ps);
        System.out.println(result);
        try {
            final Document doc = DocumentHelper.parseText(result);
            final Element root = doc.getRootElement();
            if (!"Success".equals(root.elementText("returnstatus"))) {
                if (!"ok".equals(root.elementText("message"))) {
                    WodongMessageProcessor.log.error("send content {} to {} failed, responseMsg is {}", new Object[] { content, phoneNumber, root.elementText("message") });
                }
            }
        }
        catch (DocumentException e) {
            WodongMessageProcessor.log.error("send content {} to {} failed, responseMsg is {}", new Object[] { content, phoneNumber, result });
        }
        return content;
    }
    
    public static void main(final String[] args) {
    }
    
    public String getAPI_URL() {
        return this.API_URL;
    }
    
    public void setAPI_URL(final String API_URL) {
        this.API_URL = API_URL;
    }
    
    public String getAPI_URL_GBK() {
        return this.API_URL_GBK;
    }
    
    public void setAPI_URL_GBK(final String API_URL_GBK) {
        this.API_URL_GBK = API_URL_GBK;
    }
    
    public String getUserid() {
        return this.userid;
    }
    
    public void setUserid(final String userid) {
        this.userid = userid;
    }
    
    public String getAccount() {
        return this.account;
    }
    
    public void setAccount(final String account) {
        this.account = account;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    static {
        log = LoggerFactory.getLogger((Class)WodongMessageProcessor.class);
    }
}
