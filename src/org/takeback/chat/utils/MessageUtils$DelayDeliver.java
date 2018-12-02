// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.utils;

import java.util.concurrent.TimeUnit;
import org.takeback.chat.entity.Message;
import org.takeback.chat.store.room.Room;

static class DelayDeliver implements Runnable
{
    private long delay;
    private Room room;
    private Message message;
    
    DelayDeliver(final long delay, final Room room, final Message message) {
        this(room, message);
        this.delay = delay;
    }
    
    DelayDeliver(final Room room, final Message message) {
        this.delay = 5L;
        this.room = room;
        this.message = message;
    }
    
    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(this.delay);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        MessageUtils.broadcast(this.room, this.message);
    }
}
