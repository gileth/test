// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.mail;

public class Mail
{
    private String title;
    private String content;
    private String receiver;
    
    public Mail(final String title, final String content, final String receiver) {
        this.title = title;
        this.content = content;
        this.receiver = receiver;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public String getContent() {
        return this.content;
    }
    
    public void setContent(final String content) {
        this.content = content;
    }
    
    public String getReceiver() {
        return this.receiver;
    }
    
    public void setReceiver(final String receiver) {
        this.receiver = receiver;
    }
}
