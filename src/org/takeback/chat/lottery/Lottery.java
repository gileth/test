// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.lottery;

import org.takeback.chat.lottery.listeners.RoomAndLotteryListener;
import java.util.Map;
import java.util.Date;
import org.takeback.chat.lottery.listeners.GameException;
import java.math.BigDecimal;
import org.takeback.chat.store.Item;

public interface Lottery extends Item
{
    BigDecimal open(final int p0) throws GameException;
    
    BigDecimal getMoney();
    
    void setMoney(final BigDecimal p0);
    
    Integer getNumber();
    
    void setNumber(final Integer p0);
    
    BigDecimal fakeOpen(final int p0) throws GameException;
    
    Integer getRestNumber();
    
    Integer getSender();
    
    void setSender(final Integer p0);
    
    BigDecimal getRestMoney();
    
    String getId();
    
    void setId(final String p0);
    
    String getRoomId();
    
    void setRoomId(final String p0);
    
    String getDescription();
    
    String getTitle();
    
    void setTitle(final String p0);
    
    void setDescription(final String p0);
    
    Date getCreateTime();
    
    void setCreateTime(final Date p0);
    
    String getStatus();
    
    void setStatus(final String p0);
    
    boolean isOpen();
    
    boolean isExpired();
    
    void setExpiredSeconds(final Integer p0);
    
    Integer getExpiredSeconds();
    
    void addDetail(final LotteryDetail p0);
    
    Map<Integer, LotteryDetail> getDetail();
    
    void setRoomAndLotteryListener(final RoomAndLotteryListener p0);
    
    void finished() throws GameException;
    
    void expired() throws GameException;
    
    String getType();
    
    void setType(final String p0);
}
