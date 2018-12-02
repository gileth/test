// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.store.room;

import org.takeback.chat.lottery.Lottery;
import java.util.Iterator;
import org.takeback.chat.lottery.listeners.GameException;
import java.io.Serializable;
import java.util.Set;
import java.util.TimerTask;

private static class LotteryExpireTask extends TimerTask
{
    @Override
    public void run() {
        final long now;
        Set<String> list1;
        Set<String> list2;
        LotteryFactory.access$200().execute(() -> {
            now = System.currentTimeMillis() / 1000L;
            LotteryFactory.access$400().writeLock().lock();
            try {
                list1 = LotteryFactory.access$500().remove(now);
                list2 = LotteryFactory.access$500().remove(now - 1L);
            }
            finally {
                LotteryFactory.access$400().writeLock().unlock();
            }
            this.setExpire(list1);
            this.setExpire(list2);
        });
    }
    
    private void setExpire(final Set<String> list) {
        if (list != null && !list.isEmpty()) {
            for (final String key : list) {
                final String[] tmp = key.split("@");
                final Room room = LotteryFactory.access$100().get(tmp[1]);
                final Lottery lottery = room.getLottery(tmp[0]);
                try {
                    if (lottery == null || !lottery.isOpen() || !lottery.getStatus().equals("0")) {
                        continue;
                    }
                    lottery.expired();
                    room.getLotteries().invalidate((Object)tmp[0]);
                }
                catch (GameException e) {
                    LotteryFactory.access$300().error("FATAL: Set lottery to expire state failed: ", (Throwable)e);
                }
            }
        }
    }
}
