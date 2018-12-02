// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.support;

import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.store.user.User;
import org.takeback.chat.store.room.Room;
import org.springframework.web.socket.WebSocketSession;
import org.takeback.chat.entity.Message;
import org.springframework.stereotype.Component;

@Component("txtMessageProcessor")
public class TxtMessageProcessor implements MessageProcessor
{
    @Override
    public void process(final Message message, final WebSocketSession session, final Room room, final User user) {
        MessageUtils.broadcast(room, message);
    }
}
