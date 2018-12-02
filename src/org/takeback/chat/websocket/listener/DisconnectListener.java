// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.websocket.listener;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

public interface DisconnectListener
{
    void onDisconnect(final WebSocketSession p0, final CloseStatus p1);
}
