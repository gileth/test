// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.mail;

import org.springframework.mail.SimpleMailMessage;
import javax.mail.internet.MimeMessage;
import org.takeback.util.exception.CodedBaseRuntimeException;
import java.util.Date;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class MailHelper
{
    @Autowired
    private JavaMailSenderImpl mailSender;
    private static MailHelper mailHelper;
    
    private MailHelper() {
        MailHelper.mailHelper = this;
    }
    
    public static MailHelper instance() {
        return MailHelper.mailHelper;
    }
    
    public void sendHtmlMail(final String title, final String content, final String receiver) {
        this.sendHtmlMail(new Mail(title, content, receiver));
    }
    
    public void sendHtmlMail(final Mail mail) {
        if (this.mailSender == null) {
            throw new IllegalArgumentException("mailSender is null, did not define this in spring as a bean ?");
        }
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        try {
            mimeMessageHelper.setSentDate(new Date());
            mimeMessageHelper.setFrom(this.mailSender.getUsername());
            mimeMessageHelper.setTo(mail.getReceiver());
            mimeMessageHelper.setSubject(mail.getTitle());
            mimeMessageHelper.setText(mail.getContent(), true);
            this.mailSender.send(mimeMessage);
        }
        catch (Exception e) {
            throw new CodedBaseRuntimeException("send mail to " + mail.getReceiver() + "failed");
        }
    }
    
    public void send(final Mail mail) {
        System.out.println(this.mailSender);
        if (this.mailSender == null) {
            throw new IllegalArgumentException("mailSender is null, did not define this in spring as a bean ?");
        }
        final SimpleMailMessage smm = new SimpleMailMessage();
        smm.setSentDate(new Date());
        smm.setFrom(this.mailSender.getUsername());
        smm.setTo(mail.getReceiver());
        smm.setSubject(mail.getTitle());
        smm.setText(mail.getContent());
        this.mailSender.send(smm);
    }
    
    public void send(final String title, final String content, final String receiver) {
        this.send(new Mail(title, content, receiver));
    }
}
