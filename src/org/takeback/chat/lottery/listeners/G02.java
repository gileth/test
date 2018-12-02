// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.lottery.listeners;

import org.takeback.util.BeanUtils;
import org.takeback.chat.entity.GcLottery;
import java.util.Iterator;
import java.math.BigDecimal;
import java.util.Map;
import org.takeback.chat.store.room.LotteryFactory;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.entity.Message;
import org.takeback.chat.lottery.LotteryDetail;
import org.takeback.chat.store.user.User;
import java.io.Serializable;
import org.takeback.chat.lottery.Lottery;
import org.takeback.chat.store.room.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.Game02Service;
import org.springframework.stereotype.Component;

@Component("G02")
public class G02 extends DefaultGameListener
{
    public static final String GET_MASTER_TEXT = "\u5f00\u59cb\u62a2\u5e84,\u8c01\u5927\u8c01\u5e84!";
    public static final String[] NAMES;
    public static final String[] TUORA;
    public static final String[] GODEN;
    @Autowired
    Game02Service game02Service;
    
    public void processStartEvent(final Room room) throws GameException {
        super.processStartEvent(room);
        this.sendNewMasterRed(room);
        room.setStatus("1");
        this.lotteryService.setRoomStatus(room.getId(), "1");
    }
    
    @Override
    public boolean onBeforeStart(final Room room) throws GameException {
        return true;
    }
    
    @Override
    public boolean onBeforeOpen(final Integer uid, final Lottery lottery) throws GameException {
        if ("1".equals(lottery.getType())) {
            return true;
        }
        if (uid.equals(lottery.getSender())) {
            return true;
        }
        final Room room = this.roomStore.get(lottery.getRoomId());
        final User user = this.userStore.get(uid);
        if (lottery.getSender().equals(0)) {
            final Double money = this.getMasterDeposit(room);
            if (this.lotteryService.moneyDown(uid, money) < 1) {
                throw new GameException(500, "\u4f59\u989d\u5fc5\u987b\u5927\u4e8e" + money + "\u5143\u9910\u80fd\u53c2\u4e0e\u62a2\u5e84!");
            }
        }
        else {
            final Double money = this.getDeposit(room);
            if (this.lotteryService.moneyDown(uid, money) < 1) {
                throw new GameException(500, "\u91d1\u989d\u4e0d\u80fd\u5c11\u4e8e" + money + "\u5143,\u8bf7\u53ca\u65f6\u5145\u503c!");
            }
        }
        return true;
    }
    
    @Override
    public void onOpen(final Lottery lottery, final LotteryDetail lotteryDetail) throws GameException {
        final User opener = this.userStore.get(lotteryDetail.getUid());
        Message notice = null;
        final Room room = this.roomStore.get(lottery.getRoomId());
        if (lottery.getSender().equals(0)) {
            final Double money = this.getMasterDeposit(room);
            this.game02Service.saveDetail(lottery, lotteryDetail, money, "G02");
            final String msg = opener.getNickName() + " \u4e00\u8138\u4e25\u8083,\u53c2\u4e0e\u4e86\u62a2\u5e84";
            notice = new Message("TXT_SYS", 0, msg);
        }
        else {
            final User sender = this.userStore.get(lottery.getSender());
            Double money2 = this.getDeposit(room);
            if (lotteryDetail.getUid().equals(lottery.getSender())) {
                money2 = this.getMasterDeposit(room);
            }
            this.game02Service.saveDetail(lottery, lotteryDetail, money2, "G02");
            if (opener.getId().equals(lottery.getSender())) {
                return;
            }
            final String msg2 = opener.getNickName() + " \u62a2\u8d70\u7ea2\u5305,\u4e0e\u5e84\u5bb6\u5175\u620e\u76f8\u89c1!";
            notice = new Message("TXT_ALERT", 0, msg2);
        }
        MessageUtils.broadcast(this.roomStore.get(lottery.getRoomId()), notice);
    }
    
    @Override
    public boolean onBeforeRed(final LotteryFactory.DefaultLotteryBuilder builder) throws GameException {
        throw new GameException(500, "\u8be5\u623f\u95f4\u4e0d\u5141\u8bb8\u81ea\u7531\u7ea2\u5305!");
    }
    
    @Override
    public void onRed(final LotteryFactory.DefaultLotteryBuilder builder) throws GameException {
    }
    
    @Override
    public void onFinished(final Lottery lottery) throws GameException {
        this.lotteryService.setLotteryFinished(lottery.getId());
        if (lottery.getSender().equals(0)) {
            System.out.println(lottery.getId() + " finished");
            this.dealMaster(lottery);
            return;
        }
        this.game02Service.dealGame(lottery);
    }
    
    public void processExpireEvent(final Lottery lottery) throws GameException {
        super.processExpireEvent(lottery);
        this.lotteryService.setLotteryExpired(lottery.getId());
        if (lottery.getSender().equals(0)) {
            System.out.println(lottery.getId() + " by " + lottery.getSender() + " expired");
            this.dealMaster(lottery);
            return;
        }
        this.game02Service.dealGame(lottery);
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
            final String str = "<span style='color:#B22222'>\u65e0\u4eba\u53c2\u4e0e\u62a2\u5e84,\u6e38\u620f\u7ed3\u675f.";
            final Message msg = new Message("TXT_SYS", 0, str);
            final Room room = this.roomStore.get(lottery.getRoomId());
            room.setStatus("0");
            this.game02Service.gameStop(lottery);
            MessageUtils.broadcast(room, msg);
            return;
        }
        final User master = this.userStore.get(maxMan);
        final String str2 = "<span style='color:#B22222'>" + master.getNickName() + "</span> \u5f00\u59cb\u5750\u5e84\uff01";
        final Message msg2 = new Message("TXT_SYS", 0, str2);
        final Room room2 = this.roomStore.get(lottery.getRoomId());
        room2.setMaster(maxMan);
        room2.setMasterTimes(1);
        MessageUtils.broadcast(room2, msg2);
        this.game02Service.returnMasterLoteryMoney(lottery, this.getMasterDeposit(room2));
        this.sendNewGameRed(master, room2);
    }
    
    private void sendNewGameRed(final User master, final Room room) throws GameException {
        final Double deposit = this.getMasterDeposit(room);
        final Double money = Double.valueOf(this.getConifg(room.getId(), "conf_money_game"));
        System.out.println("~~~~~~~~masterId:" + master.getId());
        if (this.lotteryService.moneyDown(master.getId(), deposit) == 0) {
            this.sendNewMasterRed(room);
            return;
        }
        final Integer num = Integer.valueOf(this.getConifg(room.getId(), "conf_size"));
        final Integer expired = Integer.valueOf(this.getConifg(room.getId(), "conf_expired"));
        final BigDecimal bd = new BigDecimal(money);
        final Lottery lottery = LotteryFactory.getDefaultBuilder(bd, num).setExpiredSeconds(expired).setType("2").setSender(master.getId()).setDescription("\u606d\u559c\u53d1\u8d22,\u725b\u725b\u5e84" + room.getMasterTimes()).setRoom(room).build();
        final GcLottery gcLottery = BeanUtils.map(lottery, GcLottery.class);
        this.lotteryService.save(GcLottery.class, gcLottery);
        room.addLottery(lottery);
        lottery.open(master.getId());
        final Message redMessage = new Message("RED", master.getId(), lottery);
        redMessage.setHeadImg(master.getHeadImg());
        MessageUtils.broadcast(room, redMessage);
    }
    
    private void sendNewMasterRed(final Room room) throws GameException {
        final BigDecimal bd = new BigDecimal(1);
        final Integer number = Integer.valueOf(this.getConifg(room.getId(), "conf_size"));
        final Integer expired = Integer.valueOf(this.getConifg(room.getId(), "conf_expired"));
        final Lottery lottery = LotteryFactory.getDefaultBuilder(bd, number).setExpiredSeconds(expired).setType("2").setSender(0).setDescription("\u5f00\u59cb\u62a2\u5e84,\u8c01\u5927\u8c01\u5e84!").setRoom(room).build();
        final GcLottery gcLottery = BeanUtils.map(lottery, GcLottery.class);
        this.lotteryService.save(GcLottery.class, gcLottery);
        room.addLottery(lottery);
        final Message redMessage = new Message("RED", 0, lottery);
        redMessage.setHeadImg("img/system.png");
        redMessage.setNickName("\u7cfb\u7edf");
        MessageUtils.broadcast(room, redMessage);
    }
    
    protected BigDecimal getInout(final Room room, final int nn) {
        final Map<String, Object> map = room.getProperties();
        final String key = "conf_n" + nn;
        Double types = 1.0;
        if (map.get(key) != null) {
            types = Double.valueOf(map.get(key).toString());
        }
        final Double money = Double.valueOf(map.get("conf_money").toString());
        return new BigDecimal(money * types);
    }
    
    protected double getDeposit(final Room room) throws GameException {
        final Double conf_money = Double.valueOf(this.getConifg(room.getId(), "conf_money"));
        final Double conf_n10 = Double.valueOf(this.getConifg(room.getId(), "conf_n10"));
        return conf_money * conf_n10;
    }
    
    protected double getMasterDeposit(final Room room) throws GameException {
        final Double conf_money = Double.valueOf(this.getConifg(room.getId(), "conf_money"));
        final Double conf_n10 = Double.valueOf(this.getConifg(room.getId(), "conf_n10"));
        final Integer num = Integer.valueOf(this.getConifg(room.getId(), "conf_size"));
        return conf_money * conf_n10 * num;
    }
    
    static {
        NAMES = new String[] { "\u725b\u725b", "\u725b\u2460", "\u725b\u2461", "\u725b\u2462", "\u725b\u2463", "\u725b\u2464", "\u725b\u2465", "\u725b\u2466", "\u725b\u2467", "\u725b\u2468", "\u725b\u725b", "\u91d1\u725b", "\u5bf9\u5b50", "\u987a\u5b50", "\u6ee1\u725b", "\u8c79\u5b50" };
        TUORA = new String[] { "0.12", "1.23", "2.34", "3.45", "4.56", "5.67", "6.78", "7.89" };
        GODEN = new String[] { "0.10", "0.20", "0.30", "0.40", "0.50", "0.60", "0.70", "0.80", "0.90" };
    }
}
