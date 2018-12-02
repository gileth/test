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
            throw new GameException(500, "\u623f\u95f4\u5df2\u7ecf\u89e3\u6563,\u505c\u6b62\u6e38\u620f!");
        }
        if (room.getMaster() < 0) {
            room.setMaster(0);
            return true;
        }
        final long sec = (System.currentTimeMillis() - room.getMasterStamp()) / 1000L;
        if (sec < 60L) {
            throw new GameException(500, "\u5e84\u4e3b\u505c\u5305<strong style='color:green'>60</strong>\u79d2\u540e\u53ef\u5f00\u59cb\u7533\u8bf7\u62a2\u5e84\uff01<br>\u7b49\u5f85<strong style='color:red'>" + (60L - sec) + "</strong>\u79d2\u540e\u91cd\u65b0\u7533\u8bf7!");
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
            final Double money = this.game02Service.getDeposit(room) * lottery.getNumber();
            if (user.getMoney() < money) {
                throw new GameException(500, "\u4f59\u989d\u5fc5\u987b\u5927\u4e8e" + money + "\u91d1\u5e01\u624d\u80fd\u53c2\u4e0e\u62a2\u5e84!");
            }
        }
        else {
            if (uid.equals(lottery.getSender())) {
                return true;
            }
            final Double money = this.game02Service.getDeposit(room);
            if (this.game02Service.moneyDown(uid, money) < 1) {
                throw new GameException(500, "\u4f59\u989d\u4e0d\u80fd\u5c11\u4e8e" + money + "\u91d1\u5e01,\u8bf7\u53ca\u65f6\u5145\u503c!");
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
            final String msg = "<span style='color:#F89C4C'>" + opener.getNickName() + "</span> \u9886\u53d6\u4e86<span style='color:#F89C4C'>" + sender.getNickName() + "</span>\u53d1\u7684\u798f\u5229\u7ea2\u5305";
            final Message notice = new Message("TXT_SYS", 0, msg);
            MessageUtils.broadcast(this.roomStore.get(lottery.getRoomId()), notice);
        }
        else {
            final User opener = this.userStore.get(lotteryDetail.getUid());
            final Room room = this.roomStore.get(lottery.getRoomId());
            if (lottery.getSender().equals(0)) {
                final Double money = this.getMasterDeposit(room);
                this.game02Service.saveDetail(lottery, lotteryDetail, money, "G022");
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
            throw new GameException(500, "\u623f\u95f4\u5df2\u7ecf\u89e3\u6563,\u505c\u6b62\u6e38\u620f!");
        }
        if (master != sender) {
            throw new GameException(500, "\u53ea\u5141\u8bb8\u5e84\u4e3b\u53d1\u5305!");
        }
        final int maxSize = Integer.valueOf(this.getConifg(room.getId(), "conf_max_size"));
        if (builder.getNumber() > maxSize || builder.getNumber() < 2) {
            throw new GameException(500, "\u623f\u95f4\u9650\u5236\u7ea2\u5305\u4e2a\u6570:2-" + maxSize + "\u4e2a!");
        }
        builder.setType("2");
        builder.setDescription("\u606d\u559c\u53d1\u8d22,\u5927\u5409\u5927\u5229!");
        final int expired = Integer.valueOf(this.getConifg(room.getId(), "conf_expired"));
        builder.setExpiredSeconds(expired);
        final Double deposit = this.game02Service.getDeposit(room);
        final Integer num = builder.getNumber();
        final double min = builder.getNumber() * 0.3;
        if (builder.getMoney().doubleValue() < min) {
            throw new GameException(500, "\u91d1\u5e01\u4e0d\u5f97\u4f4e\u4e8e:" + NumberUtil.round(min));
        }
        final int affected = this.game02Service.moneyDown(sender, deposit * num + builder.getMoney().doubleValue());
        if (affected == 0) {
            throw new GameException(500, "\u91d1\u5e01\u5fc5\u987b\u5927\u4e8e" + deposit * num);
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
            final String msg = "<span style='color:#F89C4C'>" + sender.getNickName() + "</span> \u7684\u7ea2\u5305\u5df2\u88ab\u9886\u5b8c.";
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
        final Lottery lottery = LotteryFactory.getDefaultBuilder(bd, size).setExpiredSeconds(40).setRoom(room).setType("2").setSender(0).setDescription("\u62a2\u5e84\u4e13\u5305").build();
        final Message redMessage = new Message("RED", 0, lottery);
        redMessage.setHeadImg("img/system.png");
        redMessage.setNickName("\u7cfb\u7edf");
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
