// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.support;

import org.takeback.chat.utils.MessageUtils;
import org.takeback.util.ApplicationContextHolder;
import org.takeback.chat.service.support.ord.Command;
import java.util.Map;
import org.takeback.chat.store.user.User;
import org.takeback.chat.store.room.Room;
import org.springframework.web.socket.WebSocketSession;
import org.takeback.chat.entity.Message;
import org.springframework.stereotype.Component;

@Component("ordMessageProcessor")
public class OrdMessageProcessor extends RedMessageProcessor
{
    @Override
    public void process(final Message message, final WebSocketSession session, final Room room, final User user) {
        try {
            final String cmd = message.getCmd();
            final Map<String, Object> data = (Map<String, Object>)message.getContent();
            final Command command = (Command)ApplicationContextHolder.getBean(cmd);
            command.exec(data, message, session, room, user);
        }
        catch (Exception e) {
            e.printStackTrace();
            MessageUtils.sendCMD(session, "alert", "非法指令");
        }
    }
}
