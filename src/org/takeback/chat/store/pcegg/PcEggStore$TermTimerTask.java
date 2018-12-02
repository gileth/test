// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.store.pcegg;

import java.util.Timer;
import org.takeback.chat.entity.PcEggLog;
import java.util.TimerTask;

private class TermTimerTask extends TimerTask
{
    private PcEggLog egg;
    private Timer timer;
    
    TermTimerTask(final PcEggLog egg, final Timer timer) {
        this.egg = egg;
        this.timer = timer;
    }
    
    @Override
    public void run() {
        this.timer.cancel();
        PcEggStore.access$000(PcEggStore.this, this.egg);
        PcEggStore.access$100(PcEggStore.this);
    }
}
