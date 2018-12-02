// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.utils;

import org.takeback.chat.entity.Message;
import org.springframework.web.socket.WebSocketSession;

public class FailedResult
{
    private WebSocketSession session;
    private Message message;
    private Throwable error;
    
    public FailedResult(final WebSocketSession session, final Message message) {
        this.session = session;
        this.message = message;
    }
    
    public FailedResult(final WebSocketSession session, final Message message, final Throwable error) {
        this(session, message);
        this.error = error;
    }
    
    public WebSocketSession getSession() {
        return this.session;
    }
    
    public void setSession(final WebSocketSession session) {
        this.session = session;
    }
    
    public Message getMessage() {
        return this.message;
    }
    
    public void setMessage(final Message message) {
        this.message = message;
    }
    
    public Throwable getError() {
        return this.error;
    }
    
    public void setError(final Throwable error) {
        this.error = error;
    }
}
