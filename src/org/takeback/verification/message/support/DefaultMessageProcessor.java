// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.verification.message.support;

import org.takeback.verification.message.SmsTemplates;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import org.takeback.verification.image.VerifyCodeUtils;
import org.takeback.verification.message.MessageProcessor;

public class DefaultMessageProcessor implements MessageProcessor
{
    protected int defaultCodeLength;
    protected static final String VERIFY_CODES = "1234567890";
    
    public DefaultMessageProcessor() {
        this.defaultCodeLength = 4;
    }
    
    protected String generateCode() {
        return VerifyCodeUtils.generateVerifyCode(this.defaultCodeLength, "1234567890");
    }
    
    @Override
    public String sendCode(final String phoneNumber) {
        return this.sendCode(phoneNumber, "0");
    }
    
    @Override
    public String sendCode(final String phoneNumber, final String tpl) {
        final String code = this.generateCode();
        this.sendSMS(phoneNumber, tpl, (Map<String, String>)ImmutableMap.of("code",code));
        return code;
    }
    
    @Override
    public String sendSMS(final String phoneNumber, final String tpl, final Map<String, String> params) {
        final String content = SmsTemplates.getTemplate(tpl, params);
        if (content == null) {
            throw new IllegalArgumentException("tpl " + tpl + " is not exists");
        }
        return this.sendSMS(phoneNumber, content);
    }
    
    @Override
    public String sendSMS(final String phoneNumber, final String content) {
        return content;
    }
    
    @Override
    public void setDefaultCodeLength(final int length) {
        this.defaultCodeLength = length;
    }
}
