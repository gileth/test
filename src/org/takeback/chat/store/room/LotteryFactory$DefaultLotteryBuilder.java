// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.store.room;

import java.io.Serializable;
import org.apache.commons.lang.StringUtils;
import org.takeback.chat.lottery.DefaultLottery;
import java.math.BigDecimal;
import org.takeback.chat.lottery.Lottery;

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
            return LotteryFactory.access$100().get(this.lottery.getRoomId());
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
