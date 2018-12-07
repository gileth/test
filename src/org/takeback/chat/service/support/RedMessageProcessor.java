// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.support;

import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;
import org.takeback.chat.lottery.listeners.RoomAndLotteryListener;
import org.takeback.chat.store.room.LotteryFactory;
import org.takeback.util.BeanUtils;
import java.util.Date;
import org.takeback.chat.entity.GcLottery;
import org.takeback.util.converter.ConversionUtils;
import java.math.BigDecimal;
import org.takeback.chat.lottery.listeners.GameException;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.store.user.User;
import org.takeback.chat.store.room.Room;
import org.springframework.web.socket.WebSocketSession;
import org.takeback.chat.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.LotteryService;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component("redMessageProcessor")
public class RedMessageProcessor extends TxtMessageProcessor
{
    public static final Logger LOGGER;
    @Autowired
    private LotteryService lotteryService;
    
    @Override
    public void process(final Message message, final WebSocketSession session, final Room room, final User user) {
        final LotteryFactory.DefaultLotteryBuilder builder = this.generateLottery(message, session, room, user);
        if (builder == null) {
            return;
        }
        final RoomAndLotteryListener listener = room.getRoomAndLotteryListener();
        if (listener != null) {
            try {
                if (!listener.onBeforeRed(builder)) {
                    return;
                }
            }
            catch (GameException e) {
                RedMessageProcessor.LOGGER.error(e.getMessage());
                MessageUtils.sendCMD(session, "alert", e.getMessage());
                return;
            }
        }
        builder.setRoom(room);
        final BigDecimal money = ConversionUtils.convert(user.getMoney(), BigDecimal.class);
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
        this.lotteryService.save(GcLottery.class, gcLottery);
        final Message redMessage = BeanUtils.map(message, Message.class);
        redMessage.setContent(gcLottery);
        MessageUtils.broadcast(room, redMessage);
        if (listener != null) {
            try {
                listener.onRed(builder);
            }
            catch (GameException e2) {
                RedMessageProcessor.LOGGER.error(e2.getMessage());
                MessageUtils.sendCMD(session, "alert", e2.getMessage());
            }
        }
    }
    
    protected LotteryFactory.DefaultLotteryBuilder generateLottery(final Message message, final WebSocketSession session, final Room room, final User user) {
        try {
            final Map<String, Object> body = (Map<String, Object>)message.getContent();
            final BigDecimal money = ConversionUtils.convert(body.get("money"), BigDecimal.class);
            final Integer number = ConversionUtils.convert(body.get("number"), Integer.class);
            final LotteryFactory.DefaultLotteryBuilder builder = LotteryFactory.getDefaultBuilder(money, number).setType("1").setExpiredSeconds(40).setSender(user.getId()).setRoomId(room.getId());
            final String description = (String) body.get("description");
            if (!StringUtils.isEmpty((CharSequence)description)) {
                builder.setDescription(description);
            }
            return builder;
        }
        catch (Exception e) {
            e.printStackTrace();
            MessageUtils.sendCMD(session, "alert", "发送红包失败");
            return null;
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RedMessageProcessor.class);
    }
}
