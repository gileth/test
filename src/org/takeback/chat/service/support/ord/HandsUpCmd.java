// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.support.ord;

import org.takeback.chat.lottery.listeners.RoomAndLotteryListener;
import org.takeback.chat.lottery.listeners.GameException;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.store.user.User;
import org.takeback.chat.store.room.Room;
import org.springframework.web.socket.WebSocketSession;
import org.takeback.chat.entity.Message;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component("handsUpCmd")
public class HandsUpCmd implements Command
{
    @Override
    public void exec(final Map<String, Object> data, final Message message, final WebSocketSession session, final Room room, final User user) {
        user.setHandsUp(true);
        final RoomAndLotteryListener listener = room.getRoomAndLotteryListener();
        try {
            if (listener != null) {
                if (listener.onBeforeStart(room)) {
                    room.start();
                }
            }
            else {
                room.start();
            }
        }
        catch (GameException e) {
            MessageUtils.sendCMD(session, "alert", e.getMessage());
        }
    }
}
