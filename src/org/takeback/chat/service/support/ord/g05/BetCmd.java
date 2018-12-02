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
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.GameG05Service;
import org.springframework.stereotype.Component;
import org.takeback.chat.service.support.ord.Command;

@Component("betCmd")
public class BetCmd implements Command
{
    @Autowired
    GameG05Service gameG05Service;
    
    @Override
    public void exec(final Map<String, Object> data, final Message message, final WebSocketSession session, final Room room, final User user) {
        if (room.getStep() == Room.STEP_START_BET) {
            if (room.getMaster().equals(user.getId())) {
                MessageUtils.sendCMD(session, "alert", "\u5e84\u5bb6\u65e0\u9700\u4e0b\u6ce8!");
                return;
            }
            final String type = (String) data.get("type");
            final Double money = Double.valueOf(data.get("money").toString());
            Double deposit = 0.0;
            if ("1".equals(type)) {
                final Double maxTypes = Double.valueOf(room.getProperties().get("conf_n15").toString());
                deposit = money * maxTypes;
            }
            else {
                if (!"2".equals(type)) {
                    MessageUtils.sendCMD(session, "alert", "\u9519\u8bef\u7684\u4e0b\u6ce8\u547d\u4ee4!");
                    return;
                }
                deposit = money;
            }
            final Integer masterRecordId = room.getMasterRecordId();
            try {
                this.gameG05Service.bet(room, user, money, deposit, masterRecordId, type);
                MessageUtils.sendCMD(session, "alert", "\u4e0b\u6ce8\u6210\u529f!");
                final Message msg = new Message("TXT", user.getId(), this.buildMessage(money));
                msg.setHeadImg(user.getHeadImg());
                msg.setNickName(user.getNickName());
                MessageUtils.broadcast(room, msg);
            }
            catch (Exception e) {
                MessageUtils.sendCMD(session, "alert", e.getMessage());
            }
        }
        else {
            MessageUtils.sendCMD(session, "alert", "\u975e\u6295\u6ce8\u65f6\u95f4!");
        }
    }
    
    private String buildMessage(final Double money) {
        return "<span style='color:#B22222'>[\u6ce8] </span><span style='color:orange;font-style:italic;font-weight:bold;font-size:18px;'>" + money + "</span> ";
    }
}
