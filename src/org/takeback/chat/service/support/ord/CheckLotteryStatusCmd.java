// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.support.ord;

import org.takeback.chat.lottery.Lottery;
import org.takeback.chat.utils.MessageUtils;
import org.apache.commons.lang3.StringUtils;
import org.takeback.chat.store.user.User;
import org.takeback.chat.store.room.Room;
import org.springframework.web.socket.WebSocketSession;
import org.takeback.chat.entity.Message;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.LotteryService;
import org.springframework.stereotype.Component;

@Component("checkLotteryStatusCmd")
public class CheckLotteryStatusCmd implements Command
{
    @Autowired
    private LotteryService lotteryService;
    
    @Override
    public void exec(final Map<String, Object> data, final Message message, final WebSocketSession session, final Room room, final User user) {
        final String lotteryId = (String) data.get("lotteryId");
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
        final String status = lottery.getStatus();
        if (status.equals("0") && !lottery.isExpired()) {
            MessageUtils.sendCMD(session, "lotteryCanOpen", lotteryId);
        }
        else if (status.equals("1")) {
            MessageUtils.sendCMD(session, "lotteryFinished", lotteryId);
        }
        else if (status.equals("2") || lottery.isExpired()) {
            MessageUtils.sendCMD(session, "lotteryExpired", lotteryId);
        }
    }
}
