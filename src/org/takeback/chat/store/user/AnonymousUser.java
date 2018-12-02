// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.store.user;

import org.springframework.web.socket.WebSocketSession;

public class AnonymousUser extends User
{
    public AnonymousUser(final WebSocketSession webSocketSession) {
        super.setWebSocketSession(webSocketSession);
    }
}
