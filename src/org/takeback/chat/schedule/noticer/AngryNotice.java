// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.schedule.noticer;

import org.takeback.chat.utils.MessageUtils;
import java.io.Serializable;
import org.takeback.chat.store.room.Room;
import org.takeback.chat.entity.Message;
import org.takeback.chat.utils.LotteryUtilBean;
import java.util.concurrent.TimeUnit;
import org.takeback.chat.store.room.RoomStore;

public class AngryNotice extends Thread
{
    RoomStore roomStore;
    public static final Integer TO_SLEEP;
    private String[] rooms;
    
    public AngryNotice(final String[] rooms, final RoomStore roomStore) {
        this.rooms = rooms;
        this.roomStore = roomStore;
    }
    
    @Override
    public void run() {
            try {
                while (true) {
                    TimeUnit.SECONDS.sleep(AngryNotice.TO_SLEEP);
                    final LotteryUtilBean lub = new LotteryUtilBean();
                    final Long timeLeft = lub.getNextOpenRestTime();
                    final String txt = new StringBuffer("<span style='color:#B22222'>").append(lub.getNextStage()).append("期下注剩余时间：").append(timeLeft / 60L).append("分").append(timeLeft % 60L).append("秒</span>").toString();
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
    
    static {
        TO_SLEEP = 10;
    }
}
