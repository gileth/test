// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.support.ord.g05;

import java.util.Iterator;
import java.util.List;
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

@Component("getMasterCmd")
public class GetMasterCmd implements Command
{
    static Double MIN_FREEZE;
    @Autowired
    GameG05Service gameG05Service;
    
    @Override
    public void exec(final Map<String, Object> data, final Message message, final WebSocketSession session, final Room room, final User user) {
        final Double freeze = Double.valueOf(data.get("freeze").toString());
        if (freeze < GetMasterCmd.MIN_FREEZE) {
            MessageUtils.sendCMD(session, "alert", "\u623f\u95f4\u8d77\u6807:" + GetMasterCmd.MIN_FREEZE);
            return;
        }
        try {
            if (room.getStep() == Room.STEP_MASTER || room.getStep() == Room.STEP_CHECK1 || room.getStep() == Room.STEP_CHECK2) {
                final List<GcMasterRecord> masterRecords = this.gameG05Service.getMasterRecrods(room.getId());
                if (masterRecords.size() > 0) {
                    final GcMasterRecord maxRecord = masterRecords.get(0);
                    final Double maxFreeze = maxRecord.getFreeze();
                    if (freeze <= maxFreeze) {
                        MessageUtils.sendCMD(session, "alert", "\u7ade\u6807\u5fc5\u987b\u5927\u4e8e:" + maxFreeze);
                        return;
                    }
                    for (final GcMasterRecord rec : masterRecords) {
                        if (rec.getUid().equals(user.getId())) {
                            this.gameG05Service.addMasterFreeze(user, rec.getId(), freeze - rec.getFreeze());
                            final Message msg = new Message("TXT", user.getId(), this.buildMessage(freeze));
                            msg.setHeadImg(user.getHeadImg());
                            msg.setNickName(user.getNickName());
                            MessageUtils.broadcast(room, msg);
                            room.setMaster(user.getId());
                            room.setStep(Room.STEP_MASTER);
                            return;
                        }
                    }
                }
                final GcMasterRecord masterRecord = this.gameG05Service.newMasterRecord(user, room, freeze);
                room.setMasterRecordId(masterRecord.getId());
                room.setMaster(user.getId());
                final Message msg2 = new Message("TXT", user.getId(), this.buildMessage(freeze));
                msg2.setHeadImg(user.getHeadImg());
                msg2.setNickName(user.getNickName());
                MessageUtils.broadcast(room, msg2);
                room.setStep(Room.STEP_MASTER);
            }
            else {
                MessageUtils.sendCMD(session, "alert", "\u975e\u7ade\u6807\u65f6\u95f4!");
            }
        }
        catch (Exception e) {
            MessageUtils.sendCMD(session, "alert", GameG05Service.suggestNext(room.getStep()));
        }
    }
    
    private String buildMessage(final Double freeze) {
        return "<span style='color:orange;font-style:italic;font-weight:bold;font-size:26px;'>" + freeze + "</span> <span style='color:#B22222;'>\u53c2\u4e0e\u7ade\u6807</span>";
    }
    
    static {
        GetMasterCmd.MIN_FREEZE = 8000.0;
    }
}
