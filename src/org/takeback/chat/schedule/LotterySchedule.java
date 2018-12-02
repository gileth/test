// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.schedule;

import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.io.Serializable;
import org.takeback.chat.lottery.Lottery;
import org.takeback.chat.store.room.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;

public class LotterySchedule
{
    @Autowired
    RoomStore roomStore;
    
    public void work() {
        final List<Room> roomList = this.roomStore.getByCatalog("");
        try {
            for (final Room room : roomList) {
                final Map<String, Lottery> lotteries = (Map<String, Lottery>)room.getLotteries().asMap();
                for (Lottery lottery : lotteries.values()) {
                    if (lottery.isOpen() && lottery.isExpired()) {
                        try {
                            lottery.expired();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            continue;
                        }
                    }
                    if (!lottery.isOpen() && lottery.isExpired()) {
                        final String roomId = lottery.getRoomId();
                        final Room rm = this.roomStore.get(roomId);
                        rm.getLotteries().invalidate((Object)lottery.getId());
                        lottery = null;
                        room.showLotteries();
                    }
                }
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}
