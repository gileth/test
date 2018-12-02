// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.store.user;

import java.util.Iterator;
import java.text.DecimalFormat;
import org.takeback.chat.entity.PcEggLog;
import org.takeback.chat.utils.NumberUtil;
import java.util.Calendar;
import org.takeback.chat.store.pcegg.PcEggStore;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.entity.Message;
import org.takeback.util.BeanUtils;
import org.takeback.chat.entity.GcLottery;
import org.takeback.chat.store.room.LotteryFactory;
import java.math.BigDecimal;
import org.apache.commons.lang.math.RandomUtils;
import java.util.Map;
import org.takeback.chat.lottery.Lottery;
import org.takeback.chat.lottery.listeners.GameException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.takeback.util.ApplicationContextHolder;
import java.util.ArrayList;
import java.util.List;
import org.takeback.chat.store.room.Room;
import org.takeback.chat.service.LotteryService;

public class RobotUser extends User implements Runnable
{
    LotteryService lotteryService;
    public static final Integer level;
    public static String[] talkList;
    private Room room;
    private int masterTimes;
    private Integer eggNumber;
    private List<String> eggBets;
    
    public RobotUser() {
        this.masterTimes = 0;
        this.eggNumber = 0;
        this.eggBets = new ArrayList<String>();
        super.setUserType("0");
        this.lotteryService = (LotteryService)ApplicationContextHolder.getBean("lotteryService");
    }
    
    public void setRoom(final Room rm) {
        this.room = rm;
    }
    
    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                final Long random = Math.round(10.0 * Math.random());
                TimeUnit.SECONDS.sleep(random + RobotUser.level);
                if (this.room.getType().startsWith("G01")) {
                    this.playG01();
                }
                else if (this.room.getType().startsWith("G02")) {
                    this.playG02();
                }
                else if (this.room.getType().startsWith("G03")) {
                    this.playG03();
                }
                else if (this.room.getType().startsWith("G04")) {
                    this.playG04();
                }
                final long r1 = Math.round(10.0 * Math.random());
                TimeUnit.SECONDS.sleep(r1 + RobotUser.level);
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
    
    private void playG01() {
        final Lottery lottery = this.getOpenableLottery();
        final Map<String, Object> p = this.room.getProperties();
        Integer rest = 2;
        try {
            rest = Integer.valueOf(p.get("conf_rest_time").toString());
            ++rest;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (lottery != null) {
            if ((new Date().getTime() - lottery.getCreateTime().getTime()) / 1000L <= rest) {
                return;
            }
            try {
                lottery.open(this.getId());
            }
            catch (GameException ex) {}
        }
    }
    
    private void playG02() {
        final Lottery lottery = this.getOpenableLottery();
        if (lottery != null) {
            try {
                lottery.open(this.getId());
            }
            catch (GameException ex) {}
        }
        else if (this.room.getMaster().equals(this.getId())) {
            final Integer r = RandomUtils.nextInt(5) + 2;
            if (this.masterTimes == r) {
                this.masterTimes = 0;
                this.room.setMaster(0);
            }
            final Integer num = Integer.valueOf(this.room.getProperties().get("conf_size").toString());
            final Lottery lottery2 = LotteryFactory.getDefaultBuilder(new BigDecimal(0.5 * num), num).setType("2").setSender(this.getId()).setDescription("\u606d\u559c\u53d1\u8d22,\u5927\u5409\u5927\u5229!").setRoom(this.room).build();
            this.room.setMasterStamp(System.currentTimeMillis());
            final GcLottery gcLottery = BeanUtils.map(lottery2, GcLottery.class);
            this.lotteryService.save(GcLottery.class, gcLottery);
            try {
                lottery2.fakeOpen(this.getId());
            }
            catch (GameException e) {
                e.printStackTrace();
                return;
            }
            final Message redMessage = new Message("RED", this.getId(), lottery2);
            redMessage.setHeadImg(this.getHeadImg());
            redMessage.setNickName(this.getNickName());
            MessageUtils.broadcast(this.room, redMessage);
        }
    }
    
    private void playG03() {
        final PcEggLog log = PcEggStore.getStore().getLastest();
        final Calendar c = Calendar.getInstance();
        c.setTime(log.getExpireTime());
        c.add(13, -30);
        if (c.getTime().compareTo(new Date()) < 0) {
            return;
        }
        final String[] games = { "\u5927", "\u5c0f", "\u5355", "\u53cc", "\u5927\u5355", "\u5927\u53cc", "\u5c0f\u53cc", "\u5c0f\u5355", "\u6781\u5927", "\u6781\u5c0f", "\u7ea2", "\u9ec4", "\u84dd" };
        final int d = (int)Math.round(Math.random() * 10.0);
        if (d == 0 || d % 4 == 0) {
            return;
        }
        long game = Math.round(d) % games.length;
        if (game == games.length) {
            --game;
        }
        final Double money = Math.random() * 100.0;
        final Lottery lottery = LotteryFactory.getDefaultBuilder(new BigDecimal(money), 1).setExpiredSeconds(1).setType("2").setTitle(games[(int)game] + " " + NumberUtil.round(d * 5) + "\u91d1\u5e01").setSender(this.getId()).setDescription(log.getId() + "\u671f").build();
        try {
            lottery.open(0);
        }
        catch (GameException e) {
            e.printStackTrace();
        }
        final Message redMessage = new Message("RED", this.getId(), lottery);
        redMessage.setHeadImg(this.getHeadImg());
        redMessage.setNickName(this.getNickName());
        MessageUtils.broadcast(this.room, redMessage);
    }
    
    private void playG04() {
        final Map<String, Object> p = this.room.getProperties();
        final Integer size = Integer.valueOf(p.get("conf_max_size").toString());
        final Integer money = Integer.valueOf(p.get("conf_max_money").toString());
        final Double rate = this.room.getFeeAdd();
        final Lottery lottery = this.getOpenableLottery();
        if (lottery != null) {
            try {
                lottery.open(this.getId());
            }
            catch (GameException ex) {}
        }
        else {
            double perRate = Double.valueOf(this.room.getProperties().get("conf_rate").toString());
            if (this.room.getProperties().containsKey("p_" + size)) {
                perRate = Double.valueOf(this.room.getProperties().get("p_" + size.toString()).toString());
            }
            final Integer expired = Integer.valueOf(this.room.getProperties().get("conf_expired").toString());
            final Integer raidPoint = RandomUtils.nextInt(9);
            final DecimalFormat df = new DecimalFormat("0.00");
            final Lottery lottery2 = LotteryFactory.getDefaultBuilder(new BigDecimal(money * (1.0 - rate)), size).setType("2").setSender(this.getId()).setExpiredSeconds(expired).setDescription(df.format(money * (1.0 - rate)) + "\u91d1/\u96f7" + raidPoint + "/" + perRate + "\u500d").setRoom(this.room).build();
            this.room.setMasterStamp(System.currentTimeMillis());
            this.room.getProperties().put("raid", raidPoint);
            final GcLottery gcLottery = BeanUtils.map(lottery2, GcLottery.class);
            this.lotteryService.save(GcLottery.class, gcLottery);
            final Message redMessage = new Message("RED", this.getId(), lottery2);
            redMessage.setHeadImg(this.getHeadImg());
            redMessage.setNickName(this.getNickName());
            MessageUtils.broadcast(this.room, redMessage);
        }
    }
    
    public void talk() {
        long r = Math.round(10.0 * Math.random());
        if (r > 0L) {
            r = r % RobotUser.talkList.length - 1L;
        }
        else if (r < 0L) {
            r = 0L;
        }
        final Message msg = new Message();
        msg.setContent(RobotUser.talkList[(int)r]);
        msg.setHeadImg(this.getHeadImg());
        msg.setSender(this.getId());
        msg.setNickName(this.getUserId());
        msg.setType("TXT");
        MessageUtils.broadcast(this.room, msg);
    }
    
    private Lottery getOpenableLottery() {
        final Map<String, Lottery> lts = (Map<String, Lottery>)this.room.getLotteries().asMap();
        for (final String key : lts.keySet()) {
            final Lottery lottery = lts.get(key);
            if ("0".equals(lottery.getStatus()) && lottery.isOpen() && !lottery.isExpired()) {
                return lottery;
            }
        }
        return null;
    }
    
    public static void main(final String... args) {
        while (true) {
            System.out.println(RandomUtils.nextInt(10));
        }
    }
    
    static {
        level = 1;
        RobotUser.talkList = new String[] { "\u6765\u4e2a\u8c79\u5b50...", "\u5367\u69fd,\u4eca\u5929\u624b\u6c14\u597d\u50cf\u5dee\u4e86\u70b9", "\u7ed9\u6211\u56de\u70b9\u8840\u5427!!!!!", "\u6211\u6253\u7b97\u6536\u624b\u4e86,\u8349 \u518d\u62a2\u5241\u624b\u4e86", "\u5475\u5475 \u6211\u770b\u7740\u4f60\u5241\u4e86", "\u6211\u4eca\u5929\u770b\u5230\u4e2a\u59b9\u5b50,\u5927\u8857\u4e0a\u5c31\u5618\u5618\u4e86\u8d77\u6765,\u57ce\u91cc\u4eba\u771f\u4f1a\u73a9", "\u611f\u89c9\u6709\u70b9\u6076\u641e", "\u5988\u7684\u8001\u662f\u6211,,", "\u8c01\u5141\u8bb8\u6211\u5531\u9996\u6b4c\u554a  \u8fd9\u4e2a\u8f6f\u4ef6\u5565\u90fd\u597d,\u5c31\u662f\u4e0d\u80fd\u8bed\u97f3", "\u74e6\u91cc\u683c\u6c14 \u6211\u80fd\u8bf4\u7684\u53ea\u6709\u5708\u5708\u548c\u53c9\u53c9\u4e86", "\u6211\u662f\u4e00\u4e2a\u5c0f\u6bdb\u9a74\u6211\u4ece\u6765\u90fd\u4e0d\u9f50", "\u597d\u65e0\u804a\u554a........." };
    }
}
