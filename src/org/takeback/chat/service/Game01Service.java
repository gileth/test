// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.takeback.chat.utils.NumberUtil;
import java.util.Iterator;
import org.takeback.util.BeanUtils;
import org.takeback.chat.entity.GcLottery;
import org.takeback.chat.store.room.LotteryFactory;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;
import java.util.Date;
import java.math.BigDecimal;
import org.takeback.chat.entity.GcLotteryDetail;
import org.takeback.util.exception.CodedBaseRuntimeException;
import com.google.common.collect.ImmutableMap;
import org.takeback.chat.lottery.LotteryDetail;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.entity.Message;
import java.io.Serializable;
import org.takeback.chat.store.user.User;
import org.takeback.chat.store.room.Room;
import org.takeback.chat.lottery.Lottery;
import org.takeback.chat.store.user.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;
import org.springframework.stereotype.Service;

@Service("game01Service")
public class Game01Service extends LotteryService
{
    @Autowired
    private RoomStore roomStore;
    @Autowired
    private UserStore userStore;
    @Autowired
    private GameMonitor monitor;
    
    @Transactional(rollbackFor = { Throwable.class })
    public void dealResult(final Lottery lottery, final Room room) {
        final Integer looserId = this.who(lottery, room);
        final User looser = this.userStore.get(looserId);
        this.clear(lottery, room, looserId);
        this.clearRoom(lottery, room);
        final Map<String, Object> p = room.getProperties();
        Integer delay = 0;
        try {
            delay = Integer.valueOf(p.get("conf_rest_time").toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.noticeResult(room, lottery, looserId, delay);
        this.sendNew(room, looser, delay);
        final String msg = new StringBuffer("<span style='color:#B22222'>\u4f60\u624b\u6c14\u7cdf\u7cd5,\u4e0b\u4e2a\u7ea2\u5305\u7531\u4f60\u53d1\u51fa!</span>").toString();
        MessageUtils.send(looserId, room, new Message("TXT_SYS", looserId, msg));
    }
    
    private void noticeResult(final Room room, final Lottery lottery, final Integer looserId, final Integer delay) {
        final Integer luckyId = this.whoLucky(lottery);
        final LotteryDetail luckyDetail = lottery.getDetail().get(luckyId);
        final User looser = this.userStore.get(looserId);
        final User lucky = this.userStore.get(luckyId);
        final LotteryDetail badLuckDetail = lottery.getDetail().get(looserId);
        final String msg = new StringBuffer("\u6700\u4f73\u624b\u6c14 <span style='color:red'>").append(lucky.getNickName()).append(" (").append(luckyDetail.getCoin()).append("\u91d1\u5e01)</span><br>").append("\u624b\u6c14\u6700\u5dee <span style='color:green'>").append(looser.getNickName()).append("(").append(badLuckDetail.getCoin()).append("\u91d1\u5e01)</span>").toString();
        MessageUtils.broadcast(room, new Message("TXT_SYS", 0, msg));
        final String msg2 = new StringBuffer("<span style='color:red'><strong>").append(delay).append("\u79d2\u540e\u53d1\u51fa\u7ea2\u5305,\u51c6\u5907\u5f00\u62a2!</strong></span>").toString();
        MessageUtils.broadcast(room, new Message("TXT_SYS", 0, msg2));
        if (delay > 5) {
            final Integer half = delay / 2;
            final String msg3 = new StringBuffer("<span style='color:red'><strong>").append(delay - half).append("\u79d2\u5012\u8ba1\u65f6,\u8896\u5b50\u62a1\u8d77!</strong></span>").toString();
            MessageUtils.broadcastDelay(room, new Message("TXT_SYS", 0, msg3), half);
        }
    }
    
    @Transactional
    public void open(final Lottery lottery, final User user, final Double money) {
        final int effected = this.dao.executeUpdate("update PubUser a set a.money = coalesce(a.money,0) - :money,a.exp=coalesce(exp,0)+:exp where a.id=:uid and a.money>=:money",  ImmutableMap.of("money", money, "exp", money, "uid", user.getId()));
        if (effected == 0) {
            throw new CodedBaseRuntimeException("\u91d1\u5e01\u4e0d\u80fd\u5c11\u4e8e" + money + ",\u8bf7\u53ca\u65f6\u5145\u503c!");
        }
        final Room room = this.roomStore.get(lottery.getRoomId());
        final GcLotteryDetail gcLotteryDetail = new GcLotteryDetail();
        gcLotteryDetail.setLotteryid(lottery.getId());
        gcLotteryDetail.setGameType("G011");
        gcLotteryDetail.setRoomId(room.getId());
        gcLotteryDetail.setUid(user.getId());
        gcLotteryDetail.setDeposit(money);
        gcLotteryDetail.setAddback(0.0);
        gcLotteryDetail.setInoutNum(0.0);
        gcLotteryDetail.setCoin(new BigDecimal(0.0));
        gcLotteryDetail.setCreateDate(new Date());
        gcLotteryDetail.setMasterId(lottery.getSender());
        this.dao.save(GcLotteryDetail.class, gcLotteryDetail);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void sendNew(final Room room, final User looser, final Integer delay) {
        final Message message = new Message();
        message.setType("RED");
        message.setHeadImg(looser.getHeadImg());
        message.setNickName(looser.getNickName());
        final Map<String, Object> properties = room.getProperties();
        BigDecimal money = new BigDecimal(properties.get("conf_money").toString());
        if (room.getPoolAdd() != null && room.getPoolAdd() > 0.0) {
            money = money.subtract(new BigDecimal(room.getPoolAdd()));
        }
        if (room.getFeeAdd() != null && room.getFeeAdd() > 0.0) {
            money = money.subtract(new BigDecimal(room.getFeeAdd()));
        }
        final Integer number = Integer.valueOf(properties.get("conf_size").toString());
        final String description = StringUtils.isEmpty((CharSequence)properties.get("conf_title").toString()) ? "\u606d\u559c\u53d1\u8d22" : properties.get("conf_title").toString();
        final Integer expired = Integer.valueOf(properties.get("conf_expired").toString());
        final Map<String, Object> content = new HashMap<String, Object>();
        content.put("money", money);
        content.put("number", number);
        content.put("description", description);
        message.setContent(content);
        final Map<String, Object> body = (Map<String, Object>)message.getContent();
        final Lottery red = LotteryFactory.getDefaultBuilder(money, number).setDescription("\u606d\u559c\u53d1\u8d22,\u5927\u5409\u5927\u5229").setSender(looser.getId()).setType("2").setRoom(room).setExpiredSeconds(expired).build();
        final GcLottery gcLottery = BeanUtils.map(red, GcLottery.class);
        this.dao.save(GcLottery.class, gcLottery);
        final Message redMessage = BeanUtils.map(message, Message.class);
        redMessage.setContent(red);
        redMessage.setSender(looser.getId());
        MessageUtils.broadcastDelay(room, redMessage, delay);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void gameLotteryExpired(final Lottery lottery, final Room room) {
        final Map<Integer, LotteryDetail> detail = lottery.getDetail();
        final Iterator itr = detail.keySet().iterator();
        final Map<String, Object> romProps = room.getProperties();
        final BigDecimal redMoney = new BigDecimal(Double.valueOf(romProps.get("conf_money").toString()));
        StringBuffer hql = new StringBuffer("update PubUser a set a.money = COALESCE(a.money,0)+:money where a.id in(");
        StringBuilder sb = new StringBuilder();
        while (itr.hasNext()) {
            final Integer uid = (Integer) itr.next();
            sb.append(uid).append(",");
        }
        if (sb.length() > 0) {
            sb = sb.deleteCharAt(sb.length() - 1);
            hql = hql.append((CharSequence)sb).append(")");
            this.dao.executeUpdate(hql.toString(),  ImmutableMap.of("money", redMoney.doubleValue()));
        }
        if (!lottery.getSender().equals(0)) {
            final BigDecimal retn = redMoney.subtract(new BigDecimal(1));
            this.dao.executeUpdate("update PubUser a set a.money = COALESCE(a.money,0)+:money where a.id=:id", ImmutableMap.of("money", retn.doubleValue(),"id",lottery.getSender()));
        }
        this.dao.executeUpdate("update GcLottery a set a.status = '2' where a.id = :id and a.status = '0'", ImmutableMap.of("id", lottery.getId()));
        this.dao.executeUpdate("update GcRoom a set a.status=0 where id =:id ",ImmutableMap.of("id", lottery.getRoomId()));
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void clear(final Lottery lottery, final Room room, final Integer looserId) {
        final Map<Integer, LotteryDetail> detail = lottery.getDetail();
        final Iterator itr = detail.keySet().iterator();
        final Map<String, Object> romProps = room.getProperties();
        final BigDecimal redMoney = new BigDecimal(Double.valueOf(romProps.get("conf_money").toString()));
        while (itr.hasNext()) {
            final Integer uid = (Integer) itr.next();
            final User user = this.userStore.get(uid);
            final LotteryDetail d = detail.get(uid);
            BigDecimal money = d.getCoin();
            if (!user.getId().equals(looserId)) {
                money = money.add(redMoney);
            }
            final String key = new StringBuffer("b_").append(money.toString()).toString();
            if (romProps.containsKey(key)) {
                final Double value = Double.valueOf(romProps.get(key).toString());
                this.dao.executeUpdate("update GcRoom set sumPool = COALESCE(sumPool,0)-:bonus where id =:roomId ",  ImmutableMap.of( "bonus", value, "roomId", room.getId()));
                money = money.add(new BigDecimal(value));
                final String msg = new StringBuffer("<span style='color:#B22222'>").append(user.getNickName()).append(" \u624b\u6c14\u8d85\u597d,\u83b7\u5f97\u5956\u91d1</span><span style='font-size:16;color:red'>\uffe5").append(value).append("</span>").toString();
                MessageUtils.broadcast(room, new Message("TXT_SYS", uid, msg));
            }
            String desc = "\u5e78\u8fd0";
            Double inout = NumberUtil.round(money.subtract(redMoney).doubleValue());
            if (user.getId().equals(looserId)) {
                inout = NumberUtil.round(money.subtract(redMoney).doubleValue());
                desc = "\u6700\u5dee\u624b\u6c14";
            }
            final Map param = new HashMap();
            param.put("coin", d.getCoin());
            param.put("desc1", desc);
            param.put("addBack", NumberUtil.round(money.doubleValue()));
            param.put("inoutNum", inout);
            param.put("lotteryId", lottery.getId());
            param.put("uid", user.getId());
            final int effected = this.dao.executeUpdate("update GcLotteryDetail set coin =:coin , desc1 =:desc1,addBack =:addBack,inoutNum=:inoutNum where lotteryId=:lotteryId and uid =:uid", param);
            if (effected == 1) {
                this.dao.executeUpdate("update PubUser set money = COALESCE(money,0)+:money ,exp = coalesce(exp,0)+:exp where id=:uid ",  ImmutableMap.of( "money", money.doubleValue(), "exp",  redMoney.doubleValue(),  "uid",  uid));
                this.monitor.setData(room.getId(), user.getId(), inout);
            }
        }
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void clearRoom(final Lottery lottery, final Room room) {
        if (lottery.getSender().equals(0)) {
            return;
        }
        final User sender = this.userStore.get(lottery.getSender());
        if ("9".equals(sender.getUserType())) {
            return;
        }
        Double poolAdd = 0.0;
        Double feeAdd = 0.0;
        if (room.getPoolAdd() != null && room.getPoolAdd() > 0.0) {
            poolAdd = room.getPoolAdd();
        }
        if (room.getFeeAdd() != null && room.getFeeAdd() > 0.0) {
            feeAdd = room.getFeeAdd();
        }
        if (poolAdd == 0.0 && feeAdd == 0.0) {
            return;
        }
        feeAdd = this.setProxyWater(sender, feeAdd, room.getId(), "G011", lottery.getId());
        final String hql = "update GcRoom set sumPool = COALESCE(sumPool,0) + :poolAdd , sumFee = COALESCE(sumFee,0) + :feeAdd ,sumPack = COALESCE(sumPack,0)+1 where id =:roomId";
        this.dao.executeUpdate(hql, ImmutableMap.of( "poolAdd",  poolAdd,  "feeAdd",  feeAdd,  "roomId",  room.getId()));
    }
    
    private Integer whoLucky(final Lottery lottery) {
        final Map<Integer, LotteryDetail> detail = lottery.getDetail();
        final Iterator itr = detail.keySet().iterator();
        Integer luckyId = null;
        BigDecimal num = null;
        while (itr.hasNext()) {
            final Integer uid = (Integer) itr.next();
            final LotteryDetail d = detail.get(uid);
            if (num == null) {
                num = d.getCoin();
                luckyId = uid;
            }
            else {
                if (num.compareTo(d.getCoin()) >= 0) {
                    continue;
                }
                num = d.getCoin();
                luckyId = uid;
            }
        }
        return luckyId;
    }
    
    private Integer who(final Lottery lottery, final Room room) {
        final Map<String, Object> romProps = room.getProperties();
        if (romProps.get("conf_looser") == null) {
            throw new CodedBaseRuntimeException("\u914d\u7f6e\u4e22\u5931!");
        }
        final Integer unDead = room.getUnDead();
        final String rule = romProps.get("conf_looser").toString();
        final Map<Integer, LotteryDetail> detail = lottery.getDetail();
        final Iterator itr = detail.keySet().iterator();
        Integer looserId = null;
        BigDecimal mem = null;
        if ("max".equals(rule)) {
            while (itr.hasNext()) {
                final Integer uid = (Integer) itr.next();
                if (uid.equals(unDead)) {
                    continue;
                }
                final LotteryDetail d = detail.get(uid);
                if (mem == null) {
                    mem = d.getCoin();
                    looserId = uid;
                }
                else {
                    if (mem.compareTo(d.getCoin()) >= 0) {
                        continue;
                    }
                    mem = d.getCoin();
                    looserId = uid;
                }
            }
        }
        else if ("min".equals(rule)) {
            while (itr.hasNext()) {
                final Integer uid = (Integer) itr.next();
                if (uid.equals(unDead)) {
                    continue;
                }
                final LotteryDetail d = detail.get(uid);
                if (mem == null) {
                    mem = d.getCoin();
                    looserId = uid;
                }
                else {
                    if (mem.compareTo(d.getCoin()) <= 0) {
                        continue;
                    }
                    mem = d.getCoin();
                    looserId = uid;
                }
            }
        }
        else if ("tail_max".equals(rule)) {
            while (itr.hasNext()) {
                final Integer uid = (Integer) itr.next();
                if (uid.equals(unDead)) {
                    continue;
                }
                final LotteryDetail d = detail.get(uid);
                if (mem == null) {
                    mem = NumberUtil.getDecimalPart(d.getCoin());
                    looserId = uid;
                }
                else {
                    if (mem.compareTo(d.getCoin()) <= 0) {
                        continue;
                    }
                    mem = NumberUtil.getDecimalPart(d.getCoin());
                    looserId = uid;
                }
            }
        }
        else if ("tail_min".equals(rule)) {
            while (itr.hasNext()) {
                final Integer uid = (Integer) itr.next();
                if (uid.equals(unDead)) {
                    continue;
                }
                final LotteryDetail d = detail.get(uid);
                if (mem == null) {
                    mem = NumberUtil.getDecimalPart(d.getCoin());
                    looserId = uid;
                }
                else {
                    if (mem.compareTo(d.getCoin()) >= 0) {
                        continue;
                    }
                    mem = (mem = NumberUtil.getDecimalPart(d.getCoin()));
                    looserId = uid;
                }
            }
        }
        else if (!"tail_sum_max".equals(rule)) {
            if (!"tail_sum_min".equals(rule)) {
                if (!"sum_max".equals(rule)) {
                    if ("sum_min".equals(rule)) {}
                }
            }
        }
        return looserId;
    }
}
