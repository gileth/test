// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.support.ord;

import org.takeback.chat.store.user.User;
import org.takeback.chat.store.room.Room;
import org.springframework.web.socket.WebSocketSession;
import org.takeback.chat.entity.Message;
import java.util.Map;

public interface Command
{
    void exec(final Map<String, Object> p0, final Message p1, final WebSocketSession p2, final Room p3, final User p4);
}
