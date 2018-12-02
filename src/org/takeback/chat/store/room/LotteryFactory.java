// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.store.room;

import java.util.Iterator;
import org.takeback.chat.lottery.listeners.GameException;
import java.io.Serializable;
import org.apache.commons.lang.StringUtils;
import org.takeback.chat.lottery.DefaultLottery;
import java.util.TimerTask;
import java.util.Timer;
import org.takeback.util.ApplicationContextHolder;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.LoggerFactory;
import java.util.HashSet;
import org.takeback.chat.lottery.Lottery;
import java.math.BigDecimal;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Set;
import java.util.Map;
import org.slf4j.Logger;

public class LotteryFactory
{
    private static final Logger LOGGER;
    private static Map<Long, Set<String>> lotteryRegistry;
    private static ReentrantReadWriteLock lock;
    private static RoomStore roomStore;
    private static ThreadPoolTaskExecutor executor;
    
    public static DefaultLotteryBuilder getDefaultBuilder(final BigDecimal money, final Integer number) {
        return new DefaultLotteryBuilder(money, number);
    }
    
    public static boolean removeLottery(final Lottery lottery) {
        LotteryFactory.lock.writeLock().lock();
        try {
            final long expires = (long)Math.ceil(lottery.getCreateTime().getTime() / 1000.0) + lottery.getExpiredSeconds();
            final Set<String> set = LotteryFactory.lotteryRegistry.get(expires);
            return set != null && !set.isEmpty() && set.remove(lottery.getId());
        }
        finally {
            LotteryFactory.lock.writeLock().unlock();
        }
    }
    
    public static void addLottery(final Lottery lottery) {
        final long expires = (long)Math.ceil(lottery.getCreateTime().getTime() / 1000.0) + lottery.getExpiredSeconds();
        LotteryFactory.lock.readLock().lock();
        try {
            final Set<String> list = LotteryFactory.lotteryRegistry.get(expires);
            if (list != null) {
                list.add(lottery.getId() + "@" + lottery.getRoomId());
                return;
            }
        }
        finally {
            LotteryFactory.lock.readLock().unlock();
        }
        LotteryFactory.lock.writeLock().lock();
        try {
            Set<String> list = LotteryFactory.lotteryRegistry.get(expires);
            if (list != null) {
                list.add(lottery.getId());
            }
            else {
                list = new HashSet<String>();
                list.add(lottery.getId() + "@" + lottery.getRoomId());
                LotteryFactory.lotteryRegistry.put(expires, list);
            }
        }
        finally {
            LotteryFactory.lock.writeLock().unlock();
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)LotteryFactory.class);
        LotteryFactory.lotteryRegistry = new ConcurrentHashMap<Long, Set<String>>();
        LotteryFactory.lock = new ReentrantReadWriteLock();
        LotteryFactory.roomStore = ApplicationContextHolder.getBean(DefaultRoomStore.class);
        LotteryFactory.executor = ApplicationContextHolder.getBean("threadPool", ThreadPoolTaskExecutor.class);
        final Timer timer = new Timer(true);
        timer.schedule(new LotteryExpireTask(), 0L, 1000L);
    }
    
    public static class DefaultLotteryBuilder
    {
        private Lottery lottery;
        
        DefaultLotteryBuilder(final BigDecimal money, final Integer number) {
            this.lottery = new DefaultLottery(money, number);
        }
        
        public String getLotteryId() {
            return this.lottery.getId();
        }
        
        public BigDecimal getMoney() {
            return this.lottery.getMoney();
        }
        
        public int getNumber() {
            return this.lottery.getNumber();
        }
        
        public DefaultLotteryBuilder setExpiredSeconds(final Integer seconds) {
            this.lottery.setExpiredSeconds(seconds);
            return this;
        }
        
        public DefaultLotteryBuilder setMoney(final BigDecimal money) {
            this.lottery.setMoney(money);
            return this;
        }
        
        public int getExpiredSeconds() {
            return this.lottery.getExpiredSeconds();
        }
        
        public DefaultLotteryBuilder setType(final String type) {
            this.lottery.setType(type);
            return this;
        }
        
        public String getType() {
            return this.lottery.getType();
        }
        
        public DefaultLotteryBuilder setSender(final int sender) {
            this.lottery.setSender(sender);
            return this;
        }
        
        public String getTitle() {
            return this.lottery.getTitle();
        }
        
        public DefaultLotteryBuilder setTitle(final String title) {
            this.lottery.setTitle(title);
            return this;
        }
        
        public int getSender() {
            return this.lottery.getSender();
        }
        
        public DefaultLotteryBuilder setDescription(final String description) {
            this.lottery.setDescription(description);
            return this;
        }
        
        public String getDescription() {
            return this.lottery.getDescription();
        }
        
        public DefaultLotteryBuilder setRoom(final Room room) {
            this.lottery.setRoomId(room.getId());
            this.lottery.setRoomAndLotteryListener(room.getRoomAndLotteryListener());
            room.addLottery(this.lottery);
            return this;
        }
        
        public DefaultLotteryBuilder setRoomId(final String roomId) {
            this.lottery.setRoomId(roomId);
            return this;
        }
        
        public Room getRoom() {
            if (StringUtils.isNotEmpty(this.lottery.getRoomId())) {
                return LotteryFactory.roomStore.get(this.lottery.getRoomId());
            }
            return null;
        }
        
        public String getRoomId() {
            return this.lottery.getRoomId();
        }
        
        public Lottery build() {
            if (this.lottery.getExpiredSeconds() == 0 || StringUtils.isEmpty(this.lottery.getRoomId())) {
                return this.lottery;
            }
            LotteryFactory.addLottery(this.lottery);
            return this.lottery;
        }
    }
    
    private static class LotteryExpireTask extends TimerTask
    {
        @Override
        public void run() {
            LotteryFactory.executor.execute(() -> {
                long now;
                Set<String> list1;
                Set<String> list2;
                now = System.currentTimeMillis() / 1000L;
                LotteryFactory.lock.writeLock().lock();
                try {
                    list1 = LotteryFactory.lotteryRegistry.remove(now);
                    list2 = LotteryFactory.lotteryRegistry.remove(now - 1L);
                }
                finally {
                    LotteryFactory.lock.writeLock().unlock();
                }
                this.setExpire(list1);
                this.setExpire(list2);
            });
        }
        
        private void setExpire(final Set<String> list) {
            if (list != null && !list.isEmpty()) {
                for (final String key : list) {
                    final String[] tmp = key.split("@");
                    final Room room = LotteryFactory.roomStore.get(tmp[1]);
                    final Lottery lottery = room.getLottery(tmp[0]);
                    try {
                        if (lottery == null || !lottery.isOpen() || !lottery.getStatus().equals("0")) {
                            continue;
                        }
                        lottery.expired();
                        room.getLotteries().invalidate((Object)tmp[0]);
                    }
                    catch (GameException e) {
                        LotteryFactory.LOGGER.error("FATAL: Set lottery to expire state failed: ", (Throwable)e);
                    }
                }
            }
        }
    }
}
