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
            throw new GameException(500, "庄主停包<strong style='color:green'>120</strong>秒后可开始申请抢庄！<br>等待<strong style='color:red'>" + (120L - sec) + "</strong>秒后重新申请!");
        }
        if (room.getMaster().equals(0)) {
            throw new GameException(500, "抢庄进行中，拆抢庄包争夺庄主.");
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
                throw new GameException(500, "余额必须大于" + money + "元才能参与抢庄!");
            }
        }
        else {
            if (uid.equals(lottery.getSender())) {
                return true;
            }
            final Double money = this.getDeposit(room);
            if (this.lotteryService.moneyDown(uid, money) < 1) {
                throw new GameException(500, "金额不能少于" + money + "元,请及时充值!");
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
            final String msg = "<span style='color:#F89C4C'>" + opener.getNickName() + "</span> 领取了<span style='color:#F89C4C'>" + sender.getNickName() + "</span>发的福利红包";
            final Message notice = new Message("TXT_SYS", 0, msg);
            MessageUtils.broadcast(this.roomStore.get(lottery.getRoomId()), notice);
        }
        else {
            final User opener = this.userStore.get(lotteryDetail.getUid());
            final Room room = this.roomStore.get(lottery.getRoomId());
            if (lottery.getSender().equals(0)) {
                final Double money = this.getMasterDeposit(room);
                this.game02Service.saveDetail(lottery, lotteryDetail, money, "G02");
                final String msg2 = "<span style='color:#F89C4C'>" + opener.getNickName() + "</span> 参与抢庄.";
                final Message notice2 = new Message("TXT_SYS", 0, msg2);
                MessageUtils.broadcast(room, notice2);
                return;
            }
            final User sender2 = this.userStore.get(lottery.getSender());
            final String sendNickName = sender2.getNickName();
            String msg3 = "<span style='color:#F89C4C'>" + opener.getNickName() + "</span> 抢走红包,与庄家兵戎相见!";
            if (lottery.getSender().equals(lotteryDetail.getUid())) {
                msg3 = "<span style='color:#F89C4C'>庄家</span>抢走红包,坐等挑战.";
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
            builder.setDescription("恭喜发财,害怕别来!");
            builder.setExpiredSeconds(40);
            final Double deposit = this.getDeposit(room);
            final Integer num = builder.getNumber();
            if (builder.getMoney().doubleValue() < 0.1 * builder.getNumber()) {
                throw new GameException(500, "红包金额必须大于" + 0.1 * builder.getNumber());
            }
            final int affected = this.lotteryService.moneyDown(sender, deposit * num);
            if (affected == 0) {
                throw new GameException(500, "金额不足!余额必须大于" + deposit * num);
            }
            room.setStatus("1");
            room.setMasterStamp(System.currentTimeMillis());
        }
        else {
            if (builder.getDescription().contains("牛牛")) {
                throw new GameException(500, "非法的关键字:牛牛");
            }
            builder.setType("1");
            final int affected2 = this.lotteryService.moneyDown(sender, builder.getMoney().doubleValue());
            if (affected2 == 0) {
                throw new GameException(500, "余额不足!");
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
            final String msg = "<span style='color:#F89C4C'>" + sender.getNickName() + "</span> 的红包已被领完.";
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
            final String str = "<span style='color:#B22222'>无人参与抢庄,抢庄结束.";
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
        final String str2 = "<span style='color:#F89C4C'>" + master.getNickName() + "</span> 坐上庄主宝座,傲视群雄！";
        final Message msg2 = new Message("TXT_SYS", 0, str2);
        MessageUtils.broadcast(room2, msg2);
        final String str3 = "<span style='color:#B22222'>你已成为庄主,发红包开始坐庄!</span>";
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
            throw new GameException(500, "服务费设置错误!");
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
            msg.append("<tr><td>〖闲〗</td><td class='g021-nick-name'>").append(player.getNickName()).append("</td><td>(").append(ld.getCoin()).append(")</td>");
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
                msg.append("<td style='color:green;'>").append("牛").append(G021.NAMES[playerPoint]).append(" -").append(NumberUtil.format(inout)).append("</td>");
            }
            else if (masterPoint < playerPoint) {
                BigDecimal inout = this.getInout(room, playerPoint);
                masterInout = masterInout.subtract(inout);
                if (playerPoint == 10) {
                    water = water.add(inout.multiply(new BigDecimal(feePercent)));
                    inout = inout.multiply(new BigDecimal(1.0 - feePercent));
                }
                addMap.put(player.getId(), this.getDeposit(room) + inout.doubleValue());
                msg.append("<td style='color:red;'>").append("牛").append(G021.NAMES[playerPoint]).append(NumberUtil.format(inout)).append("</td>");
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
                msg.append("<td style='color:green;'>").append("牛").append(G021.NAMES[playerPoint]).append(" -").append(NumberUtil.format(inout)).append("</td>");
            }
            else {
                BigDecimal inout = this.getInout(room, playerPoint);
                masterInout = masterInout.subtract(inout);
                if (playerPoint == 10) {
                    water = water.add(inout.multiply(new BigDecimal(feePercent)));
                    inout = inout.multiply(new BigDecimal(1.0 - feePercent));
                }
                addMap.put(player.getId(), this.getDeposit(room) + inout.doubleValue());
                msg.append("<td style='color:red;'>").append("牛").append(G021.NAMES[playerPoint]).append("+").append(NumberUtil.format(inout)).append("</td>");
            }
            msg.append("</tr>");
        }
        msg.append("<tr><td  style='color:#B22222'>【庄】</td><td class='g021-nick-name'>").append(master.getNickName()).append("</td><td>(").append(masterDetail.getCoin()).append(")</td>");
        if (masterInout.compareTo(new BigDecimal(0)) > 0) {
            msg.append("<td style='color:red'>").append("牛").append(G021.NAMES[masterPoint]).append("+").append(NumberUtil.format(masterInout)).append("</td>");
        }
        else if (masterInout.compareTo(new BigDecimal(0)) < 0) {
            msg.append("<td style='color:green'>").append("牛").append(G021.NAMES[masterPoint]).append(" -").append(NumberUtil.format(Math.abs(masterInout.doubleValue()))).append("</td></tr>");
        }
        else {
            msg.append("<td style='color:gray'>").append("牛").append(G021.NAMES[masterPoint]).append("��平庄</td></tr>");
        }
        msg.append("</table>");
        masterInout = masterInout.add(new BigDecimal(this.getDeposit(room) * lottery.getNumber()));
        addMap.put(masterId, masterInout.doubleValue());
        final Message rmsg = new Message("TXT_SYS", 0, msg.toString());
        MessageUtils.broadcast(room, rmsg);
    }
    
    private void sendMasterRed(final Room room) throws GameException {
        final BigDecimal bd = new BigDecimal(1);
        final Lottery lottery = LotteryFactory.getDefaultBuilder(bd, 10).setExpiredSeconds(40).setRoom(room).setType("2").setSender(0).setDescription("开始抢庄,谁大谁庄!").build();
        final Message redMessage = new Message("RED", 0, lottery);
        redMessage.setHeadImg("img/system.png");
        redMessage.setNickName("系统");
        MessageUtils.broadcast(room, redMessage);
    }
}