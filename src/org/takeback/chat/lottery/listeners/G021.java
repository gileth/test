// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.lottery.listeners;

import java.util.HashMap;
import org.takeback.chat.utils.NumberUtil;
import java.util.Iterator;
import java.math.BigDecimal;
import java.util.Map;
import org.takeback.chat.store.room.LotteryFactory;
import org.takeback.chat.store.user.User;
import org.takeback.chat.lottery.LotteryDetail;
import org.takeback.chat.entity.PubUser;
import java.io.Serializable;
import org.takeback.chat.lottery.Lottery;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.entity.Message;
import org.takeback.chat.store.room.Room;
import org.springframework.stereotype.Component;

@Component("G021")
public class G021 extends G02
{
    @Override
    public void onStart(final Room room) throws GameException {
        room.setStatus("1");
        this.sendMasterRed(room);
        MessageUtils.broadcast(room, new Message("gameBegin", null));
    }
    
    @Override
    public boolean onBeforeStart(final Room room) throws GameException {
        if (room.getMaster() < 0) {
            room.setMaster(0);
            return true;
        }
        final long sec = (System.currentTimeMillis() - room.getMasterStamp()) / 1000L;
        if (sec < 120L) {
            throw new GameException(500, "\u5e84\u4e3b\u505c\u5305<strong style='color:green'>120</strong>\u79d2\u540e\u53ef\u5f00\u59cb\u7533\u8bf7\u62a2\u5e84\uff01<br>\u7b49\u5f85<strong style='color:red'>" + (120L - sec) + "</strong>\u79d2\u540e\u91cd\u65b0\u7533\u8bf7!");
        }
        if (room.getMaster().equals(0)) {
            throw new GameException(500, "\u62a2\u5e84\u8fdb\u884c\u4e2d\uff0c\u62c6\u62a2\u5e84\u5305\u4e89\u593a\u5e84\u4e3b.");
        }
        return true;
    }
    
    @Override
    public boolean onBeforeOpen(final Integer uid, final Lottery lottery) throws GameException {
        if ("1".equals(lottery.getType())) {
            return true;
        }
        final Room room = this.roomStore.get(lottery.getRoomId());
        final PubUser user = this.lotteryService.get(PubUser.class, uid);
        if (lottery.getSender().equals(0)) {
            final Double money = this.getDeposit(room) * lottery.getNumber();
            if (user.getMoney() < money) {
                throw new GameException(500, "\u4f59\u989d\u5fc5\u987b\u5927\u4e8e" + money + "\u5143\u624d\u80fd\u53c2\u4e0e\u62a2\u5e84!");
            }
        }
        else {
            if (uid.equals(lottery.getSender())) {
                return true;
            }
            final Double money = this.getDeposit(room);
            if (this.lotteryService.moneyDown(uid, money) < 1) {
                throw new GameException(500, "\u91d1\u989d\u4e0d\u80fd\u5c11\u4e8e" + money + "\u5143,\u8bf7\u53ca\u65f6\u5145\u503c!");
            }
        }
        return true;
    }
    
    @Override
    public void onOpen(final Lottery lottery, final LotteryDetail lotteryDetail) throws GameException {
        if ("1".equals(lottery.getType())) {
            this.lotteryService.saveLotteryDetail(lottery, lotteryDetail);
            final User opener = this.userStore.get(lotteryDetail.getUid());
            final User sender = this.userStore.get(lottery.getSender());
            final String msg = "<span style='color:#F89C4C'>" + opener.getNickName() + "</span> \u9886\u53d6\u4e86<span style='color:#F89C4C'>" + sender.getNickName() + "</span>\u53d1\u7684\u798f\u5229\u7ea2\u5305";
            final Message notice = new Message("TXT_SYS", 0, msg);
            MessageUtils.broadcast(this.roomStore.get(lottery.getRoomId()), notice);
        }
        else {
            final User opener = this.userStore.get(lotteryDetail.getUid());
            final Room room = this.roomStore.get(lottery.getRoomId());
            if (lottery.getSender().equals(0)) {
                final Double money = this.getMasterDeposit(room);
                this.game02Service.saveDetail(lottery, lotteryDetail, money, "G02");
                final String msg2 = "<span style='color:#F89C4C'>" + opener.getNickName() + "</span> \u53c2\u4e0e\u62a2\u5e84.";
                final Message notice2 = new Message("TXT_SYS", 0, msg2);
                MessageUtils.broadcast(room, notice2);
                return;
            }
            final User sender2 = this.userStore.get(lottery.getSender());
            final String sendNickName = sender2.getNickName();
            String msg3 = "<span style='color:#F89C4C'>" + opener.getNickName() + "</span> \u62a2\u8d70\u7ea2\u5305,\u4e0e\u5e84\u5bb6\u5175\u620e\u76f8\u89c1!";
            if (lottery.getSender().equals(lotteryDetail.getUid())) {
                msg3 = "<span style='color:#F89C4C'>\u5e84\u5bb6</span>\u62a2\u8d70\u7ea2\u5305,\u5750\u7b49\u6311\u6218.";
            }
            double deposit = this.getDeposit(room);
            if (lotteryDetail.getUid().equals(lottery.getSender())) {
                deposit *= lottery.getNumber();
            }
            this.game02Service.saveDetail(lottery, lotteryDetail, deposit, "G021");
            final Message notice3 = new Message("TXT_SYS", 0, msg3);
            MessageUtils.broadcast(this.roomStore.get(lottery.getRoomId()), notice3);
            if (!lotteryDetail.getUid().equals(lottery.getSender()) && lottery.getRestNumber() == 1 && !lottery.getDetail().containsKey(lottery.getSender())) {
                lottery.open(lottery.getSender());
            }
        }
    }
    
    @Override
    public boolean onBeforeRed(final LotteryFactory.DefaultLotteryBuilder builder) throws GameException {
        final Room room = builder.getRoom();
        final int master = room.getMaster();
        final int sender = builder.getSender();
        final User user = this.userStore.get(sender);
        if (master == sender) {
            builder.setType("2");
            builder.setDescription("\u606d\u559c\u53d1\u8d22,\u5bb3\u6015\u522b\u6765!");
            builder.setExpiredSeconds(40);
            final Double deposit = this.getDeposit(room);
            final Integer num = builder.getNumber();
            if (builder.getMoney().doubleValue() < 0.1 * builder.getNumber()) {
                throw new GameException(500, "\u7ea2\u5305\u91d1\u989d\u5fc5\u987b\u5927\u4e8e" + 0.1 * builder.getNumber());
            }
            final int affected = this.lotteryService.moneyDown(sender, deposit * num);
            if (affected == 0) {
                throw new GameException(500, "\u91d1\u989d\u4e0d\u8db3!\u4f59\u989d\u5fc5\u987b\u5927\u4e8e" + deposit * num);
            }
            room.setStatus("1");
            room.setMasterStamp(System.currentTimeMillis());
        }
        else {
            if (builder.getDescription().contains("\u725b\u725b")) {
                throw new GameException(500, "\u975e\u6cd5\u7684\u5173\u952e\u5b57:\u725b\u725b");
            }
            builder.setType("1");
            final int affected2 = this.lotteryService.moneyDown(sender, builder.getMoney().doubleValue());
            if (affected2 == 0) {
                throw new GameException(500, "\u4f59\u989d\u4e0d\u8db3!");
            }
        }
        builder.build();
        return true;
    }
    
    @Override
    public void onRed(final LotteryFactory.DefaultLotteryBuilder builder) throws GameException {
        if ("2".equals(builder.getType())) {
            final Room room = this.roomStore.get(builder.getRoomId());
            room.setStatus("1");
            MessageUtils.broadcast(room, new Message("gameBegin", null));
        }
    }
    
    @Override
    public void onFinished(final Lottery lottery) throws GameException {
        if ("1".equals(lottery.getType())) {
            final Room room = this.roomStore.get(lottery.getRoomId());
            final User sender = this.userStore.get(lottery.getSender());
            final String msg = "<span style='color:#F89C4C'>" + sender.getNickName() + "</span> \u7684\u7ea2\u5305\u5df2\u88ab\u9886\u5b8c.";
            final Message notice = new Message("TXT_SYS", 0, msg);
            MessageUtils.broadcast(room, notice);
            return;
        }
        final Room room = this.roomStore.get(lottery.getRoomId());
        room.setStatus("0");
        MessageUtils.broadcast(room, new Message("gameOver", null));
        if (lottery.getSender().equals(0)) {
            this.dealMaster(lottery);
            return;
        }
        this.lotteryService.setLotteryFinished(lottery.getId());
        this.dealGame(lottery);
    }
    
    @Override
    public void processExpireEvent(final Lottery lottery) throws GameException {
        if ("2".equals(lottery.getType())) {
            final Room room = this.roomStore.get(lottery.getRoomId());
            room.setStatus("0");
            MessageUtils.broadcast(room, new Message("gameOver", null));
            if (lottery.getSender().equals(0)) {
                this.dealMaster(lottery);
                return;
            }
            if (!lottery.getDetail().containsKey(lottery.getSender())) {
                lottery.fakeOpen(lottery.getSender());
            }
            this.lotteryService.setLotteryExpired(lottery.getId());
            this.dealGame(lottery);
        }
    }
    
    private void openForMaster(final Lottery lottery) {
    }
    
    private void dealMaster(final Lottery lottery) throws GameException {
        final Map<Integer, LotteryDetail> details = lottery.getDetail();
        Integer maxMan = 0;
        BigDecimal maxMoney = null;
        for (final LotteryDetail ld : details.values()) {
            if (maxMan == 0 || maxMoney.compareTo(ld.getCoin()) < 0) {
                maxMan = ld.getUid();
                maxMoney = ld.getCoin();
            }
        }
        if (maxMan.equals(0)) {
            final String str = "<span style='color:#B22222'>\u65e0\u4eba\u53c2\u4e0e\u62a2\u5e84,\u62a2\u5e84\u7ed3\u675f.";
            final Message msg = new Message("TXT_SYS", 0, str);
            final Room room = this.roomStore.get(lottery.getRoomId());
            room.setStatus("0");
            room.setMaster(-1);
            MessageUtils.broadcast(room, msg);
            return;
        }
        final User master = this.userStore.get(maxMan);
        final Room room2 = this.roomStore.get(lottery.getRoomId());
        room2.setMaster(maxMan);
        room2.setMasterTimes(1);
        room2.setMasterStamp(System.currentTimeMillis());
        final String str2 = "<span style='color:#F89C4C'>" + master.getNickName() + "</span> \u5750\u4e0a\u5e84\u4e3b\u5b9d\u5ea7,\u50b2\u89c6\u7fa4\u96c4\uff01";
        final Message msg2 = new Message("TXT_SYS", 0, str2);
        MessageUtils.broadcast(room2, msg2);
        final String str3 = "<span style='color:#B22222'>\u4f60\u5df2\u6210\u4e3a\u5e84\u4e3b,\u53d1\u7ea2\u5305\u5f00\u59cb\u5750\u5e84!</span>";
        final Message msg3 = new Message("TXT_SYS", 0, str3);
        MessageUtils.send(master.getId(), room2, msg3);
    }
    
    private void dealGame(final Lottery lottery) throws GameException {
        final Room room = this.roomStore.get(lottery.getRoomId());
        room.setStatus("0");
        final Integer masterId = lottery.getSender();
        final User master = this.userStore.get(masterId);
        final LotteryDetail masterDetail = lottery.getDetail().get(masterId);
        Integer masterPoint = NumberUtil.getDecimalPartSum(masterDetail.getCoin()) % 10;
        BigDecimal masterInout = new BigDecimal(0.0);
        if (masterPoint == 0) {
            masterPoint = 10;
        }
        BigDecimal water = new BigDecimal(0.0);
        final Map<Integer, LotteryDetail> details = lottery.getDetail();
        final StringBuilder msg = new StringBuilder("<table style='color:#0493b2'>");
        final Map<Integer, Double> addMap = new HashMap<Integer, Double>();
        final BigDecimal deposit = new BigDecimal(this.getDeposit(room));
        final double feePercent = (room.getFeeAdd() == null) ? 0.0 : room.getFeeAdd();
        if (feePercent >= 1.0 || feePercent < 0.0) {
            throw new GameException(500, "\u670d\u52a1\u8d39\u8bbe\u7f6e\u9519\u8bef!");
        }
        for (final LotteryDetail ld : details.values()) {
            if (ld.getUid().equals(masterId)) {
                continue;
            }
            final User player = this.userStore.get(ld.getUid());
            Integer playerPoint = NumberUtil.getDecimalPartSum(ld.getCoin()) % 10;
            if (playerPoint.equals(0)) {
                playerPoint = 10;
            }
            msg.append("<tr><td>\u3016\u95f2\u3017</td><td class='g021-nick-name'>").append(player.getNickName()).append("</td><td>(").append(ld.getCoin()).append(")</td>");
            if (masterPoint > playerPoint) {
                final BigDecimal inout = this.getInout(room, masterPoint);
                if (masterPoint == 10) {
                    water = water.add(inout.multiply(new BigDecimal(feePercent)));
                    masterInout = masterInout.add(inout.multiply(new BigDecimal(1.0 - feePercent)));
                }
                else {
                    masterInout = masterInout.add(inout);
                }
                addMap.put(player.getId(), this.getDeposit(room) - inout.doubleValue());
                msg.append("<td style='color:green;'>").append("\u725b").append(G021.NAMES[playerPoint]).append(" -").append(NumberUtil.format(inout)).append("</td>");
            }
            else if (masterPoint < playerPoint) {
                BigDecimal inout = this.getInout(room, playerPoint);
                masterInout = masterInout.subtract(inout);
                if (playerPoint == 10) {
                    water = water.add(inout.multiply(new BigDecimal(feePercent)));
                    inout = inout.multiply(new BigDecimal(1.0 - feePercent));
                }
                addMap.put(player.getId(), this.getDeposit(room) + inout.doubleValue());
                msg.append("<td style='color:red;'>").append("\u725b").append(G021.NAMES[playerPoint]).append(NumberUtil.format(inout)).append("</td>");
            }
            else if (masterDetail.getCoin().compareTo(ld.getCoin()) >= 0) {
                final BigDecimal inout = this.getInout(room, masterPoint);
                if (masterPoint == 10) {
                    water = water.add(inout.multiply(new BigDecimal(feePercent)));
                    masterInout = masterInout.add(inout.multiply(new BigDecimal(1.0 - feePercent)));
                }
                else {
                    masterInout = masterInout.add(inout);
                }
                addMap.put(player.getId(), this.getDeposit(room) - inout.doubleValue());
                msg.append("<td style='color:green;'>").append("\u725b").append(G021.NAMES[playerPoint]).append(" -").append(NumberUtil.format(inout)).append("</td>");
            }
            else {
                BigDecimal inout = this.getInout(room, playerPoint);
                masterInout = masterInout.subtract(inout);
                if (playerPoint == 10) {
                    water = water.add(inout.multiply(new BigDecimal(feePercent)));
                    inout = inout.multiply(new BigDecimal(1.0 - feePercent));
                }
                addMap.put(player.getId(), this.getDeposit(room) + inout.doubleValue());
                msg.append("<td style='color:red;'>").append("\u725b").append(G021.NAMES[playerPoint]).append("+").append(NumberUtil.format(inout)).append("</td>");
            }
            msg.append("</tr>");
        }
        msg.append("<tr><td  style='color:#B22222'>\u3010\u5e84\u3011</td><td class='g021-nick-name'>").append(master.getNickName()).append("</td><td>(").append(masterDetail.getCoin()).append(")</td>");
        if (masterInout.compareTo(new BigDecimal(0)) > 0) {
            msg.append("<td style='color:red'>").append("\u725b").append(G021.NAMES[masterPoint]).append("+").append(NumberUtil.format(masterInout)).append("</td>");
        }
        else if (masterInout.compareTo(new BigDecimal(0)) < 0) {
            msg.append("<td style='color:green'>").append("\u725b").append(G021.NAMES[masterPoint]).append(" -").append(NumberUtil.format(Math.abs(masterInout.doubleValue()))).append("</td></tr>");
        }
        else {
            msg.append("<td style='color:gray'>").append("\u725b").append(G021.NAMES[masterPoint]).append("¡À\u5e73\u5e84</td></tr>");
        }
        msg.append("</table>");
        masterInout = masterInout.add(new BigDecimal(this.getDeposit(room) * lottery.getNumber()));
        addMap.put(masterId, masterInout.doubleValue());
        final Message rmsg = new Message("TXT_SYS", 0, msg.toString());
        MessageUtils.broadcast(room, rmsg);
    }
    
    private void sendMasterRed(final Room room) throws GameException {
        final BigDecimal bd = new BigDecimal(1);
        final Lottery lottery = LotteryFactory.getDefaultBuilder(bd, 10).setExpiredSeconds(40).setRoom(room).setType("2").setSender(0).setDescription("\u5f00\u59cb\u62a2\u5e84,\u8c01\u5927\u8c01\u5e84!").build();
        final Message redMessage = new Message("RED", 0, lottery);
        redMessage.setHeadImg("img/system.png");
        redMessage.setNickName("\u7cfb\u7edf");
        MessageUtils.broadcast(room, redMessage);
    }
}
