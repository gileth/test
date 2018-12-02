// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.websocket.listener;

import org.springframework.web.socket.WebSocketSession;

public interface ConnectListener
{
    void onConnect(final WebSocketSession p0);
}
