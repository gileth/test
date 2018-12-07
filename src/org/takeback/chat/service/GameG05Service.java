// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service;

import org.apache.commons.collections.map.HashedMap;
import java.util.Date;
import org.takeback.util.exception.CodedBaseRuntimeException;
import java.util.Iterator;
import java.util.List;
import org.takeback.chat.entity.PubUser;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.entity.Message;
import org.takeback.chat.entity.GcBetRecord;
import org.takeback.chat.store.user.User;
import org.takeback.chat.utils.NumberUtil;
import org.takeback.chat.lottery.LotteryDetail;
import org.takeback.chat.lottery.Lottery;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import org.takeback.chat.entity.GcMasterRecord;
import org.takeback.chat.store.room.Room;
import org.takeback.chat.store.user.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;
import org.springframework.stereotype.Service;
import org.takeback.service.BaseService;

@Service("gameG05Service")
public class GameG05Service extends BaseService
{
    public static final String GET_MASTER_TEXT = "开始抢庄,谁大谁庄!";
    public static final String[] NAMES;
    public static final String[] TUORA;
    public static final String[] GODEN;
    @Autowired
    RoomStore roomStore;
    @Autowired
    UserStore userStore;
    
    public static String suggestNext(final Integer currentStep) {
        if (Room.STEP_FREE.equals(currentStep)) {
            return "当前状态:" + currentStep + ",建议执行'开始标桩'";
        }
        if (Room.STEP_MASTER.equals(currentStep)) {
            return "当前状态:" + currentStep + ",建议执行'标桩确认'";
        }
        if (Room.STEP_CHECK1.equals(currentStep)) {
            return "当前状态:" + currentStep + ",建议执行'标桩确认'";
        }
        if (Room.STEP_CHECK2.equals(currentStep)) {
            return "当前状态:" + currentStep + ",建议执行'标桩确认'";
        }
        if (Room.STEP_CHECK3.equals(currentStep)) {
            return "当前状态:" + currentStep + ",建议执行'开始下注'";
        }
        if (Room.STEP_START_BET.equals(currentStep)) {
            return "当前状态:" + currentStep + ",建议执行'结束下注'";
        }
        if (Room.STEP_FINISH_BET.equals(currentStep)) {
            return "当前状态:" + currentStep + ",建议执行'发包'";
        }
        if (Room.STEP_PLAY_FINISHED.equals(currentStep)) {
            return "当前状态:" + currentStep + ",建议执行'下庄或开始下注'";
        }
        return "当前命令:" + currentStep;
    }
    
    @Transactional
    public void restoreMasterMoney(final Integer masterRecordId) {
        final GcMasterRecord gmr = this.dao.get(GcMasterRecord.class, masterRecordId);
        final int effected = this.dao.executeUpdate("update GcMasterRecord set status = '3',restBetable=0 where id =:id and status = '1' ",  ImmutableMap.of( "id",  masterRecordId));
        if (effected == 1) {
            this.dao.executeUpdate("update PubUser set money =coalesce(money,0)+:freeze where id=:id",  ImmutableMap.of( "freeze",  gmr.getFreeze(), "id", gmr.getUid()));
        }
    }
    
    @Transactional
    public void dealResult(final Lottery lottery) {
        final Room room = this.roomStore.get(lottery.getRoomId());
        room.setStep(Room.STEP_PLAY_FINISHED);
        final Integer masterRecordId = room.getMasterRecordId();
        final List<GcBetRecord> list = this.getBetRecords(masterRecordId);
        final LotteryDetail masterDetail = lottery.getDetail().get(room.getMaster());
        final Integer masterPoint = NumberUtil.getDecimalPartSum4G22(masterDetail.getCoin());
        Double masterInout = 0.0;
        final User master = this.userStore.get(room.getMaster());
        String msg = "<table style='color:#0493b2'>";
        for (final GcBetRecord r : list) {
            if (r.getUid().equals(room.getMaster())) {
                continue;
            }
            final Integer playerId = r.getUid();
            final User player = this.userStore.get(playerId);
            final LotteryDetail playerDetail = lottery.getDetail().get(r.getUid());
            msg = msg + "<tr><td>〖闲〗</td><td class='g021-nick-name'>" + player.getNickName() + "</td><td>(" + playerDetail.getCoin() + ")</td>";
            final Integer playerPoint = NumberUtil.getDecimalPartSum4G22(playerDetail.getCoin());
            Double playerInout = 0.0;
            if ("2".equals(r.getBetType())) {
                playerInout = r.getMoney();
            }
            else {
                playerInout = this.getInout(room, masterPoint, r.getMoney());
            }
            Double playerAddBack = 0.0;
            if (masterPoint > playerPoint) {
                masterInout += playerInout;
                r.setUserInout(-playerInout);
                playerAddBack = r.getFreeze() - playerInout;
                msg = msg + "<td style='color:green;'>" + GameG05Service.NAMES[playerPoint] + " -" + NumberUtil.format(playerInout) + "</td>";
            }
            else if (masterPoint < playerPoint) {
                masterInout -= playerInout;
                r.setUserInout(playerInout);
                playerAddBack = r.getFreeze() + playerInout;
                msg = msg + "<td style='color:red;'>" + GameG05Service.NAMES[playerPoint] + "+" + NumberUtil.format(playerInout) + "</td>";
            }
            else if (masterDetail.getCoin().compareTo(playerDetail.getCoin()) >= 0) {
                masterInout += playerInout;
                r.setUserInout(-playerInout);
                playerAddBack = r.getFreeze() - playerInout;
                msg = msg + "<td style='color:green;'>" + GameG05Service.NAMES[playerPoint] + " -" + NumberUtil.format(playerInout) + "</td>";
            }
            else {
                masterInout -= playerInout;
                r.setUserInout(playerInout);
                playerAddBack = r.getFreeze() + playerInout;
                msg = msg + "<td style='color:red;'>" + GameG05Service.NAMES[playerPoint] + "+" + NumberUtil.format(playerInout) + "</td>";
            }
            msg += "</tr>";
            r.setAddBack(playerAddBack);
            r.setStatus("1");
            this.dao.save(GcBetRecord.class, r);
            if (playerAddBack > 0.0) {
                this.dao.executeUpdate("update PubUser set money = coalesce(money,0) + :addBack where id = :uid",  ImmutableMap.of( "addBack", playerAddBack,  "uid",  playerId));
            }
            this.dao.executeUpdate("update GcLotteryDetail a set  a.addback =:addback,a.inoutNum = :inoutNum where a.lotteryid = :lotteryid and a.uid =:uid", ImmutableMap.of( "addback",  playerAddBack,  "inoutNum",  playerInout, "lotteryid",  lottery.getId(),  "uid",  playerId));
        }
        msg = msg + "<tr><td  style='color:#B22222'>【庄】</td><td class='g021-nick-name'>" + master.getNickName() + "</td><td>(" + masterDetail.getCoin() + ")</td>";
        if (masterInout > 0.0) {
            msg = msg + "<td style='color:red'>" + GameG05Service.NAMES[masterPoint] + "+" + NumberUtil.format(masterInout) + "</td></tr></table>";
        }
        else if (masterInout < 0.0) {
            msg = msg + "<td style='color:green'>" + GameG05Service.NAMES[masterPoint] + " -" + NumberUtil.format(Math.abs(masterInout)) + "</td></tr></table>";
        }
        else {
            msg = msg + "<td style='color:gray'>" + GameG05Service.NAMES[masterPoint] + "��平庄</td></tr></table>";
        }
        if (masterInout != 0.0) {
            this.dao.executeUpdate("update GcMasterRecord set freeze = coalesce(freeze,0) + :freeze , restBetable = coalesce(restBetable,0) + :freeze where id = :id",  ImmutableMap.of( "freeze",  masterInout,  "id",  masterRecordId));
        }
        this.dao.executeUpdate("update GcLotteryDetail a set  a.inoutNum = :inoutNum where a.lotteryid = :lotteryid and a.uid =:uid", ImmutableMap.of("inoutNum",  masterInout,  "lotteryid", lottery.getId(), "uid", room.getMaster()));
        final Message roundMsg = new Message("TXT_SYS", room.getManager(), msg);
        MessageUtils.broadcastDelay(room, roundMsg, 1L);
        final String masterTxt = this.buildMasterMoney(room);
        final PubUser manager = this.dao.get(PubUser.class, room.getOwner());
        final Message masterMsg = new Message("TXT", manager.getId(), masterTxt);
        masterMsg.setHeadImg(manager.getHeadImg());
        masterMsg.setNickName(manager.getNickName());
        MessageUtils.broadcastDelay(room, masterMsg, 3L);
    }
    
    public String buildMasterMoney(final Room room) {
        final Integer masterRecordId = room.getMasterRecordId();
        final Integer masterId = room.getMaster();
        final GcMasterRecord gmr = this.dao.get(GcMasterRecord.class, masterRecordId);
        final User master = this.userStore.get(masterId);
        final Double maxTypes = Double.valueOf(room.getProperties().get("conf_n15").toString());
        final Double betable = NumberUtil.round(gmr.getRestBetable() / maxTypes);
        final String txt = "<table><tr><td colspan=2 align='center'><span style='color:red;font-weight:bold;font-size:22px;'>庄家金币</span></td></tr><tr><td style='color:#B22222;font-style:italic'>" + master.getNickName() + "</td><td style='color:orange;font-size:18px;font-weight:bold'>" + gmr.getFreeze() + " </td></tr><tr><td style='color:#B22222;'>最低下注</td><td style='color:green;'>" + 20 + "</td></tr><tr><td style='color:#B22222;'>最高下注</td><td style='color:yellow;'>" + 100 + "</td></tr><tr><td style='color:#B22222;'>可押注金额</td><td style='color:red;'>" + betable + "</td></tr></table>";
        return txt;
    }
    
    protected Double getInout(final Room room, final int nn, final Double money) {
        final Map<String, Object> map = room.getProperties();
        final String key = "conf_n" + nn;
        Double types = 1.0;
        if (map.get(key) != null) {
            types = Double.valueOf(map.get(key).toString());
        }
        return money * types;
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void bet(final Room room, final User user, final Double money, final Double freeze, final Integer masterRecordId, final String betType) {
        final int effected = this.dao.executeUpdate("update GcMasterRecord  set restBetable = coalesce(restBetable,0) - :freeze where id=:id and restBetable>=:freeze", ImmutableMap.of( "freeze",  freeze,  "id",  masterRecordId));
        if (effected == 0) {
            throw new CodedBaseRuntimeException("可下金额不足!");
        }
        final int userEffeted = this.dao.executeUpdate("update PubUser a set money = coalesce(money,0) - :money where id=:uid and money>=:money",  ImmutableMap.of( "money", freeze, "uid", user.getId()));
        if (userEffeted == 0) {
            throw new CodedBaseRuntimeException("账户余额不足!");
        }
        final GcBetRecord gbr = new GcBetRecord();
        gbr.setMoney(money);
        gbr.setFreeze(freeze);
        gbr.setBetType(betType);
        gbr.setMasterRecordId(masterRecordId);
        gbr.setUid(user.getId());
        gbr.setStatus("0");
        gbr.setUserId(user.getUserId());
        gbr.setBetTime(new Date());
        this.dao.save(GcBetRecord.class, gbr);
    }
    
    @Transactional
    public List<GcBetRecord> getBetRecords(final Integer masterRecordId) {
        final List l = this.dao.findByHql(" from GcBetRecord where masterRecordId = :masterRecordId and status='0' ",  ImmutableMap.of( "masterRecordId",  masterRecordId));
        return (List<GcBetRecord>)l;
    }
    
    @Transactional
    public Integer getBetNumbers(final Integer masterRecordId) {
        final Map<String, Object> param = (Map<String, Object>)new HashedMap();
        param.put("masterRecordId", masterRecordId);
        final List<GcBetRecord> l = this.dao.findByHql(" from GcBetRecord where masterRecordId = :masterRecordId and status = '0' group by uid", param);
        return l.size();
    }
    
    @Transactional
    public boolean checkBet(final Integer masterRecordId, final Integer uid) {
        final List<Long> l = this.dao.findByHql("select count(*) from GcBetRecord where masterRecordId = :masterRecordId and  uid = :uid ", ImmutableMap.of( "masterRecordId",  masterRecordId,  "uid", uid));
        return l.get(0) > 0L;
    }
    
    @Transactional
    public GcMasterRecord checkMasterRecord(final Room room) {
        final List<GcMasterRecord> list = this.getMasterRecrods(room.getId());
        if (list.size() == 0) {
            throw new CodedBaseRuntimeException("无竞标记录!");
        }
        for (int i = 1; i < list.size(); ++i) {
            final GcMasterRecord r = list.get(i);
            this.dao.executeUpdate("update PubUser a set a.money = coalesce(a.money,0) + :money where a.id=:uid and status = '0' ", ImmutableMap.of( "money", r.getFreeze(),  "uid",  r.getUid()));
            r.setStatus("3");
            r.setAddBack(r.getFreeze());
            r.setRestBetable(0.0);
            this.dao.save(GcMasterRecord.class, r);
        }
        list.get(0).setStatus("1");
        this.dao.save(GcMasterRecord.class, list.get(0));
        return list.get(0);
    }
    
    @Transactional
    public void addMasterFreeze(final User user, final Integer masterRecordId, final Double addedFreeze) {
        final int userEffeted = this.dao.executeUpdate("update PubUser a set a.money = coalesce(a.money,0) - :money where a.id=:uid and a.money>=:money",  ImmutableMap.of( "money",  addedFreeze,  "uid",  user.getId()));
        if (userEffeted == 0) {
            throw new CodedBaseRuntimeException("账户余额不足!");
        }
        this.dao.executeUpdate("update GcMasterRecord  set freeze = coalesce(freeze,0) + :freeze, restBetable = coalesce(restBetable,0) + :freeze where id=:id",  ImmutableMap.of( "freeze", addedFreeze, "id", masterRecordId));
    }
    
    @Transactional
    public GcMasterRecord newMasterRecord(final User user, final Room room, final Double freeze) {
        final int userEffeted = this.dao.executeUpdate("update PubUser  set money = coalesce(money,0) - :money,exp=coalesce(exp,0)+:exp where id=:uid and money>=:money",  ImmutableMap.of( "money",  freeze,  "exp", freeze,  "uid",  user.getId()));
        if (userEffeted == 0) {
            throw new CodedBaseRuntimeException("账户余额不足!");
        }
        final GcMasterRecord gmr = new GcMasterRecord();
        gmr.setUid(user.getId());
        gmr.setUserId(user.getUserId());
        gmr.setFreeze(freeze);
        gmr.setRoomId(room.getId());
        gmr.setRestBetable(freeze);
        gmr.setAddBack(0.0);
        gmr.setUserInout(0.0);
        gmr.setStatus("0");
        this.dao.save(GcMasterRecord.class, gmr);
        return gmr;
    }
    
    @Transactional
    public List<GcMasterRecord> getMasterRecrods(final String roomId) {
        return this.dao.findByHql("from GcMasterRecord where roomId =:roomId and  status = '0' order by freeze desc", ImmutableMap.of( "roomId",  roomId));
    }
    
    static {
        NAMES = new String[] { "牛牛", "牛①", "牛②", "牛③", "牛④", "牛⑤", "牛⑥", "牛⑦", "牛⑧", "牛⑨", "牛牛", "金牛", "对子", "顺子", "满牛", "豹子" };
        TUORA = new String[] { "0.12", "1.23", "2.34", "3.45", "4.56", "5.67", "6.78", "7.89" };
        GODEN = new String[] { "0.10", "0.20", "0.30", "0.40", "0.50", "0.60", "0.70", "0.80", "0.90" };
    }
}