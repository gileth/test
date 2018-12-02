// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.store.pcegg;

import org.joda.time.LocalTime;
import java.util.concurrent.TimeUnit;
import java.util.Timer;
import org.takeback.chat.entity.PcEggLog;
import java.util.TimerTask;

class PcEggScheduler$1 extends TimerTask {
    final /* synthetic */ PcEggLog val$pcEgg;
    final /* synthetic */ Timer val$timer;
    
    @Override
    public void run() {
        if (PcEggScheduler.access$000(PcEggScheduler.this).work(this.val$pcEgg) && this.val$pcEgg.getLucky() != null) {
            this.val$timer.cancel();
            try {
                TimeUnit.SECONDS.sleep(3L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            PcEggScheduler.access$100(PcEggScheduler.this);
        }
        else {
            final PeriodConfig config = PcEggScheduler.access$000(PcEggScheduler.this).getPeriodConfig(new LocalTime());
            if (System.currentTimeMillis() - this.val$pcEgg.getExpireTime().getTime() > (config.getPeriodSeconds() - 30) * 1000) {
                this.val$timer.cancel();
                try {
                    TimeUnit.SECONDS.sleep(20L);
                }
                catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                PcEggScheduler.access$100(PcEggScheduler.this);
            }
        }
    }
}