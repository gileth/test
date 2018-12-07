// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.lottery.listeners;

import java.util.Map;
import org.takeback.chat.lottery.LotteryDetail;
import java.math.BigDecimal;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.entity.Message;
import org.takeback.chat.store.user.User;
import java.io.Serializable;
import org.takeback.chat.store.room.Room;
import org.takeback.chat.lottery.Lottery;
import org.takeback.chat.store.room.LotteryFactory;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.takeback.chat.service.LotteryService;
import org.takeback.chat.store.user.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;

public class DefaultGameListener implements RoomAndLotteryListener
{
    @Autowired
    protected RoomStore roomStore;
    @Autowired
    protected UserStore userStore;
    @Autowired
    protected LotteryService lotteryService;
    private ReentrantReadWriteLock roomStatusLock;
    
    public DefaultGameListener() {
        this.roomStatusLock = new ReentrantReadWriteLock();
    }
    
    @Override
    public boolean onBeforeRed(final LotteryFactory.DefaultLotteryBuilder builder) throws GameException {
        if (!"2".equals(builder.getType())) {
            final int affected = this.lotteryService.moneyDown(builder.getSender(), builder.getMoney().doubleValue());
            if (affected == 0) {
                throw new GameException(500, "余额不足!");
            }
        }
        return true;
    }
    
    @Override
    public void onRed(final LotteryFactory.DefaultLotteryBuilder builder) throws GameException {
        if ("2".equals(builder.getType())) {
            final Integer expired = Integer.valueOf(this.getConifg(builder.getRoomId(), "conf_expired"));
            builder.setExpiredSeconds(expired);
        }
        else {
            builder.setExpiredSeconds(30);
        }
        builder.build();
    }
    
    @Override
    public void onFinished(final Lottery lottery) throws GameException {
        if ("1".equals(lottery.getType())) {
            final Room room = this.roomStore.get(lottery.getRoomId());
            final User sender = this.userStore.get(lottery.getSender());
            final String msg = "<span style='color:#B22222'>" + sender.getNickName() + "的红包已被领完.</span>";
            final Message notice = new Message("TXT_SYS", 0, msg);
            MessageUtils.broadcast(room, notice);
        }
    }
    
    @Override
    public void onExpired(final Lottery lottery) throws GameException {
        final Room room = this.roomStore.get(lottery.getRoomId());
        this.roomStatusLock.readLock().lock();
        try {
            if (!this.onBeforeExpire(lottery, room)) {
                return;
            }
        }
        finally {
            this.roomStatusLock.readLock().unlock();
        }
        this.roomStatusLock.writeLock().lock();
        try {
            if (!this.onBeforeExpire(lottery, room)) {
                return;
            }
            MessageUtils.broadcast(room, new Message("gameOver", null));
            this.processExpireEvent(lottery);
        }
        finally {
            this.roomStatusLock.writeLock().unlock();
        }
    }
    
    protected boolean onBeforeExpire(final Lottery lottery, final Room room) {
        if (lottery.getStatus().equals("2") || "9".equals(room.getStatus())) {
            return false;
        }
        if ("1".equals(lottery.getType())) {
            final BigDecimal bd = this.lotteryService.giftLotteryExpired(lottery);
            final Message msg = new Message("TXT_SYS", 0, "<span style='color:#B22222'>您发出的红包未被抢完," + bd + "金额已经退到您的账户!<span>");
            MessageUtils.send(lottery.getSender(), this.roomStore.get(lottery.getRoomId()), msg);
            return false;
        }
        return true;
    }
    
    protected void processExpireEvent(final Lottery lottery) throws GameException {
    }
    
    @Override
    public boolean onBeforeOpen(final Integer uid, final Lottery lottery) throws GameException {
        return false;
    }
    
    @Override
    public void onStart(final Room room) throws GameException {
        this.roomStatusLock.readLock().lock();
        try {
            if ("1".equals(room.getStatus()) || "9".equals(room.getStatus())) {
                return;
            }
        }
        finally {
            this.roomStatusLock.readLock().unlock();
        }
        this.roomStatusLock.writeLock().lock();
        try {
            if ("1".equals(room.getStatus()) || "9".equals(room.getStatus())) {
                return;
            }
            MessageUtils.broadcast(room, new Message("gameBegin", null));
            this.processStartEvent(room);
        }
        finally {
            this.roomStatusLock.writeLock().unlock();
        }
    }
    
    protected void processStartEvent(final Room room) throws GameException {
    }
    
    @Override
    public boolean onBeforeStart(final Room room) throws GameException {
        return false;
    }
    
    @Override
    public void onOpen(final Lottery lottery, final LotteryDetail lotteryDetail) throws GameException {
        if ("1".equals(lottery.getType())) {
            this.lotteryService.saveLotteryDetail(lottery, lotteryDetail);
            String sendNickName = null;
            final User opener = this.userStore.get(lotteryDetail.getUid());
            if (0 == lottery.getSender()) {
                sendNickName = "系统";
            }
            else {
                final User sender = this.userStore.get(lottery.getSender());
                if (opener.getId().equals(sender.getId())) {
                    sendNickName = "自己";
                }
                else {
                    sendNickName = sender.getNickName();
                }
            }
            final String msg = opener.getNickName() + "领取了" + sendNickName + "发的红包";
            final Message notice = new Message("TXT_SYS", 0, msg);
            MessageUtils.broadcast(this.roomStore.get(lottery.getRoomId()), notice);
        }
    }
    
    public String getConifg(final String roomId, final String key) throws GameException {
        final Room room = this.roomStore.get(roomId);
        final Map<String, Object> properties = room.getProperties();
        if (properties.containsKey(key)) {
            return properties.get(key).toString();
        }
        throw new GameException(500, "缺少配置项[" + key + "]");
    }
}
