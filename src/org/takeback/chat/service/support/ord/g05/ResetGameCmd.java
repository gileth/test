// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.support.ord.g05;

import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.store.user.User;
import org.takeback.chat.store.room.Room;
import org.springframework.web.socket.WebSocketSession;
import org.takeback.chat.entity.Message;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.takeback.chat.service.support.ord.Command;

@Component("resetGameCmd")
public class ResetGameCmd implements Command
{
    @Override
    public void exec(final Map<String, Object> data, final Message message, final WebSocketSession session, final Room room, final User user) {
        room.setStep(Room.STEP_FREE);
        final Message msg = new Message("TXT_SYS", user.getId(), "<span style='color:red'>\u6e38\u620f\u91cd\u7f6e!</span>");
        MessageUtils.broadcast(room, msg);
        MessageUtils.sendCMD(session, "roomStep", Room.STEP_FREE);
    }
}
