// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.websocket.listener;

import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public interface MessageReceiveListener
{
    void onMessageReceive(final WebSocketSession p0, final WebSocketMessage<?> p1);
}
