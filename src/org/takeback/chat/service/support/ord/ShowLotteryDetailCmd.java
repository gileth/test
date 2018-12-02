// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.support.ord;

import java.util.Iterator;
import java.util.List;
import org.takeback.chat.lottery.Lottery;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.utils.NumberUtil;
import java.util.Objects;
import com.google.common.collect.Maps;
import java.util.Collections;
import org.joda.time.DateTime;
import org.joda.time.base.AbstractInstant;

import java.util.HashMap;
import org.takeback.chat.lottery.LotteryDetail;
import java.math.BigDecimal;
import java.io.Serializable;
import com.google.common.collect.Lists;
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

@Component("showLotteryDetailCmd")
public class ShowLotteryDetailCmd implements Command
{
    @Autowired
    private UserStore userStore;
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
        final Map<Integer, LotteryDetail> detailMap = lottery.getDetail();
        final List<Map<String, Object>> detail =  Lists.newArrayList();
        final User sender = this.userStore.get(lottery.getSender());
        LotteryDetail masterDetail = null;
        BigDecimal mine = null;
        BigDecimal geted = new BigDecimal(0.0);
        for (final Map.Entry<Integer, LotteryDetail> entry : detailMap.entrySet()) {
            final Integer uid = entry.getKey();
            final LotteryDetail ld = entry.getValue();
            if (uid.equals(user.getId())) {
                mine = ld.getCoin();
            }
            if (ld.getUid().equals(lottery.getSender())) {
                masterDetail = ld;
            }
            geted = geted.add(ld.getCoin());
            final User u = this.userStore.get(uid);
            final Map<String, Object> obj = new HashMap<String, Object>();
            obj.put("uid", u.getId());
            obj.put("nickName", u.getNickName());
            obj.put("headImg", u.getHeadImg());
            obj.put("time", new DateTime((Object)ld.getCreateDate()).toString("HH:mm:ss"));
            if (lottery.isOpen() && "0".equals(lottery.getStatus())) {
                obj.put("coin", 0);
                if (room.getType().startsWith("G01") || room.getType().startsWith("G022")) {
                    if (message.getSender().equals(uid)) {
                        obj.put("coin", ld.getCoin());
                    }
                }
                else if (room.getType().startsWith("G04")) {
                    if (message.getSender().equals(uid)) {
                        obj.put("coin", ld.getCoin());
                    }
                }
                else {
                    obj.put("coin", ld.getCoin());
                }
            }
            else {
                obj.put("coin", ld.getCoin());
            }
            detail.add(obj);
        }
        final String t1;
        final String t2;
        Collections.sort(detail, (o1, o2) ->((String) o1.get("time")).compareTo((String)o2.get("time")));
        final Map<String, Object> result =  Maps.newHashMap();
        result.put("body", detail);
        String sendNickName = null;
        String headImg = null;
        if (0 == lottery.getSender()) {
            sendNickName = "\u7cfb\u7edf";
            headImg = "img/avatar.png";
        }
        else {
            headImg = sender.getHeadImg();
            if (Objects.equals(message.getSender(), sender.getId())) {
                sendNickName = "\u81ea\u5df1";
            }
            else {
                sendNickName = sender.getNickName();
            }
        }
        final Map d = new HashMap();
        d.put("description", lottery.getDescription());
        d.put("sender", lottery.getSender());
        d.put("nickName", sendNickName);
        d.put("headImg", headImg);
        d.put("number", detail.size() + "/" + lottery.getNumber());
        d.put("money", NumberUtil.format(geted) + "/" + NumberUtil.format(lottery.getMoney()));
        if (lottery.isOpen() && "0".equals(lottery.getStatus()) && (room.getType().startsWith("G01") || room.getType().startsWith("G04") || room.getType().startsWith("G022"))) {
            d.put("money", "***/" + NumberUtil.format(lottery.getMoney()));
        }
        if (mine != null) {
            d.put("mine", NumberUtil.format(mine));
        }
        result.put("lottery", d);
        MessageUtils.sendCMD(session, "showLotteryDetail", result);
    }
}
