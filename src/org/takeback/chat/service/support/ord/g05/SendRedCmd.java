// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.support.ord.g05;

import org.takeback.chat.lottery.listeners.RoomAndLotteryListener;
import org.takeback.util.BeanUtils;
import java.util.Date;
import org.takeback.chat.entity.GcLottery;
import org.takeback.chat.lottery.listeners.GameException;
import org.takeback.chat.store.room.LotteryFactory;
import java.math.BigDecimal;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.store.user.User;
import org.takeback.chat.store.room.Room;
import org.springframework.web.socket.WebSocketSession;
import org.takeback.chat.entity.Message;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.GameG05Service;
import org.springframework.stereotype.Component;
import org.takeback.chat.service.support.ord.Command;

@Component("sendRedCmd")
public class SendRedCmd implements Command
{
    @Autowired
    GameG05Service gameG05Service;
    
    @Override
    public void exec(final Map<String, Object> data, final Message message, final WebSocketSession session, final Room room, final User user) {
        if (!user.getId().equals(room.getOwner())) {
            MessageUtils.sendCMD(session, "alert", "\u975e\u6cd5\u64cd\u4f5c!");
            return;
        }
        if (room.getStep() == Room.STEP_FINISH_BET) {
            final Integer masterRecordId = room.getMasterRecordId();
            if (this.gameG05Service.getBetRecords(masterRecordId).size() == 0) {
                MessageUtils.sendCMD(session, "alert", "\u65e0\u4e0b\u6ce8\u8bb0\u5f55");
                return;
            }
            final Integer number = this.gameG05Service.getBetNumbers(masterRecordId) + 1;
            final BigDecimal money = new BigDecimal(number + 0.5);
            final Integer expiredTime = 60;
            final LotteryFactory.DefaultLotteryBuilder builder = LotteryFactory.getDefaultBuilder(money, number).setType("2").setExpiredSeconds(expiredTime).setSender(user.getId()).setRoomId(room.getId());
            final RoomAndLotteryListener listener = room.getRoomAndLotteryListener();
            if (listener != null) {
                try {
                    if (!listener.onBeforeRed(builder)) {
                        return;
                    }
                }
                catch (GameException e) {
                    MessageUtils.sendCMD(session, "alert", e.getMessage());
                    return;
                }
            }
            builder.setRoom(room);
            final GcLottery gcLottery = new GcLottery();
            gcLottery.setId(builder.getLotteryId());
            gcLottery.setRoomId(builder.getRoomId());
            gcLottery.setCreateTime(new Date());
            gcLottery.setDescription(builder.getDescription());
            gcLottery.setMoney(builder.getMoney());
            gcLottery.setSender(builder.getSender());
            gcLottery.setNumber(builder.getNumber());
            gcLottery.setStatus("0");
            gcLottery.setType(builder.getType());
            gcLottery.setExpiredSeconds(builder.getExpiredSeconds());
            this.gameG05Service.save(GcLottery.class, gcLottery);
            final Message redMessage = BeanUtils.map(message, Message.class);
            redMessage.setContent(gcLottery);
            redMessage.setSender(user.getId());
            redMessage.setNickName(user.getNickName());
            redMessage.setType("RED");
            redMessage.setHeadImg(user.getHeadImg());
            MessageUtils.broadcast(room, redMessage);
            if (listener != null) {
                try {
                    listener.onRed(builder);
                }
                catch (GameException e2) {
                    MessageUtils.sendCMD(session, "alert", e2.getMessage());
                    return;
                }
            }
            room.setStep(Room.STEP_PLAYING);
        }
        else {
            MessageUtils.sendCMD(session, "alert", GameG05Service.suggestNext(room.getStep()));
        }
    }
    
    private String buildMessage(final Double money) {
        return "<span style='color:#B22222'>[\u6ce8] </span><span style='color:orange;font-style:italic;font-weight:bold;font-size:18px;'>" + money + "</span> ";
    }
}
