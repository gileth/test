// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.lottery.listeners;

import java.util.Iterator;
import java.math.BigDecimal;
import org.takeback.chat.store.room.LotteryFactory;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.entity.Message;
import org.takeback.chat.utils.NumberUtil;
import org.takeback.chat.store.user.User;
import org.takeback.chat.lottery.LotteryDetail;
import java.io.Serializable;
import org.takeback.chat.store.room.Room;
import org.takeback.chat.lottery.Lottery;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.Game04Service;
import org.springframework.stereotype.Component;

@Component("G04")
public class G04 extends DefaultGameListener
{
    @Autowired
    Game04Service game04Service;
    
    @Override
    public boolean onBeforeOpen(final Integer uid, final Lottery lottery) throws GameException {
        final Room r = this.roomStore.get(lottery.getRoomId());
        final Double money = this.getDeposit(r, lottery);
        if (lottery.getSender().equals(uid)) {
            return true;
        }
        if (this.lotteryService.moneyDown(uid, money) < 1) {
            throw new GameException(500, "\u91d1\u5e01\u4e0d\u80fd\u5c11\u4e8e" + money + ",\u8bf7\u53ca\u65f6\u5145\u503c!");
        }
        return true;
    }
    
    @Override
    public void onOpen(final Lottery lottery, final LotteryDetail lotteryDetail) throws GameException {
        final User opener = this.userStore.get(lotteryDetail.getUid());
        final Room room = this.roomStore.get(lottery.getRoomId());
        final User sender = this.userStore.get(lottery.getSender());
        final Integer tailPoint = NumberUtil.getTailPoint(lotteryDetail.getCoin());
        final String raidStr = lottery.getDescription().charAt(lottery.getDescription().indexOf("\u96f7") + 1) + "";
        final Integer raidPoint = Integer.valueOf(raidStr);
        String msg = opener.getNickName() + " \u62a2\u4e86\u4f60\u7684\u7ea2\u5305,\u5e78\u8fd0\u8eb2\u8fc7\u4e86\u5730\u96f7!";
        if (lottery.getSender().equals(lotteryDetail.getUid())) {
            this.lotteryService.moneyUp(opener.getId(), lotteryDetail.getCoin().doubleValue());
            this.game04Service.saveDetail(lottery, lotteryDetail, lotteryDetail.getCoin().doubleValue());
        }
        else if (lotteryDetail.getUid().equals(room.getUnDead())) {
            this.lotteryService.moneyUp(opener.getId(), this.getDeposit(room, lottery) + lotteryDetail.getCoin().doubleValue());
            this.game04Service.saveDetail(lottery, lotteryDetail, lotteryDetail.getCoin().doubleValue());
        }
        else if (tailPoint.equals(raidPoint)) {
            msg = "<span style='color:#F89C4C'>" + opener.getNickName() + "</span>\u4e0d\u5e78\u8e29\u4e2d\u4f60\u57cb\u4e0b\u7684\u5730\u96f7!</span>";
            this.lotteryService.moneyUp(sender.getId(), this.getDeposit(room, lottery));
            this.lotteryService.moneyUp(opener.getId(), lotteryDetail.getCoin().doubleValue());
            this.game04Service.saveDetail(lottery, lotteryDetail, lotteryDetail.getCoin().doubleValue() - this.getDeposit(room, lottery));
        }
        else {
            this.lotteryService.moneyUp(opener.getId(), lotteryDetail.getCoin().doubleValue() + this.getDeposit(room, lottery));
            this.game04Service.saveDetail(lottery, lotteryDetail, lotteryDetail.getCoin().doubleValue());
        }
        final Message notice = new Message("TXT_SYS", 0, msg);
        MessageUtils.send(lottery.getSender(), room, notice);
    }
    
    @Override
    public boolean onBeforeRed(final LotteryFactory.DefaultLotteryBuilder builder) throws GameException {
        final Room room = builder.getRoom();
        final int master = room.getMaster();
        final int sender = builder.getSender();
        final User user = this.userStore.get(sender);
        final int maxSize = Integer.valueOf(this.getConifg(room.getId(), "conf_max_size"));
        final int minSize = Integer.valueOf(this.getConifg(room.getId(), "conf_min_size"));
        if (builder.getNumber() > maxSize || builder.getNumber() < minSize) {
            throw new GameException(500, "\u623f\u95f4\u9650\u5236\u7ea2\u5305\u4e2a\u6570:" + minSize + "-" + maxSize + "\u4e2a");
        }
        final Double maxMoney = Double.valueOf(this.getConifg(room.getId(), "conf_max_money"));
        final Double minMoney = Double.valueOf(this.getConifg(room.getId(), "conf_min_money"));
        if (builder.getMoney().doubleValue() > maxMoney || builder.getMoney().doubleValue() < minMoney) {
            throw new GameException(500, "\u7ea2\u5305\u91d1\u5e01\u9650\u5236:" + minMoney + "-" + maxMoney);
        }
        builder.setType("2");
        final int expired = Integer.valueOf(this.getConifg(room.getId(), "conf_expired"));
        final String raid = builder.getDescription();
        Integer raidPoint;
        try {
            raidPoint = Integer.valueOf(raid);
        }
        catch (Exception e) {
            throw new GameException(500, "\u96f7\u70b9\u6570\u4e3a0-9\u7684\u4e2a\u4f4d\u6570\u5b57");
        }
        if (raidPoint > 9 || raidPoint < 0) {
            throw new GameException(500, "\u96f7\u70b9\u6570\u4e3a0-9\u7684\u4e2a\u4f4d\u6570\u5b57");
        }
        double perRate = Double.valueOf(this.getConifg(room.getId(), "conf_rate"));
        if (room.getProperties().containsKey("p_" + builder.getNumber())) {
            perRate = Double.valueOf(room.getProperties().get("p_" + builder.getNumber()).toString());
        }
        final double water = this.getWater(room, builder.getMoney().doubleValue());
        final double cutMoney = builder.getMoney().doubleValue();
        builder.setMoney(NumberUtil.round(builder.getMoney().subtract(new BigDecimal(water))));
        builder.setDescription(builder.getMoney() + "\u91d1/\u96f7" + raidPoint + "/" + perRate + "\u500d");
        builder.setExpiredSeconds(expired);
        final int affected = this.lotteryService.moneyDown(sender, cutMoney);
        if (affected == 0) {
            throw new GameException(500, "\u91d1\u5e01\u4e0d\u8db3!\u4f59\u989d\u5fc5\u987b\u5927\u4e8e" + cutMoney);
        }
        return true;
    }
    
    @Override
    public void onRed(final LotteryFactory.DefaultLotteryBuilder builder) throws GameException {
        final Lottery l = builder.build();
        final Room room = this.roomStore.get(builder.getRoomId());
        room.setStatus("1");
        MessageUtils.broadcast(room, new Message("gameBegin", null));
        if (room.getUnDead() != null && room.getUnDead() > 0) {
            System.out.println("\u514d\u6b7b\u865f\uff1a" + room.getUnDead());
            final BigDecimal b = l.fakeOpen(room.getUnDead());
            this.lotteryService.moneyUp(room.getUnDead(), b.doubleValue());
        }
    }
    
    @Override
    public void onFinished(final Lottery lottery) throws GameException {
        final Room room = this.roomStore.get(lottery.getRoomId());
        room.setStatus("0");
        MessageUtils.broadcast(room, new Message("gameOver", null));
        this.lotteryService.setLotteryFinished(lottery.getId());
        final double water = this.caculateWater(room, lottery.getMoney().doubleValue());
        final User sender = this.userStore.get(lottery.getSender());
        if (!"9".equals(sender.getUserType())) {
            this.game04Service.setWater(room.getId(), sender, water, lottery.getId());
        }
        final Message rmsg = new Message("TXT_SYS", 0, sender.getUserId() + "\u6e38\u620f\u5305\u5df2\u88ab\u62a2\u5149!");
        MessageUtils.broadcast(room, rmsg);
        this.game04Service.setMasterMonitorData(lottery);
        this.showLotteryResult(lottery);
    }
    
    private void showLotteryResult(final Lottery lottery) {
        if (lottery.getDetail().size() == 0) {
            return;
        }
        final StringBuilder msg = new StringBuilder("<table>");
        for (final Integer uid : lottery.getDetail().keySet()) {
            final LotteryDetail ld = lottery.getDetail().get(uid);
            final User player = this.userStore.get(ld.getUid());
            if (uid.equals(lottery.getSender())) {
                msg.append("<tr style='color:#0493b2'><td>\u3016\u53d1\u3017</td><td class='g021-nick-name'>").append(player.getNickName()).append("</td><td>\u514d\u6b7b</td></tr>");
            }
            else {
                final String raidStr = lottery.getDescription().charAt(lottery.getDescription().indexOf("\u96f7") + 1) + "";
                final Integer tailPoint = NumberUtil.getTailPoint(ld.getCoin());
                if (tailPoint.toString().equals(raidStr)) {
                    msg.append("<tr  style='color:#B22222'><td>\u3016\u62a2\u3017</td><td class='g021-nick-name'>").append(player.getNickName()).append("</td><td>\u4e2d\u96f7</td></tr>");
                }
                else {
                    msg.append("<tr><td>\u3016\u62a2\u3017</td><td class='g021-nick-name'>").append(player.getNickName()).append("</td><td>\u65e0\u96f7</td></tr>");
                }
            }
        }
        msg.append("</table>");
        final Room room = this.roomStore.get(lottery.getRoomId());
        final Message rmsg = new Message("TXT_SYS", 0, msg);
        MessageUtils.broadcast(room, rmsg);
    }
    
    public void processExpireEvent(final Lottery lottery) throws GameException {
        final Room room = this.roomStore.get(lottery.getRoomId());
        final User sender = this.userStore.get(lottery.getSender());
        if (lottery.getRestMoney().doubleValue() > 0.0) {
            final double water = this.caculateWater(room, lottery.getMoney().doubleValue());
            final double money = lottery.getRestMoney().doubleValue();
            if (lottery.getRestNumber() != lottery.getNumber() && !"9".equals(sender.getUserType())) {
                this.game04Service.setWater(room.getId(), sender, water, lottery.getId());
            }
            this.lotteryService.moneyUp(lottery.getSender(), money);
        }
        final Message rmsg = new Message("TXT_SYS", 0, sender.getUserId() + " \u7684\u96f7\u5305\u5df2\u8fc7\u671f!");
        MessageUtils.broadcast(room, rmsg);
        this.lotteryService.setLotteryExpired(lottery.getId());
        this.game04Service.setMasterMonitorData(lottery);
        if (lottery.getRestMoney().doubleValue() > 0.0) {
            final Message notcie = new Message("TXT_SYS", 0, "\u4f60\u7684\u96f7\u5305\u5df2\u8fc7\u671f!" + NumberUtil.round(lottery.getRestMoney().doubleValue()) + "\u91d1\u5e01\u5df2\u7ecf\u9000\u5165\u8d26\u6237!");
            MessageUtils.send(lottery.getSender(), room, notcie);
        }
        this.showLotteryResult(lottery);
    }
    
    private Double getDeposit(final Room r, final Lottery lottery) {
        final Double rate = r.getFeeAdd();
        Double types = 1.0;
        if (r.getProperties().containsKey("conf_rate")) {
            types = Double.valueOf(r.getProperties().get("conf_rate").toString());
        }
        final String rateKey = "p_" + lottery.getNumber();
        if (r.getProperties().containsKey(rateKey)) {
            types = Double.valueOf(r.getProperties().get(rateKey).toString());
        }
        return lottery.getMoney().doubleValue() / (1.0 - rate) * types;
    }
    
    private Double getWater(final Room r, final Double money) {
        final Double rate = (r.getFeeAdd() != null) ? r.getFeeAdd() : 0.0;
        if (rate.equals(0)) {
            return 0.0;
        }
        return money * rate;
    }
    
    private Double caculateWater(final Room r, final Double money) {
        final Double rate = (r.getFeeAdd() != null) ? r.getFeeAdd() : 0.0;
        if (rate.equals(0)) {
            return 0.0;
        }
        return money / (1.0 - rate) * rate;
    }
}
