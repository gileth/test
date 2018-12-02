// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.support;

import org.takeback.chat.store.user.User;
import org.takeback.chat.store.room.Room;
import org.springframework.web.socket.WebSocketSession;
import org.takeback.chat.entity.Message;

public interface MessageProcessor
{
    void process(final Message p0, final WebSocketSession p1, final Room p2, final User p3);
}
