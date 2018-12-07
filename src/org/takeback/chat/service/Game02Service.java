// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.takeback.chat.lottery.listeners.G02;
import org.takeback.chat.lottery.listeners.GameException;
import org.takeback.chat.store.user.User;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.entity.Message;
import org.takeback.chat.utils.NumberUtil;
import org.takeback.util.BeanUtils;
import java.util.Date;
import java.math.BigDecimal;
import org.takeback.chat.entity.GcLotteryDetail;
import java.io.Serializable;
import org.takeback.chat.store.room.Room;
import org.takeback.util.exception.CodedBaseRuntimeException;
import org.springframework.transaction.annotation.Transactional;
import java.util.Iterator;
import org.takeback.chat.lottery.LotteryDetail;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import org.takeback.chat.lottery.Lottery;
import org.takeback.chat.store.user.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;
import org.springframework.stereotype.Service;

@Service("game02Service")
public class Game02Service extends LotteryService
{
    @Autowired
    private RoomStore roomStore;
    @Autowired
    private UserStore userStore;
    @Autowired
    private GameMonitor monitor;
    
    @Transactional
    public void returnMasterLoteryMoney(final Lottery lottery, final double deposit) {
        final Map<Integer, LotteryDetail> detail = lottery.getDetail();
        final Iterator itr = detail.keySet().iterator();
        StringBuffer hql = new StringBuffer("update PubUser a set a.money = COALESCE(a.money,0)+:money where a.id in(");
        StringBuilder sb = new StringBuilder();
        while (itr.hasNext()) {
            final Integer uid = (Integer) itr.next();
            sb.append(uid).append(",");
            this.dao.executeUpdate("update GcLotteryDetail a set  a.addback =:addback,desc1='抢庄' where a.lotteryid = :lotteryid and a.uid =:uid",  ImmutableMap.of( "addback",  deposit,  "lotteryid", lottery.getId(),  "uid",  uid));
        }
        if (sb.length() > 0) {
            sb = sb.deleteCharAt(sb.length() - 1);
            hql = hql.append((CharSequence)sb).append(")");
            this.dao.executeUpdate(hql.toString(),  ImmutableMap.of( "money",  deposit));
        }
    }
    
    @Transactional
    public void open(final Lottery lottery, final Integer uid, final Double money) {
        final int effected = this.dao.executeUpdate("update PubUser a set a.money = coalesce(a.money,0) - :money,a.exp=coalesce(exp,0)+:exp where a.id=:uid and a.money>=:money",  ImmutableMap.of( "money",  money, "exp",  money,  "uid",  uid));
        if (effected == 0) {
            throw new CodedBaseRuntimeException("金币不能少于" + money + ",请及时充值!");
        }
        final Room room = this.roomStore.get(lottery.getRoomId());
        final GcLotteryDetail gcLotteryDetail = new GcLotteryDetail();
        gcLotteryDetail.setLotteryid(lottery.getId());
        gcLotteryDetail.setGameType("G011");
        gcLotteryDetail.setRoomId(room.getId());
        gcLotteryDetail.setUid(uid);
        gcLotteryDetail.setDeposit(money);
        gcLotteryDetail.setAddback(0.0);
        gcLotteryDetail.setInoutNum(0.0);
        gcLotteryDetail.setCoin(new BigDecimal(0.0));
        gcLotteryDetail.setCreateDate(new Date());
        gcLotteryDetail.setMasterId(lottery.getSender());
        this.dao.save(GcLotteryDetail.class, gcLotteryDetail);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    @Override
    public int moneyDown(final Integer uid, final Double money) {
        return this.dao.executeUpdate("update PubUser a set a.money = coalesce(a.money,0) - :money,a.exp=coalesce(exp,0)+:exp where a.id=:uid and a.money>=:money", ImmutableMap.of( "money",  money, "exp", money, "uid",  uid));
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void saveDetail(final Lottery lottery, final LotteryDetail detail, final double deposit, final String gameType) {
        final GcLotteryDetail gcLotteryDetail = BeanUtils.map(detail, GcLotteryDetail.class);
        gcLotteryDetail.setLotteryid(lottery.getId());
        gcLotteryDetail.setGameType(gameType);
        gcLotteryDetail.setDeposit(deposit);
        gcLotteryDetail.setDesc1("牛" + NumberUtil.getPoint(detail.getCoin()));
        gcLotteryDetail.setRoomId(lottery.getRoomId());
        gcLotteryDetail.setMasterId(lottery.getSender());
        this.dao.save(GcLotteryDetail.class, gcLotteryDetail);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void gameStop(final Lottery lottery) {
        this.dao.executeUpdate("update GcRoom a set a.status=0 where id=:id", ImmutableMap.of( "id",  lottery.getRoomId()));
        this.dao.executeUpdate("update GcLottery a set a.status=2 where id=:id",  ImmutableMap.of( "id", lottery.getId()));
    }
    
    public void dealMaster(final Lottery lottery) throws GameException {
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
    
    @Transactional(rollbackFor = { Throwable.class })
    public void dealGame(final Lottery lottery) throws GameException {
        final Room room = this.roomStore.get(lottery.getRoomId());
        room.setStatus("0");
        final Integer masterId = lottery.getSender();
        final User master = this.userStore.get(masterId);
        final LotteryDetail masterDetail = this.getMasterDetail(lottery);
        final Integer masterPoint = NumberUtil.getDecimalPartSum4G22(masterDetail.getCoin());
        BigDecimal masterInout = new BigDecimal(0.0);
        final Map<Integer, LotteryDetail> details = lottery.getDetail();
        final StringBuilder msg = new StringBuilder("<table style='color:#0493b2'>");
        BigDecimal water = new BigDecimal(0.0);
        final BigDecimal rate = new BigDecimal(room.getFeeAdd());
        for (final LotteryDetail ld : details.values()) {
            if (ld.getUid().equals(masterId)) {
                continue;
            }
            final User player = this.userStore.get(ld.getUid());
            final Integer playerPoint = NumberUtil.getDecimalPartSum4G22(ld.getCoin());
            final Integer losePoint = Integer.valueOf(this.getConifg(room.getId(), "conf_lose"));
            msg.append("<tr><td>〖闲〗</td><td class='g021-nick-name'>").append(player.getNickName()).append("</td><td>(").append(ld.getCoin()).append(")</td>");
            if (masterPoint > playerPoint || playerPoint <= losePoint) {
                final BigDecimal inout = this.getInout(room, masterPoint);
                this.monitor.setData(lottery.getRoomId(), player.getId(), -inout.doubleValue());
                msg.append("<td style='color:green;'>").append(G02.NAMES[playerPoint]).append(" -").append(NumberUtil.format(inout)).append("</td>");
                masterInout = masterInout.add(inout);
                final Double addBack = this.getDeposit(room) - inout.doubleValue() + ld.getCoin().doubleValue();
                this.dao.executeUpdate("update PubUser a set a.money =a.money + :money,a.exp=coalesce(a.exp,0)+:exp where a.id = :uid ", ImmutableMap.of( "money",  addBack, "exp", Math.abs(inout.doubleValue()),  "uid",  player.getId()));
                this.dao.executeUpdate("update GcLotteryDetail a set  a.addback =:addback,a.inoutNum = :inoutNum where a.lotteryid = :lotteryid and a.uid =:uid",  ImmutableMap.of( "addback", (double)addBack, "inoutNum", (-inout.subtract(ld.getCoin()).doubleValue()),  "lotteryid",  lottery.getId(),  "uid",  player.getId()));
            }
            else if (masterPoint < playerPoint) {
                BigDecimal inout = this.getInout(room, playerPoint);
                masterInout = masterInout.subtract(inout);
                msg.append("<td style='color:red;'>").append(G02.NAMES[playerPoint]).append("+").append(NumberUtil.format(inout)).append("</td>");
                if (!"9".equals(player.getUserType())) {
                    final BigDecimal subWater = inout.multiply(rate);
                    inout = inout.subtract(inout.multiply(rate));
                    final BigDecimal roomWater = new BigDecimal(this.setProxyWater(player, subWater.doubleValue(), room.getId(), "G022", lottery.getId()));
                    water = water.add(roomWater);
                }
                this.monitor.setData(lottery.getRoomId(), player.getId(), inout.doubleValue());
                final Double addBack = this.getDeposit(room) + inout.add(ld.getCoin()).doubleValue();
                this.dao.executeUpdate("update PubUser a set a.money =a.money + :money,a.exp=coalesce(a.exp,0)+:exp where a.id = :uid ",  ImmutableMap.of( "money",  addBack,  "exp",  Math.abs(inout.doubleValue()),  "uid", player.getId()));
                this.dao.executeUpdate("update GcLotteryDetail a set  a.addback =:addback,a.inoutNum = :inoutNum where a.lotteryid = :lotteryid and a.uid =:uid",  ImmutableMap.of( "addback",  (double)addBack,  "inoutNum",  inout.add(ld.getCoin()).doubleValue(),  "lotteryid",  lottery.getId(), "uid",  player.getId()));
            }
            else if (masterDetail.getCoin().compareTo(ld.getCoin()) >= 0) {
                final BigDecimal inout = this.getInout(room, masterPoint);
                this.monitor.setData(lottery.getRoomId(), player.getId(), -inout.doubleValue());
                msg.append("<td style='color:green;'>").append(G02.NAMES[playerPoint]).append(" -").append(NumberUtil.format(inout)).append("</td>");
                masterInout = masterInout.add(inout);
                final Double addBack = this.getDeposit(room) - inout.doubleValue() + ld.getCoin().doubleValue();
                this.dao.executeUpdate("update PubUser a set a.money =a.money + :money,a.exp=coalesce(a.exp,0)+:exp where a.id = :uid ",  ImmutableMap.of( "money",  addBack, "exp", Math.abs(inout.doubleValue()),  "uid", player.getId()));
                this.dao.executeUpdate("update GcLotteryDetail a set  a.addback =:addback,a.inoutNum = :inoutNum where a.lotteryid = :lotteryid and a.uid =:uid", ImmutableMap.of( "addback",  (double)addBack, "inoutNum",  (-inout.subtract(ld.getCoin()).doubleValue()),  "lotteryid",  lottery.getId(),  "uid",  player.getId()));
            }
            else {
                BigDecimal inout = this.getInout(room, playerPoint);
                masterInout = masterInout.subtract(inout);
                msg.append("<td style='color:red;'>").append(G02.NAMES[playerPoint]).append("+").append(NumberUtil.format(inout)).append("</td>");
                if (!"9".equals(player.getUserType())) {
                    final BigDecimal subWater = inout.multiply(rate);
                    inout = inout.subtract(inout.multiply(rate));
                    final BigDecimal roomWater = new BigDecimal(this.setProxyWater(player, subWater.doubleValue(), room.getId(), "G022", lottery.getId()));
                    water = water.add(roomWater);
                }
                this.monitor.setData(lottery.getRoomId(), player.getId(), inout.doubleValue());
                final Double addBack = this.getDeposit(room) + inout.add(ld.getCoin()).doubleValue();
                this.dao.executeUpdate("update PubUser a set a.money =a.money + :money,a.exp=coalesce(a.exp,0)+:exp where a.id = :uid ", ImmutableMap.of( "money",  addBack,  "exp", Math.abs(inout.doubleValue()),  "uid",  player.getId()));
                this.dao.executeUpdate("update GcLotteryDetail a set  a.addback =:addback,a.inoutNum = :inoutNum where a.lotteryid = :lotteryid and a.uid =:uid",  ImmutableMap.of( "addback",  (double)addBack,  "inoutNum", inout.add(ld.getCoin()).doubleValue(),  "lotteryid",  lottery.getId(),  "uid", player.getId()));
            }
            msg.append("</tr>");
        }
        msg.append("<tr><td  style='color:#B22222'>【庄】</td><td class='g021-nick-name'>").append(master.getNickName()).append("</td><td>(").append(masterDetail.getCoin()).append(")</td>");
        if (masterInout.compareTo(new BigDecimal(0)) > 0) {
            msg.append("<td style='color:red'>").append(G02.NAMES[masterPoint]).append("+").append(NumberUtil.format(masterInout)).append("</td>");
        }
        else if (masterInout.compareTo(new BigDecimal(0)) < 0) {
            msg.append("<td style='color:green'>").append(G02.NAMES[masterPoint]).append(" -").append(NumberUtil.format(Math.abs(masterInout.doubleValue()))).append("</td></tr>");
        }
        else {
            msg.append("<td style='color:gray'>").append(G02.NAMES[masterPoint]).append("��平庄</td></tr>");
        }
        if (masterInout.doubleValue() > 0.0 && !"9".equals(master.getUserType())) {
            final BigDecimal subWater2 = masterInout.multiply(rate);
            masterInout = masterInout.subtract(subWater2);
            final BigDecimal roomWater2 = new BigDecimal(this.setProxyWater(master, subWater2.doubleValue(), room.getId(), "G022", lottery.getId()));
            water = water.add(roomWater2);
        }
        masterInout = masterInout.add(masterDetail.getCoin());
        msg.append("</table>");
        masterInout = masterInout.add(lottery.getRestMoney());
        this.monitor.setData(lottery.getRoomId(), master.getId(), masterInout.doubleValue());
        final Double masterAddBack = masterInout.add(new BigDecimal(this.getDeposit(room) * lottery.getNumber())).doubleValue();
        this.dao.executeUpdate("update PubUser a set a.money =a.money + :money,a.exp=coalesce(a.exp,0)+:exp where a.id = :uid ",  ImmutableMap.of( "money", masterAddBack,  "exp",  Math.abs(masterInout.doubleValue()),  "uid",  masterId));
        this.dao.executeUpdate("update GcLotteryDetail a set  a.addback =:addback,a.inoutNum = :inoutNum where a.lotteryid = :lotteryid and a.uid =:uid",  ImmutableMap.of( "addback", (double)masterAddBack,  "inoutNum",  masterInout.doubleValue(), "lotteryid",  lottery.getId(),  "uid",  masterId));
        this.dao.executeUpdate("update GcRoom a set a.sumFee =COALESCE(a.sumFee,0) + :water,sumPack = COALESCE(sumPack,0)+1 where a.id = :roomId ",  ImmutableMap.of( "water", water.doubleValue(),  "roomId",  lottery.getRoomId()));
        final Message rmsg = new Message("TXT_SYS", 0, msg.toString());
        MessageUtils.broadcast(room, rmsg);
    }
    
    private LotteryDetail getMasterDetail(final Lottery lottery) {
        final LotteryDetail lastDetail = null;
        for (final LotteryDetail ld : lottery.getDetail().values()) {
            if (ld.getUid().equals(lottery.getSender())) {
                return ld;
            }
        }
        return null;
    }
    
    public BigDecimal getInout(final Room room, final int nn) {
        final Map<String, Object> map = room.getProperties();
        final String key = "conf_n" + nn;
        Double types = 1.0;
        if (map.get(key) != null) {
            types = Double.valueOf(map.get(key).toString());
        }
        final Double money = Double.valueOf(map.get("conf_money").toString());
        return new BigDecimal(money * types);
    }
    
    public double getDeposit(final Room room) throws GameException {
        final Double conf_money = Double.valueOf(this.getConifg(room.getId(), "conf_money"));
        final Double conf_n10 = Double.valueOf(this.getConifg(room.getId(), "conf_n15"));
        return conf_money * conf_n10;
    }
    
    public String getConifg(final String roomId, final String key) throws GameException {
        final Room room = this.roomStore.get(roomId);
        final Map<String, Object> properties = room.getProperties();
        if (properties.containsKey(key)) {
            return properties.get(key).toString();
        }
        throw new GameException(500, "缺少配置项[" + key + "]");
    }
}