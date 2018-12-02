// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.support.ord.g05;

import org.takeback.chat.service.GameG05Service;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.store.user.User;
import org.takeback.chat.store.room.Room;
import org.springframework.web.socket.WebSocketSession;
import org.takeback.chat.entity.Message;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.takeback.chat.service.support.ord.Command;

@Component("startBetCmd")
public class StartBetCmd implements Command
{
    @Override
    public void exec(final Map<String, Object> data, final Message message, final WebSocketSession session, final Room room, final User user) {
        if (!user.getId().equals(room.getOwner())) {
            MessageUtils.sendCMD(session, "alert", "\u975e\u6cd5\u64cd\u4f5c!");
            return;
        }
        if (room.getStep() == Room.STEP_CHECK3 || room.getStep() == Room.STEP_PLAY_FINISHED || room.getStep() == Room.STEP_START_BET) {
            room.setStep(Room.STEP_START_BET);
            final Message msg = new Message("TXT_SYS", user.getId(), "<span style='color:red'>\u5f00\u59cb\u4e0b\u6ce8!</span>");
            MessageUtils.broadcast(room, msg);
            MessageUtils.sendCMD(session, "roomStep", Room.STEP_START_BET);
        }
        else {
            MessageUtils.sendCMD(session, "alert", GameG05Service.suggestNext(room.getStep()));
        }
    }
}
