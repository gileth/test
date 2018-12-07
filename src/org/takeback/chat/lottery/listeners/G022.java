// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.lottery.listeners;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Date;
import org.takeback.chat.utils.NumberUtil;
import org.takeback.chat.store.room.LotteryFactory;
import org.takeback.chat.store.user.User;
import org.takeback.chat.lottery.LotteryDetail;
import org.takeback.chat.entity.PubUser;
import org.takeback.chat.lottery.Lottery;
import java.io.Serializable;
import org.takeback.chat.entity.GcRoom;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.entity.Message;
import org.takeback.chat.store.room.Room;
import org.springframework.stereotype.Component;

@Component("G022")
public class G022 extends G02
{
    @Override
    public void onStart(final Room room) throws GameException {
        room.setStatus("1");
        this.sendMasterRed(room);
        MessageUtils.broadcast(room, new Message("gameBegin", null));
    }
    
    @Override
    public boolean onBeforeStart(final Room room) throws GameException {
        final GcRoom gcRoom = this.game02Service.get(GcRoom.class, room.getId());
        if (gcRoom == null) {
            throw new GameException(500, "房间已经解散,停止游戏!");
        }
        if (room.getMaster() < 0) {
            room.setMaster(0);
            return true;
        }
        final long sec = (System.currentTimeMillis() - room.getMasterStamp()) / 1000L;
        if (sec < 60L) {
            throw new GameException(500, "庄主停包<strong style='color:green'>60</strong>秒后可开始申请抢庄！<br>等待<strong style='color:red'>" + (60L - sec) + "</strong>秒后重新申请!");
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
            final Double money = this.game02Service.getDeposit(room) * lottery.getNumber();
            if (user.getMoney() < money) {
                throw new GameException(500, "余额必须大于" + money + "金币才能参与抢庄!");
            }
        }
        else {
            if (uid.equals(lottery.getSender())) {
                return true;
            }
            final Double money = this.game02Service.getDeposit(room);
            if (this.game02Service.moneyDown(uid, money) < 1) {
                throw new GameException(500, "余额不能少于" + money + "金币,请及时充值!");
            }
            System.out.println(user.getId() + " before open ..");
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
                this.game02Service.saveDetail(lottery, lotteryDetail, money, "G022");
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
            double deposit = this.game02Service.getDeposit(room);
            if (lotteryDetail.getUid().equals(lottery.getSender())) {
                deposit *= lottery.getNumber();
            }
            if (!lotteryDetail.getUid().equals(lottery.getSender())) {
                this.game02Service.saveDetail(lottery, lotteryDetail, deposit, "G022");
            }
            final Message notice3 = new Message("TXT_SYS", 0, msg3);
            if (lottery.getSender().equals(lotteryDetail.getUid())) {
                return;
            }
            MessageUtils.broadcast(this.roomStore.get(lottery.getRoomId()), notice3);
            if (!lotteryDetail.getUid().equals(lottery.getSender()) && lottery.getRestNumber() == 1 && !lottery.getDetail().containsKey(lottery.getSender())) {
                lottery.fakeOpen(lottery.getSender());
                final LotteryDetail masterLd = lottery.getDetail().get(lottery.getSender());
                this.game02Service.saveDetail(lottery, masterLd, deposit, "G022");
            }
        }
    }
    
    @Override
    public boolean onBeforeRed(final LotteryFactory.DefaultLotteryBuilder builder) throws GameException {
        final Room room = builder.getRoom();
        final int master = room.getMaster();
        final int sender = builder.getSender();
        final User user = this.userStore.get(sender);
        final GcRoom gcRoom = this.game02Service.get(GcRoom.class, room.getId());
        if (gcRoom == null) {
            throw new GameException(500, "房间已经解散,停止游戏!");
        }
        if (master != sender) {
            throw new GameException(500, "只允许庄主发包!");
        }
        final int maxSize = Integer.valueOf(this.getConifg(room.getId(), "conf_max_size"));
        if (builder.getNumber() > maxSize || builder.getNumber() < 2) {
            throw new GameException(500, "房间限制红包个数:2-" + maxSize + "个!");
        }
        builder.setType("2");
        builder.setDescription("恭喜发财,大吉大利!");
        final int expired = Integer.valueOf(this.getConifg(room.getId(), "conf_expired"));
        builder.setExpiredSeconds(expired);
        final Double deposit = this.game02Service.getDeposit(room);
        final Integer num = builder.getNumber();
        final double min = builder.getNumber() * 0.3;
        if (builder.getMoney().doubleValue() < min) {
            throw new GameException(500, "金币不得低于:" + NumberUtil.round(min));
        }
        final int affected = this.game02Service.moneyDown(sender, deposit * num + builder.getMoney().doubleValue());
        if (affected == 0) {
            throw new GameException(500, "金币必须大于" + deposit * num);
        }
        room.setStatus("1");
        room.setMasterStamp(System.currentTimeMillis());
        return true;
    }
    
    @Override
    public void onRed(final LotteryFactory.DefaultLotteryBuilder builder) throws GameException {
        final Lottery lottery = builder.build();
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
            this.game02Service.dealMaster(lottery);
            return;
        }
        this.lotteryService.setLotteryFinished(lottery.getId());
        final Map<Integer, LotteryDetail> details = lottery.getDetail();
        final LotteryDetail md = details.get(lottery.getSender());
        if (md != null) {
            md.setCreateDate(new Date());
            double deposit = this.game02Service.getDeposit(room);
            if (md.getUid().equals(lottery.getSender())) {
                deposit *= lottery.getNumber();
            }
            this.game02Service.saveDetail(lottery, md, deposit, "G022");
        }
        this.game02Service.dealGame(lottery);
    }
    
    @Override
    public void processExpireEvent(final Lottery lottery) throws GameException {
        if ("2".equals(lottery.getType())) {
            final Room room = this.roomStore.get(lottery.getRoomId());
            room.setStatus("0");
            MessageUtils.broadcast(room, new Message("gameOver", null));
            if (lottery.getSender().equals(0)) {
                this.game02Service.dealMaster(lottery);
                return;
            }
            if (!lottery.getDetail().containsKey(lottery.getSender())) {
                lottery.fakeOpen(lottery.getSender());
                final Map<Integer, LotteryDetail> details = lottery.getDetail();
                final LotteryDetail md = details.get(lottery.getSender());
                if (md != null) {
                    md.setCreateDate(new Date());
                    double deposit = this.game02Service.getDeposit(room);
                    if (md.getUid().equals(lottery.getSender())) {
                        deposit *= lottery.getNumber();
                    }
                    this.game02Service.saveDetail(lottery, md, deposit, "G022");
                }
            }
            this.lotteryService.setLotteryExpired(lottery.getId());
            this.game02Service.dealGame(lottery);
        }
    }
    
    private void sendMasterRed(final Room room) throws GameException {
        final BigDecimal bd = new BigDecimal(1);
        final Integer size = Integer.valueOf(room.getProperties().get("conf_size").toString());
        final Lottery lottery = LotteryFactory.getDefaultBuilder(bd, size).setExpiredSeconds(40).setRoom(room).setType("2").setSender(0).setDescription("抢庄专包").build();
        final Message redMessage = new Message("RED", 0, lottery);
        redMessage.setHeadImg("img/system.png");
        redMessage.setNickName("系统");
        MessageUtils.broadcast(room, redMessage);
    }
    
    @Override
    protected double getMasterDeposit(final Room room) throws GameException {
        final Double conf_money = Double.valueOf(this.getConifg(room.getId(), "conf_money"));
        final Double conf_n10 = Double.valueOf(this.getConifg(room.getId(), "conf_n15"));
        final Integer num = Integer.valueOf(this.getConifg(room.getId(), "conf_size"));
        return conf_money * conf_n10 * num;
    }
}