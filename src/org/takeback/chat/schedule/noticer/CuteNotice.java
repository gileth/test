// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.schedule.noticer;

import org.takeback.chat.utils.MessageUtils;
import java.io.Serializable;
import org.takeback.chat.store.room.Room;
import org.takeback.chat.entity.Message;
import java.util.concurrent.TimeUnit;
import org.takeback.chat.utils.LotteryUtilBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;

public class CuteNotice extends Thread
{
    @Autowired
    RoomStore roomStore;
    private String[] rooms;
    
    public CuteNotice(final String[] rooms, final RoomStore roomStore) {
        this.rooms = rooms;
        this.roomStore = roomStore;
    }
    
    @Override
    public void run() {
            try {
                while (true) {
                    LotteryUtilBean lub = new LotteryUtilBean();
                    final Long timeLeft = lub.getNextOpenRestTime();
                    TimeUnit.SECONDS.sleep(timeLeft + 1L);
                    lub = new LotteryUtilBean();
                    final String txt = new StringBuffer("<span style='color:#B22222'>").append(lub.getCurrentStage()).append("\u671f\u505c\u6b62\u4e0b\u6ce8,").append(lub.getNextStage()).append("\u5f00\u59cb\u4e0b\u6ce8\uff01</span>").toString();
                    final Message msg = new Message("TXT_SYS", 0, txt);
                    for (final String roomId : this.rooms) {
                        final Room r = this.roomStore.get(roomId);
                        if (r != null) {
                            MessageUtils.broadcast(r, msg);
                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
    }
}
