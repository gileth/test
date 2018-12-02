// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.lottery.listeners;

import org.takeback.chat.store.room.Room;
import org.takeback.chat.lottery.LotteryDetail;
import org.takeback.chat.lottery.Lottery;
import org.takeback.chat.store.room.LotteryFactory;

public interface RoomAndLotteryListener
{
    boolean onBeforeRed(final LotteryFactory.DefaultLotteryBuilder p0) throws GameException;
    
    void onRed(final LotteryFactory.DefaultLotteryBuilder p0) throws GameException;
    
    void onFinished(final Lottery p0) throws GameException;
    
    void onExpired(final Lottery p0) throws GameException;
    
    boolean onBeforeOpen(final Integer p0, final Lottery p1) throws GameException;
    
    void onOpen(final Lottery p0, final LotteryDetail p1) throws GameException;
    
    void onStart(final Room p0) throws GameException;
    
    boolean onBeforeStart(final Room p0) throws GameException;
}
