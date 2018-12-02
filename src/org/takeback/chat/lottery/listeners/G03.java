// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.lottery.listeners;

import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.entity.Message;
import org.takeback.util.BeanUtils;
import org.takeback.chat.entity.GcLottery;
import java.math.BigDecimal;
import org.takeback.chat.store.room.Room;
import org.takeback.chat.lottery.Lottery;
import org.takeback.chat.store.room.LotteryFactory;
import org.springframework.stereotype.Component;

@Component("G03")
public class G03 extends DefaultGameListener
{
    @Override
    public boolean onBeforeRed(final LotteryFactory.DefaultLotteryBuilder builder) throws GameException {
        return true;
    }
    
    @Override
    public void onRed(final LotteryFactory.DefaultLotteryBuilder builder) throws GameException {
    }
    
    @Override
    public void onFinished(final Lottery lottery) throws GameException {
    }
    
    public void processExpireEvent(final Lottery lottery) throws GameException {
        super.processExpireEvent(lottery);
    }
    
    @Override
    public boolean onBeforeOpen(final Integer uid, final Lottery lottery) throws GameException {
        return true;
    }
    
    public void processStartEvent(final Room room) throws GameException {
        super.processStartEvent(room);
        final Integer number = Integer.valueOf(this.getConifg(room.getId(), "conf_size"));
        final Lottery lottery = LotteryFactory.getDefaultBuilder(BigDecimal.valueOf(1.0), number).setDescription("\u6e38\u620f\u5f00\u59cb,\u795d\u4f60\u597d\u8fd0!").setSender(0).setType("2").setRoom(room).build();
        final GcLottery gcLottery = BeanUtils.map(lottery, GcLottery.class);
        this.lotteryService.save(GcLottery.class, gcLottery);
        final Message message = new Message("RED", 0, lottery);
        message.setHeadImg("img/avatar.png");
        message.setNickName("\u7cfb\u7edf");
        MessageUtils.broadcast(room, message);
    }
    
    @Override
    public boolean onBeforeStart(final Room room) throws GameException {
        return true;
    }
}
