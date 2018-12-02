// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.support.ord;

import java.math.BigDecimal;
import org.takeback.chat.lottery.Lottery;
import org.takeback.chat.lottery.listeners.GameException;
import org.takeback.chat.utils.MessageUtils;
import org.apache.commons.lang3.StringUtils;
import org.takeback.chat.store.user.User;
import org.takeback.chat.store.room.Room;
import org.springframework.web.socket.WebSocketSession;
import org.takeback.chat.entity.Message;
import java.util.Map;
import org.takeback.chat.service.LotteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.user.UserStore;
import org.springframework.stereotype.Component;

@Component("openLotteryCmd")
public class OpenLotteryCmd implements Command
{
    @Autowired
    private UserStore userStore;
    @Autowired
    private LotteryService lotteryService;
    
    @Override
    public void exec(final Map<String, Object> data, final Message message, final WebSocketSession session, final Room room, final User user) {
        final String lotteryId = data.get("lotteryId");
        if (StringUtils.isEmpty((CharSequence)lotteryId)) {
            return;
        }
        Lottery lottery = room.getLottery(lotteryId);
        if (lottery == null) {
            lottery = this.lotteryService.loadLottery(lotteryId);
        }
        if (lottery == null) {
            return;
        }
        if (!lottery.isOpen()) {
            MessageUtils.sendCMD(session, "lotteryFinished", lotteryId);
            return;
        }
        try {
            final BigDecimal result = lottery.open(user.getId());
            if (lottery.getRestNumber() == 0) {
                lottery.finished();
            }
            MessageUtils.sendCMD(session, "lotteryOpenSuccess", lotteryId);
        }
        catch (GameException e) {
            switch (e.getCode()) {
                case 500: {
                    MessageUtils.sendCMD(session, "alert", e.getMessage());
                    break;
                }
                case 511: {
                    MessageUtils.sendCMD(session, "lotteryExpired", lotteryId);
                    break;
                }
                case 512: {
                    MessageUtils.sendCMD(session, "lotteryHasOpened", lotteryId);
                    break;
                }
                default: {
                    MessageUtils.sendCMD(session, "lotteryOpenFailed", lotteryId);
                    break;
                }
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
            MessageUtils.sendCMD(session, "lotteryOpenFailed", lotteryId);
        }
    }
}
