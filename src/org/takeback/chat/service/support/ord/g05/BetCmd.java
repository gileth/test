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
                MessageUtils.sendCMD(session, "alert", "庄家无需下注!");
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
                    MessageUtils.sendCMD(session, "alert", "错误的下注命令!");
                    return;
                }
                deposit = money;
            }
            final Integer masterRecordId = room.getMasterRecordId();
            try {
                this.gameG05Service.bet(room, user, money, deposit, masterRecordId, type);
                MessageUtils.sendCMD(session, "alert", "下注成功!");
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
            MessageUtils.sendCMD(session, "alert", "非投注时间!");
        }
    }
    
    private String buildMessage(final Double money) {
        return "<span style='color:#B22222'>[注] </span><span style='color:orange;font-style:italic;font-weight:bold;font-size:18px;'>" + money + "</span> ";
    }
}