// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.support.ord.g05;

import java.io.Serializable;
import org.takeback.chat.entity.GcMasterRecord;
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

@Component("finishBetCmd")
public class FinishBetCmd implements Command
{
    @Autowired
    GameG05Service gameG05Service;
    
    @Override
    public void exec(final Map<String, Object> data, final Message message, final WebSocketSession session, final Room room, final User user) {
        if (!user.getId().equals(room.getOwner())) {
            MessageUtils.sendCMD(session, "alert", "\u975e\u6cd5\u64cd\u4f5c!");
            return;
        }
        if (room.getStep() == Room.STEP_START_BET || room.getStep() == Room.STEP_FINISH_BET) {
            if (this.gameG05Service.getBetRecords(room.getMasterRecordId()).size() == 0) {
                final GcMasterRecord gmr = this.gameG05Service.get(GcMasterRecord.class, room.getMasterRecordId());
                this.gameG05Service.restoreMasterMoney(room.getMasterRecordId());
                room.setMaster(0);
                room.setStep(Room.STEP_FREE);
                room.setMasterRecordId(0);
                final Message msg = new Message("TXT", room.getManager(), "<span style='color:#B22222'>\u65e0\u4eba\u4e0b\u6ce8," + gmr.getFreeze() + " \u5df2\u9000\u8fd8\u5e84\u4e3b\u8d26\u6237!</span>");
                msg.setHeadImg(user.getHeadImg());
                msg.setNickName(user.getNickName());
                MessageUtils.broadcast(room, msg);
                return;
            }
            room.setStep(Room.STEP_FINISH_BET);
            final Message msg2 = new Message("TXT_SYS", user.getId(), "<span style='color:red'>\u622a\u6b62\u4e0b\u6ce8!</span>");
            MessageUtils.broadcast(room, msg2);
            MessageUtils.sendCMD(session, "roomStep", Room.STEP_FINISH_BET);
        }
        else {
            MessageUtils.sendCMD(session, "alert", GameG05Service.suggestNext(room.getStep()));
        }
    }
}
