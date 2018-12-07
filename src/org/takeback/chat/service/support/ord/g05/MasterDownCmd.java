// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.support.ord.g05;

import java.io.Serializable;
import org.takeback.chat.entity.PubUser;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.store.user.User;
import org.takeback.chat.store.room.Room;
import org.springframework.web.socket.WebSocketSession;
import org.takeback.chat.entity.Message;
import java.util.Map;
import org.takeback.chat.store.user.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.GameG05Service;
import org.springframework.stereotype.Component;
import org.takeback.chat.service.support.ord.Command;

@Component("masterDownCmd")
public class MasterDownCmd implements Command
{
    @Autowired
    GameG05Service gameG05Service;
    @Autowired
    UserStore userStore;
    
    @Override
    public void exec(final Map<String, Object> data, final Message message, final WebSocketSession session, final Room room, final User user) {
        if (!user.getId().equals(room.getOwner())) {
            MessageUtils.sendCMD(session, "alert", "非法操作!");
            return;
        }
        if (room.getStep() == Room.STEP_CHECK3 || room.getStep() == Room.STEP_PLAY_FINISHED) {
            this.gameG05Service.restoreMasterMoney(room.getMasterRecordId());
            final PubUser master = this.gameG05Service.get(PubUser.class, room.getMaster());
            final Message msg = new Message("TXT_SYS", user.getId(), "<span style='color:#B22222'>" + master.getNickName() + "已下庄,剩余金币已退还账户!</span>");
            MessageUtils.broadcast(room, msg);
            room.setMaster(0);
            room.setStep(Room.STEP_FREE);
            room.setMasterRecordId(0);
        }
        else {
            MessageUtils.sendCMD(session, "alert", GameG05Service.suggestNext(room.getStep()));
        }
    }
    
    private String buildMessage(final Double money) {
        return "<span style='color:#B22222'>[注] </span><span style='color:orange;font-style:italic;font-weight:bold;font-size:18px;'>" + money + "</span> ";
    }
}