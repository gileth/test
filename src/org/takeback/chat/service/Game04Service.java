// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service;

import java.util.Iterator;
import org.takeback.chat.store.user.User;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import org.springframework.transaction.annotation.Transactional;
import org.takeback.chat.utils.NumberUtil;
import java.io.Serializable;
import org.takeback.chat.store.room.Room;
import org.takeback.util.BeanUtils;
import org.takeback.chat.entity.GcLotteryDetail;
import org.takeback.chat.lottery.LotteryDetail;
import org.takeback.chat.lottery.Lottery;
import org.takeback.chat.store.user.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;
import org.springframework.stereotype.Service;

@Service("game04Service")
public class Game04Service extends LotteryService
{
    @Autowired
    private RoomStore roomStore;
    @Autowired
    private UserStore userStore;
    @Autowired
    GameMonitor monitor;
    
    @Transactional(rollbackFor = { Throwable.class })
    public void saveDetail(final Lottery lottery, final LotteryDetail detail, final Double userInout) {
        final GcLotteryDetail gcLotteryDetail = BeanUtils.map(detail, GcLotteryDetail.class);
        gcLotteryDetail.setLotteryid(lottery.getId());
        gcLotteryDetail.setGameType("G04");
        final Room r = this.roomStore.get(lottery.getRoomId());
        final Double rate = r.getFeeAdd();
        final Double deposit = lottery.getMoney().doubleValue() / (1.0 - rate);
        gcLotteryDetail.setDeposit(deposit);
        if (userInout > 0.0) {
            gcLotteryDetail.setDesc1("\u65e0\u96f7");
            gcLotteryDetail.setAddback(NumberUtil.round(deposit + userInout));
            gcLotteryDetail.setInoutNum(NumberUtil.round(userInout));
        }
        else if (userInout < 0.0) {
            gcLotteryDetail.setDesc1("\u4e2d\u96f7");
            gcLotteryDetail.setAddback(detail.getCoin().doubleValue());
            gcLotteryDetail.setInoutNum(NumberUtil.round(userInout));
        }
        this.monitor.setData(lottery.getRoomId(), detail.getUid(), userInout);
        gcLotteryDetail.setRoomId(lottery.getRoomId());
        gcLotteryDetail.setMasterId(lottery.getSender());
        this.dao.save(GcLotteryDetail.class, gcLotteryDetail);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void gameStop(final Lottery lottery) {
        this.dao.executeUpdate("update GcRoom a set a.status=0 where id=:id",  ImmutableMap.of( "id", lottery.getRoomId()));
        this.dao.executeUpdate("update GcLottery a set a.status=2 where id=:id",  ImmutableMap.of( "id",  lottery.getId()));
    }
    
    @Transactional
    public void setWater(final String roomId, final User child, Double water, final String lotteryId) {
        water = this.setProxyWater(child, water, roomId, "G04", lotteryId);
        this.waterUp(roomId, water);
    }
    
    public void setMasterMonitorData(final Lottery lottery) {
        final Map<Integer, LotteryDetail> detail = lottery.getDetail();
        final Iterator itr = detail.keySet().iterator();
        final Room r = this.roomStore.get(lottery.getRoomId());
        final Double rate = r.getFeeAdd();
        final Double deposit = lottery.getMoney().doubleValue() / (1.0 - rate);
        final String raidStr = lottery.getDescription().charAt(lottery.getDescription().indexOf("\u96f7") + 1) + "";
        final Integer raidPoint = Integer.valueOf(raidStr);
        Double masterInout = -(deposit - lottery.getRestMoney().doubleValue());
        if (lottery.getRestNumber() == lottery.getNumber()) {
            masterInout = 0.0;
        }
        while (itr.hasNext()) {
            final Integer uid = (Integer) itr.next();
            final LotteryDetail d = detail.get(uid);
            final Integer tailPoint = NumberUtil.getTailPoint(d.getCoin());
            if (d.getUid().equals(lottery.getSender())) {
                continue;
            }
            if (!tailPoint.equals(raidPoint)) {
                continue;
            }
            masterInout += deposit;
        }
        this.monitor.setData(lottery.getRoomId(), lottery.getSender(), masterInout);
    }
}
