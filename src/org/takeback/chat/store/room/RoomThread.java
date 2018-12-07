// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.store.room;

import java.text.DecimalFormat;
import org.takeback.chat.entity.PcEggLog;
import java.util.HashMap;
import java.util.Calendar;
import org.takeback.chat.store.pcegg.PcEggStore;
import org.takeback.util.BeanUtils;
import java.util.ArrayList;
import java.util.Iterator;
import org.takeback.chat.lottery.listeners.RoomAndLotteryListener;
import java.util.Date;
import org.takeback.chat.entity.GcLottery;
import org.takeback.chat.lottery.listeners.GameException;
import java.math.BigDecimal;
import org.takeback.chat.lottery.Lottery;
import org.takeback.chat.store.user.RobotUser;
import java.util.List;
import org.takeback.chat.entity.GcBetRecord;
import org.takeback.chat.utils.NumberUtil;
import org.takeback.chat.entity.GcMasterRecord;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.entity.Message;
import java.io.Serializable;
import org.takeback.chat.store.user.User;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.math.RandomUtils;
import org.takeback.util.ApplicationContextHolder;
import org.apache.commons.collections.map.HashedMap;
import java.util.Map;
import org.takeback.chat.store.user.UserStore;
import org.takeback.chat.service.support.ord.HandsUpCmd;
import org.takeback.chat.service.GameG05Service;
import org.takeback.chat.service.LotteryService;

public class RoomThread implements Runnable
{
    LotteryService lotteryService;
    GameG05Service gameG05Service;
    HandsUpCmd handsUpCmd;
    UserStore userStore;
    public static final Integer level;
    static Map<String, String[]> eggGroups;
    static String[] group1;
    static String[] group2;
    static String[] group3;
    static String[] group4;
    private Integer currentEggNumber;
    private Map<Integer, Map<String, Integer>> currentBetMap;
    public static String[] talkList;
    private Room room;
    private int masterTimes;
    
    public RoomThread() {
        this.currentEggNumber = 0;
        this.currentBetMap = (Map<Integer, Map<String, Integer>>)new HashedMap();
        this.masterTimes = 0;
        this.lotteryService = (LotteryService)ApplicationContextHolder.getBean("lotteryService");
        this.handsUpCmd = (HandsUpCmd)ApplicationContextHolder.getBean("handsUpCmd");
        this.gameG05Service = (GameG05Service)ApplicationContextHolder.getBean("gameG05Service");
        this.userStore = (UserStore)ApplicationContextHolder.getBean("userStore");
    }
    
    public void setRoom(final Room rm) {
        this.room = rm;
    }
    
    @Override
    public void run() {
        Integer random = 0;
        while (!Thread.interrupted()) {
            try {
                random = RandomUtils.nextInt(4) + 2;
                TimeUnit.SECONDS.sleep(random + RoomThread.level);
                if (random % 2 == 0) {
                    random += RandomUtils.nextInt(3);
                }
                if (this.room.getType().startsWith("G01")) {
                    this.playG01();
                }
                else if (this.room.getType().startsWith("G02")) {
                    random += RandomUtils.nextInt(2);
                    this.playG02();
                }
                else if (this.room.getType().startsWith("G03")) {
                    random = random + RandomUtils.nextInt(15) + 3;
                    this.playG03();
                }
                else if (this.room.getType().startsWith("G04")) {
                    random = random + RandomUtils.nextInt(4) + 1;
                    this.playG04();
                }
                else {
                    if (!this.room.getType().startsWith("G05")) {
                        continue;
                    }
                    random += RandomUtils.nextInt(4);
                    this.playG05();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void playG05() {
        final User manager = this.userStore.get(this.room.getOwner());
        final User master = this.userStore.get(this.room.getMaster());
        if (this.room.getStep() == Room.STEP_FREE) {
            this.room.setStep(Room.STEP_MASTER);
            final Message msg = new Message("TXT_SYS", manager.getId(), "<span style='color:red'>=====开始标庄=====</span>");
            MessageUtils.broadcast(this.room, msg);
            return;
        }
        if (this.room.getStep() == Room.STEP_MASTER) {
            final List<GcMasterRecord> l = this.gameG05Service.getMasterRecrods(this.room.getId());
            if (l.size() > RandomUtils.nextInt(3) + 1) {
                this.room.setStep(Room.STEP_CHECK1);
                return;
            }
            final RobotUser u = this.pickRobotInRoom();
            Double freeze = 0.0;
            if (l.size() == 0) {
                freeze = RandomUtils.nextInt(9) * 1000 + 5000.0;
            }
            else {
                freeze = l.get(0).getFreeze() + RandomUtils.nextInt(9) * 100;
            }
            this.doGetMaster(u, freeze);
        }
        else {
            if (this.room.getStep() == Room.STEP_CHECK1) {
                this.room.setStep(Room.STEP_CHECK2);
                final GcMasterRecord masterRecord = this.gameG05Service.get(GcMasterRecord.class, this.room.getMasterRecordId());
                final Message msg2 = new Message("TXT", manager.getId(), "<span style='color:green'>确认①次</span> <span style='color:#B22222'>" + master.getNickName() + " <span style='color:orange;font-size:20px;font-weight:bold;font-style:italic'>" + masterRecord.getFreeze() + "</span>竞标,有没有更高?</span>");
                msg2.setHeadImg(manager.getHeadImg());
                msg2.setNickName(manager.getNickName());
                MessageUtils.broadcast(this.room, msg2);
                return;
            }
            if (this.room.getStep() == Room.STEP_CHECK2) {
                this.room.setStep(Room.STEP_CHECK3);
                final GcMasterRecord masterRecord = this.gameG05Service.get(GcMasterRecord.class, this.room.getMasterRecordId());
                final Message msg2 = new Message("TXT", manager.getId(), "<span style='color:green'>确认②次</span> <span style='color:#B22222'>" + master.getNickName() + " <span style='color:orange;font-size:20px;font-weight:bold;font-style:italic'>" + masterRecord.getFreeze() + "</span>竞标,有没有更高?</span>");
                msg2.setHeadImg(manager.getHeadImg());
                msg2.setNickName(manager.getNickName());
                MessageUtils.broadcast(this.room, msg2);
            }
            else if (this.room.getStep() == Room.STEP_CHECK3) {
                this.room.setStep(Room.STEP_START_BET);
                final Double maxTypes = Double.valueOf(this.room.getProperties().get("conf_n15").toString());
                final GcMasterRecord gmr = this.gameG05Service.checkMasterRecord(this.room);
                final Double betable = NumberUtil.round(gmr.getFreeze() / maxTypes);
                this.room.setMasterRecordId(gmr.getId());
                this.room.setMaster(gmr.getUid());
                final String txt = "<table style='color:#B22222'><tr><td colspan=2 align='center'><span style='color:red;font-weight:bold;font-size:20px;'>标庄结束!</span></td></tr><tr><td style='font-style:italic;font-weight:bold;font-size:18px;color:green' colspan=2>" + master.getNickName() + " <strong style='color:orange;font-style:italic;font-weight:bold;'>" + gmr.getFreeze() + " 夺标</strong></tr><tr><td >最低下注</td><td style='color:green'><strong>" + 10 + "</strong></td></tr><tr><td>最高下注</td><td style='color:red'><strong>" + 100 + "</strong></td></tr><tr><td>可押注金额</td><td style='color:orange'><strong>" + betable + "</strong></td></tr></table>";
                final Message msg3 = new Message("TXT", manager.getId(), txt);
                msg3.setHeadImg(manager.getHeadImg());
                msg3.setNickName(manager.getNickName());
                MessageUtils.broadcast(this.room, msg3);
                try {
                    TimeUnit.SECONDS.sleep(RandomUtils.nextInt(4) + 2);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final Message msg4 = new Message("TXT_SYS", manager.getId(), "<span style='color:red'>开始下注!</span>");
                MessageUtils.broadcast(this.room, msg4);
            }
            else if (this.room.getStep() == Room.STEP_START_BET) {
                try {
                    TimeUnit.SECONDS.sleep(RandomUtils.nextInt(4));
                }
                catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                final List<GcBetRecord> i = this.gameG05Service.getBetRecords(this.room.getMasterRecordId());
                if (i.size() >= RandomUtils.nextInt(5) + 3) {
                    this.room.setStep(Room.STEP_FINISH_BET);
                    return;
                }
                final RobotUser user = this.pickRobotInRoom();
                if (this.room.getMaster().equals(user.getId())) {
                    return;
                }
                final Double money = RandomUtils.nextInt(40) + 5.0;
                final Double maxTypes2 = Double.valueOf(this.room.getProperties().get("conf_n15").toString());
                final Double deposit = money * maxTypes2;
                final Integer masterRecordId = this.room.getMasterRecordId();
                this.gameG05Service.bet(this.room, user, money, deposit, masterRecordId, "1");
                final String txt2 = "<span style='color:#B22222'>[注] </span><span style='color:orange;font-style:italic;font-weight:bold;font-size:18px;'>" + money + "</span> ";
                final Message msg5 = new Message("TXT", user.getId(), txt2);
                msg5.setHeadImg(user.getHeadImg());
                msg5.setNickName(user.getNickName());
                MessageUtils.broadcast(this.room, msg5);
            }
            else if (this.room.getStep() == Room.STEP_FINISH_BET) {
                if (this.gameG05Service.getBetRecords(this.room.getMasterRecordId()).size() == 0) {
                    final GcMasterRecord gmr2 = this.gameG05Service.get(GcMasterRecord.class, this.room.getMasterRecordId());
                    this.gameG05Service.restoreMasterMoney(this.room.getMasterRecordId());
                    final Message msg2 = new Message("TXT_SYS", manager.getId(), "<span style='color:#B22222'>无人下注," + gmr2.getFreeze() + " 已退还庄主账户!</span>");
                    MessageUtils.broadcast(this.room, msg2);
                    this.room.setStep(Room.STEP_FREE);
                    this.room.setMaster(0);
                    this.room.setStep(Room.STEP_FREE);
                    this.room.setMasterRecordId(0);
                    return;
                }
                this.room.setStep(Room.STEP_SEND_RED);
                final Message msg = new Message("TXT_SYS", manager.getId(), "<span style='color:red'>截止下注!</span>");
                MessageUtils.broadcast(this.room, msg);
            }
            else if (this.room.getStep() == Room.STEP_SEND_RED) {
                this.sendRed(manager);
                this.room.setStep(Room.STEP_PLAYING);
            }
            else if (this.room.getStep() == Room.STEP_PLAYING) {
                final Lottery lottery = this.getOpenableLottery();
                final List<GcBetRecord> j = this.gameG05Service.getBetRecords(this.room.getMasterRecordId());
                Integer masterPlace = RandomUtils.nextInt(j.size() - 1);
                if (masterPlace == 0) {
                    masterPlace = 1;
                }
                for (int k = 0; k < j.size(); ++k) {
                    final GcBetRecord rec = j.get(k);
                    try {
                        TimeUnit.SECONDS.sleep(RandomUtils.nextInt(4) + 2);
                        lottery.open(rec.getUid());
                        if (k == masterPlace) {
                            TimeUnit.SECONDS.sleep(RandomUtils.nextInt(4) + 2);
                            lottery.open(this.room.getMaster());
                        }
                    }
                    catch (Exception e3) {
                        e3.printStackTrace();
                    }
                }
                this.room.setStep(Room.STEP_PLAY_FINISHED);
            }
            else if (this.room.getStep() == Room.STEP_PLAY_FINISHED) {
                final GcMasterRecord gmr2 = this.gameG05Service.get(GcMasterRecord.class, this.room.getMasterRecordId());
                if (gmr2.getFreeze() < 8000.0) {
                    this.gameG05Service.restoreMasterMoney(this.room.getMasterRecordId());
                    final Message msg2 = new Message("TXT_SYS", master.getId(), "<span style='color:#B22222'>" + master.getNickName() + "已下庄,剩余金币已退还账户!</span>");
                    MessageUtils.broadcast(this.room, msg2);
                    this.room.setMaster(0);
                    this.room.setStep(Room.STEP_FREE);
                    this.room.setMasterRecordId(0);
                    return;
                }
                if (RandomUtils.nextInt(10) % 3 == 0) {
                    this.gameG05Service.restoreMasterMoney(this.room.getMasterRecordId());
                    final Message msg2 = new Message("TXT_SYS", master.getId(), "<span style='color:#B22222'>" + master.getNickName() + "已下庄,剩余金币已退还账户!</span>");
                    MessageUtils.broadcast(this.room, msg2);
                    this.room.setMaster(0);
                    this.room.setStep(Room.STEP_FREE);
                    this.room.setMasterRecordId(0);
                    return;
                }
                final Message msg2 = new Message("TXT_SYS", manager.getId(), "<span style='color:red'>开始下注!</span>");
                MessageUtils.broadcast(this.room, msg2);
                this.room.setStep(Room.STEP_START_BET);
                try {
                    TimeUnit.SECONDS.sleep(RandomUtils.nextInt(4) + 2);
                }
                catch (Exception e4) {
                    e4.printStackTrace();
                }
            }
        }
    }
    
    private void sendRed(final User user) {
        final Integer masterRecordId = this.room.getMasterRecordId();
        final Integer number = this.gameG05Service.getBetNumbers(masterRecordId) + 1;
        final BigDecimal money = new BigDecimal(number + 0.5);
        final Integer expiredTime = 60;
        final LotteryFactory.DefaultLotteryBuilder builder = LotteryFactory.getDefaultBuilder(money, number).setType("2").setExpiredSeconds(expiredTime).setSender(user.getId()).setRoomId(this.room.getId());
        final RoomAndLotteryListener listener = this.room.getRoomAndLotteryListener();
        if (listener != null) {
            try {
                if (!listener.onBeforeRed(builder)) {
                    return;
                }
            }
            catch (GameException e) {
                return;
            }
        }
        builder.setRoom(this.room);
        final GcLottery gcLottery = new GcLottery();
        gcLottery.setId(builder.getLotteryId());
        gcLottery.setRoomId(builder.getRoomId());
        gcLottery.setCreateTime(new Date());
        gcLottery.setDescription(builder.getDescription());
        gcLottery.setMoney(builder.getMoney());
        gcLottery.setSender(builder.getSender());
        gcLottery.setNumber(builder.getNumber());
        gcLottery.setStatus("0");
        gcLottery.setType(builder.getType());
        gcLottery.setExpiredSeconds(builder.getExpiredSeconds());
        this.gameG05Service.save(GcLottery.class, gcLottery);
        final Message redMessage = new Message();
        redMessage.setContent(gcLottery);
        redMessage.setSender(user.getId());
        redMessage.setType("RED");
        redMessage.setNickName(user.getNickName());
        redMessage.setHeadImg(user.getHeadImg());
        MessageUtils.broadcast(this.room, redMessage);
        if (listener != null) {
            try {
                listener.onRed(builder);
            }
            catch (GameException e2) {
                return;
            }
        }
        this.room.setStep(Room.STEP_PLAYING);
    }
    
    private void doGetMaster(final User user, final Double freeze) {
        final List<GcMasterRecord> masterRecords = this.gameG05Service.getMasterRecrods(this.room.getId());
        if (masterRecords.size() > 0) {
            final GcMasterRecord maxRecord = masterRecords.get(0);
            final Double maxFreeze = maxRecord.getFreeze();
            if (freeze <= maxFreeze) {
                return;
            }
            for (final GcMasterRecord rec : masterRecords) {
                if (rec.getUid().equals(user.getId())) {
                    this.gameG05Service.addMasterFreeze(user, rec.getId(), freeze - rec.getFreeze());
                    final String txt = "<span style='color:orange;font-style:italic;font-weight:bold;font-size:26px;'>" + freeze + "</span> <span style='color:#B22222;'>参与竞标</span>";
                    final Message msg = new Message("TXT", user.getId(), txt);
                    msg.setHeadImg(user.getHeadImg());
                    msg.setNickName(user.getNickName());
                    MessageUtils.broadcast(this.room, msg);
                    this.room.setMaster(user.getId());
                    this.room.setMasterRecordId(rec.getId());
                    return;
                }
            }
        }
        final GcMasterRecord gmr = this.gameG05Service.newMasterRecord(user, this.room, freeze);
        this.room.setMaster(user.getId());
        this.room.setMasterRecordId(gmr.getId());
        final String txt2 = "<span style='color:orange;font-style:italic;font-weight:bold;font-size:26px;'>" + freeze + "</span> <span style='color:#B22222;'>参与竞标</span>";
        final Message msg2 = new Message("TXT", user.getId(), txt2);
        msg2.setHeadImg(user.getHeadImg());
        msg2.setNickName(user.getNickName());
        MessageUtils.broadcast(this.room, msg2);
    }
    
    private void playG01() {
        final Map<String, Object> p = this.room.getProperties();
        if (this.room.getStatus().equals("0")) {
            final Integer nums = Integer.valueOf(p.get("conf_size").toString());
            if (this.room.getUsers().size() >= nums) {
                this.startGame();
            }
        }
        else {
            final Lottery lottery = this.getOpenableLottery();
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
                    final RobotUser u = this.pickRobot(lottery);
                    if (u == null) {
                        return;
                    }
                    lottery.open(u.getId());
                }
                catch (GameException ex) {}
            }
        }
    }
    
    private RobotUser pickRobot(final Lottery lottery) {
        final Map<Integer, User> users = this.room.getUsers();
        final Iterator<Integer> itr = users.keySet().iterator();
        final List<RobotUser> robotsList = new ArrayList<RobotUser>();
        while (itr.hasNext()) {
            final Integer uid = itr.next();
            final User u = users.get(uid);
            if (u instanceof RobotUser && !lottery.getDetail().containsKey(u.getId())) {
                robotsList.add((RobotUser)u);
            }
        }
        if (robotsList.size() == 0) {
            return null;
        }
        if (robotsList.size() == 1) {
            return robotsList.get(0);
        }
        final Integer r = RandomUtils.nextInt(robotsList.size() - 1);
        return robotsList.get(r);
    }
    
    public RobotUser pickRobotInRoom() {
        final Map<Integer, User> users = this.room.getUsers();
        final Iterator<Integer> itr = users.keySet().iterator();
        final List<RobotUser> robotsList = new ArrayList<RobotUser>();
        while (itr.hasNext()) {
            final Integer uid = itr.next();
            final User u = users.get(uid);
            if (u instanceof RobotUser) {
                robotsList.add((RobotUser)u);
            }
        }
        if (robotsList.size() == 0) {
            return null;
        }
        if (robotsList.size() == 1) {
            return robotsList.get(0);
        }
        final Integer r = RandomUtils.nextInt(robotsList.size() - 1);
        return robotsList.get(r);
    }
    
    private boolean startGame() {
        final RoomAndLotteryListener listener = this.room.getRoomAndLotteryListener();
        try {
            if (listener == null) {
                this.room.start();
                return true;
            }
            if (listener.onBeforeStart(this.room)) {
                this.room.start();
                return true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private void playG02() {
        final Map<String, Object> p = this.room.getProperties();
        final Integer masterId = this.room.getMaster();
        final User master = this.room.getUsers().get(masterId);
        final Integer nums = Integer.valueOf(p.get("conf_size").toString());
        if (master == null) {
            if (this.room.getUsers().size() >= nums) {
                this.startGame();
            }
        }
        else if (master instanceof RobotUser) {
            final Integer r = RandomUtils.nextInt(7) + 8;
            if (this.masterTimes < r) {
                final Lottery lottery = this.getOpenableLottery();
                if (lottery == null) {
                    try {
                        Integer sleep = RandomUtils.nextInt(3) + 2;
                        if (this.masterTimes == 0) {
                            sleep += RandomUtils.nextInt(5);
                        }
                        TimeUnit.SECONDS.sleep(sleep);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final Integer num = Integer.valueOf(this.room.getProperties().get("conf_size").toString());
                    final BigDecimal money = new BigDecimal(1 * num + 0.5);
                    final Lottery lottery2 = LotteryFactory.getDefaultBuilder(money, num).setType("2").setSender(master.getId()).setDescription("恭喜发财,大吉大利!").setRoom(this.room).build();
                    this.room.setMasterStamp(System.currentTimeMillis());
                    final GcLottery gcLottery = BeanUtils.map(lottery2, GcLottery.class);
                    final Double conf_money = Double.valueOf(this.room.getProperties().get("conf_money").toString());
                    final Double conf_n10 = Double.valueOf(this.room.getProperties().get("conf_n15").toString());
                    final Double deposit = conf_money * conf_n10;
                    this.lotteryService.moneyDown(master.getId(), deposit * num + money.doubleValue());
                    this.lotteryService.save(GcLottery.class, gcLottery);
                    try {
                        lottery2.fakeOpen(master.getId());
                    }
                    catch (GameException e2) {
                        e2.printStackTrace();
                        return;
                    }
                    final Message redMessage = new Message("RED", master.getId(), lottery2);
                    redMessage.setHeadImg(master.getHeadImg());
                    redMessage.setNickName(master.getNickName());
                    MessageUtils.broadcast(this.room, redMessage);
                    ++this.masterTimes;
                }
            }
        }
        final Long minus = System.currentTimeMillis() - this.room.getMasterStamp();
        if (minus / 1000L > 65L && this.room.getUsers().size() >= nums && this.startGame()) {
            this.masterTimes = 0;
        }
        final Lottery lottery = this.getOpenableLottery();
        if (lottery != null) {
            final RobotUser u = this.pickRobot(lottery);
            if (u == null) {
                return;
            }
            if (lottery.getSender().equals(0)) {
                try {
                    TimeUnit.SECONDS.sleep(3L);
                }
                catch (Exception ex) {}
            }
            try {
                lottery.open(u.getId());
            }
            catch (GameException ex2) {}
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
        final RobotUser robot = this.pickRobotInRoom();
        if (robot == null) {
            return;
        }
        if (!this.currentEggNumber.equals(log.getId())) {
            this.currentEggNumber = log.getId();
            this.currentBetMap = (Map<Integer, Map<String, Integer>>)new HashedMap();
        }
        final Map<String, Integer> betRecord = this.currentBetMap.get(robot.getId());
        String key = "";
        if (betRecord != null) {
            final String groupId = betRecord.keySet().iterator().next();
            if (betRecord.get(groupId) >= 3) {
                return;
            }
            final String[] keySet = RoomThread.eggGroups.get(groupId);
            key = keySet[RandomUtils.nextInt(keySet.length - 1)];
            betRecord.put(groupId, betRecord.get(groupId) + 1);
        }
        else {
            Integer rd = RandomUtils.nextInt(3);
            if (rd == 0) {
                rd = 1;
            }
            final String groupId2 = "group" + rd;
            final String[] keySet2 = RoomThread.eggGroups.get(groupId2);
            key = keySet2[RandomUtils.nextInt(keySet2.length - 1)];
            final Map<String, Integer> amp = new HashMap<String, Integer>();
            amp.put(groupId2, 1);
            this.currentBetMap.put(robot.getId(), amp);
        }
        final int d = (int)Math.round(Math.random() * 10.0) + 1;
        final Double money = Math.random() * 100.0;
        final Lottery lottery = LotteryFactory.getDefaultBuilder(new BigDecimal(money), 1).setExpiredSeconds(1).setType("2").setTitle(key + " " + NumberUtil.round(d * 5) + "金币").setSender(robot.getId()).setDescription(log.getId() + "期").build();
        try {
            lottery.open(0);
        }
        catch (GameException e) {
            e.printStackTrace();
        }
        final Message redMessage = new Message("RED", robot.getId(), lottery);
        redMessage.setHeadImg(robot.getHeadImg());
        redMessage.setNickName(robot.getNickName());
        MessageUtils.broadcast(this.room, redMessage);
    }
    
    private void playG04() {
        final Map<String, Object> p = this.room.getProperties();
        final Integer size = Integer.valueOf(p.get("conf_max_size").toString());
        final Double money = Double.valueOf(p.get("conf_max_money").toString());
        final Double rate = this.room.getFeeAdd();
        final Lottery lottery = this.getOpenableLottery();
        if (lottery != null) {
            try {
                final RobotUser u = this.pickRobot(lottery);
                if (u != null) {
                    lottery.open(u.getId());
                }
            }
            catch (GameException ex) {}
        }
        else {
            final RobotUser u = this.pickRobotInRoom();
            if (u == null) {
                return;
            }
            double perRate = Double.valueOf(this.room.getProperties().get("conf_rate").toString());
            if (this.room.getProperties().containsKey("p_" + size)) {
                perRate = Double.valueOf(this.room.getProperties().get("p_" + size.toString()).toString());
            }
            final Integer expired = Integer.valueOf(this.room.getProperties().get("conf_expired").toString());
            final Integer raidPoint = RandomUtils.nextInt(9);
            final DecimalFormat df = new DecimalFormat("0.00");
            final Lottery lottery2 = LotteryFactory.getDefaultBuilder(new BigDecimal(money * (1.0 - rate)), size).setType("2").setSender(u.getId()).setExpiredSeconds(expired).setDescription(df.format(money * (1.0 - rate)) + "金/雷" + raidPoint + "/" + perRate + "倍").setRoom(this.room).build();
            this.room.setMasterStamp(System.currentTimeMillis());
            this.room.getProperties().put("raid", raidPoint);
            final GcLottery gcLottery = BeanUtils.map(lottery2, GcLottery.class);
            this.lotteryService.moneyUp(u.getId(), money);
            this.lotteryService.save(GcLottery.class, gcLottery);
            final Message redMessage = new Message("RED", u.getId(), lottery2);
            redMessage.setHeadImg(u.getHeadImg());
            redMessage.setNickName(u.getNickName());
            MessageUtils.broadcast(this.room, redMessage);
        }
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
        level = 0;
        RoomThread.eggGroups = (Map<String, String[]>)new HashedMap();
        RoomThread.group1 = new String[] { "大", "单", "大单", "极大", "红", "黄", "蓝" };
        RoomThread.group2 = new String[] { "大", "双", "大双", "极大", "红", "黄", "蓝" };
        RoomThread.group3 = new String[] { "小", "单", "小单", "极小", "红", "黄", "蓝" };
        RoomThread.group4 = new String[] { "小", "双", "小双", "极小", "红", "黄", "蓝" };
        RoomThread.eggGroups.put("group1", RoomThread.group1);
        RoomThread.eggGroups.put("group2", RoomThread.group2);
        RoomThread.eggGroups.put("group3", RoomThread.group3);
        RoomThread.eggGroups.put("group4", RoomThread.group4);
        RoomThread.talkList = new String[] { "来个豹子...", "卧槽,今天手气好像差了点", "给我回点血吧!!!!!", "我打算收手了,草 再抢剁手了", "呵呵 我看着你剁了", "我今天看到个妹子,大街上就嘘嘘了起来,城里人真会玩", "感觉有点恶搞", "妈的老是我,,", "谁允许我唱首歌啊  这个软件啥都好,就是不能语音", "瓦里格气 我能说的只有圈圈和叉叉了", "我是一个小毛驴我从来都不齐", "好无聊啊........." };
    }
}