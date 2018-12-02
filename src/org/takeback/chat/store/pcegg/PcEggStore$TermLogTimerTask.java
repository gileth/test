// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.store.pcegg;

import java.util.Iterator;
import java.util.List;
import org.takeback.chat.entity.PcEggLog;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.entity.Message;
import org.takeback.chat.store.room.Room;
import org.takeback.core.dictionary.DictionaryController;
import org.takeback.core.dictionary.Dictionary;
import org.takeback.chat.entity.PcGameLog;
import java.util.Timer;
import java.util.TimerTask;

private class TermLogTimerTask extends TimerTask
{
    private Timer timer;
    
    TermLogTimerTask(final Timer timer) {
        this.timer = timer;
    }
    
    @Override
    public void run() {
        this.timer.cancel();
        final PcEggLog latest;
        final List<PcGameLog> logs;
        final StringBuilder content;
        final Iterator<PcGameLog> iterator;
        PcGameLog log;
        String bet;
        String userId;
        final List<Room> rms;
        final Iterator<Room> iterator2;
        Room r;
        Message msg;
        PcEggStore.access$200(PcEggStore.this).execute(() -> {
            latest = PcEggStore.this.getLastest();
            if (latest != null) {
                logs = PcEggStore.access$300(PcEggStore.this).getGameLog(latest.getId());
                if (logs.size() != 0) {
                    content = new StringBuilder();
                    content.append("<p><strong>\u672c\u671f\u4e0b\u6ce8\uff1a</strong></p>");
                    logs.iterator();
                    while (iterator.hasNext()) {
                        log = iterator.next();
                        bet = DictionaryController.instance().get("dic.pc.betKey").getText(log.getBet());
                        for (userId = log.getUserId(); userId.length() < 150; userId += "&nbsp;") {}
                        content.append("<p><strong>").append(userId).append("[").append(log.getFreeze()).append(bet).append("]").append("</strong></p>");
                    }
                    rms = PcEggStore.access$400(PcEggStore.this).getByType("G03");
                    rms.iterator();
                    while (iterator2.hasNext()) {
                        r = iterator2.next();
                        msg = new Message("TXT_SYS", 0, content.toString());
                        MessageUtils.broadcast(r, msg);
                    }
                }
            }
        });
    }
}
