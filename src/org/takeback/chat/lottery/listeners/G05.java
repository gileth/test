// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.lottery.listeners;

import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.entity.Message;
import org.takeback.chat.store.user.User;
import org.takeback.chat.lottery.LotteryDetail;
import java.io.Serializable;
import org.takeback.chat.store.room.Room;
import org.takeback.chat.lottery.Lottery;
import org.takeback.chat.store.user.UserStore;
import org.takeback.chat.store.room.RoomStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.GameG05Service;
import org.springframework.stereotype.Component;

@Component("G05")
public class G05 extends DefaultGameListener
{
    @Autowired
    GameG05Service gameG05Service;
    @Autowired
    RoomStore roomStore;
    @Autowired
    UserStore userStore;
    
    @Override
    public boolean onBeforeOpen(final Integer uid, final Lottery lottery) throws GameException {
        final Room room = this.roomStore.get(lottery.getRoomId());
        final Integer masterRecordId = room.getMasterRecordId();
        if (this.gameG05Service.checkBet(masterRecordId, uid) || room.getMaster().equals(uid)) {
            return true;
        }
        throw new GameException("本期您未下注,无法抢包!");
    }
    
    @Override
    public void onOpen(final Lottery lottery, final LotteryDetail lotteryDetail) throws GameException {
        String sendNickName = null;
        final User opener = this.userStore.get(lotteryDetail.getUid());
        final User sender = this.userStore.get(lottery.getSender());
        if (opener.getId().equals(sender.getId())) {
            sendNickName = "自己";
        }
        else {
            sendNickName = sender.getNickName();
        }
        if ("1".equals(lottery.getType())) {
            this.lotteryService.saveLotteryDetail(lottery, lotteryDetail);
            final String msg = opener.getNickName() + " 领取了" + sendNickName + "发的红包";
            final Message notice = new Message("TXT_SYS", 0, msg);
            MessageUtils.broadcast(this.roomStore.get(lottery.getRoomId()), notice);
        }
        else {
            this.lotteryService.saveLotteryDetail(lottery, lotteryDetail);
            final String msg = opener.getNickName() + " 领取了红包";
            final Message notice = new Message("TXT_SYS", 0, msg);
            MessageUtils.broadcast(this.roomStore.get(lottery.getRoomId()), notice);
        }
    }
    
    @Override
    public void onFinished(final Lottery lottery) throws GameException {
        if ("1".equals(lottery.getType())) {
            final Room room = this.roomStore.get(lottery.getRoomId());
            final User sender = this.userStore.get(lottery.getSender());
            final String msg = "<span style='color:#F89C4C'>" + sender.getNickName() + "</span> 的红包已被领完.";
            final Message notice = new Message("TXT_SYS", 0, msg);
            MessageUtils.broadcast(room, notice);
            return;
        }
        this.gameG05Service.dealResult(lottery);
        final Room room = this.roomStore.get(lottery.getRoomId());
        room.setStep(Room.STEP_PLAY_FINISHED);
        this.lotteryService.setLotteryFinished(lottery.getId());
    }
    
    public void processExpireEvent(final Lottery lottery) throws GameException {
        if ("2".equals(lottery.getType())) {
            final Room room = this.roomStore.get(lottery.getRoomId());
            room.setStatus("0");
            MessageUtils.broadcast(room, new Message("gameOver", null));
            this.gameG05Service.dealResult(lottery);
            room.setStep(Room.STEP_PLAY_FINISHED);
        }
        this.lotteryService.setLotteryExpired(lottery.getId());
    }
}