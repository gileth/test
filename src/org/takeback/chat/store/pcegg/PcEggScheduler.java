// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.store.pcegg;

import org.slf4j.LoggerFactory;
import java.util.Date;
import org.joda.time.LocalTime;
import java.util.TimerTask;
import java.util.Timer;
import org.joda.time.LocalDateTime;
import org.takeback.chat.entity.PcEggLog;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;

public class PcEggScheduler
{
    private static final Logger LOGGER;
    private PcEggStore pcEggStore;
    
    public void init() throws IOException {
        final Callable<Void> callable = (Callable<Void>)(() -> {
            for (int i = 0; i < 5 && !this.pcEggStore.initData(); ++i) {
                try {
                    TimeUnit.SECONDS.sleep(i * 2);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.launchSchedule();
            return null;
        });
        final FutureTask<Void> task = new FutureTask<Void>(callable);
        new Thread(task).start();
    }
    
    private void launchSchedule() {
        while (!Thread.currentThread().isInterrupted()) {
            final PcEggLog pcEgg = this.pcEggStore.getLastest();
            if (pcEgg != null && pcEgg.getExp() == null) {
                this.schedule(pcEgg);
                break;
            }
            PcEggScheduler.LOGGER.info("Cannot get new data, wait for 3 seconds to retry.");
            try {
                TimeUnit.SECONDS.sleep(3L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void schedule(final PcEggLog pcEgg) {
        LocalDateTime localDateTime = LocalDateTime.fromDateFields(pcEgg.getExpireTime());
        localDateTime = localDateTime.minusSeconds(10);
        final Date workTime = localDateTime.toDate();
        final Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (PcEggScheduler.this.pcEggStore.work(pcEgg) && pcEgg.getLucky() != null) {
                    timer.cancel();
                    try {
                        TimeUnit.SECONDS.sleep(3L);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    PcEggScheduler.this.launchSchedule();
                }
                else {
                    final PeriodConfig config = PcEggScheduler.this.pcEggStore.getPeriodConfig(new LocalTime());
                    if (System.currentTimeMillis() - pcEgg.getExpireTime().getTime() > (config.getPeriodSeconds() - 30) * 1000) {
                        timer.cancel();
                        try {
                            TimeUnit.SECONDS.sleep(20L);
                        }
                        catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                        PcEggScheduler.this.launchSchedule();
                    }
                }
            }
        }, workTime, 5000L);
    }
    
    public PcEggStore getPcEggStore() {
        return this.pcEggStore;
    }
    
    public void setPcEggStore(final PcEggStore pcEggStore) {
        this.pcEggStore = pcEggStore;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PcEggScheduler.class);
    }
}
