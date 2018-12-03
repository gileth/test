// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service;

import java.util.Date;
import org.takeback.chat.entity.GcWaterLog;
import org.takeback.chat.entity.GcRoomMember;
import org.takeback.chat.service.admin.SystemConfigService;
import org.takeback.chat.store.room.Room;
import org.takeback.chat.store.user.User;
import java.util.List;
import org.takeback.chat.lottery.listeners.GameException;
import java.io.Serializable;
import org.takeback.chat.entity.GcLottery;
import java.util.Iterator;
import java.util.Collection;
import java.math.BigDecimal;
import org.takeback.util.BeanUtils;
import org.takeback.chat.entity.GcLotteryDetail;
import org.takeback.chat.lottery.LotteryDetail;
import org.takeback.chat.lottery.Lottery;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;
import org.springframework.stereotype.Service;
import org.takeback.service.BaseService;

@Service("lotteryService")
public class LotteryService extends BaseService
{
    @Autowired
    RoomStore roomStore;
    
    @Transactional(rollbackFor = { Throwable.class })
    public int setLotteryExpired(final String lotteryId) {
        return this.dao.executeUpdate("update GcLottery a set a.status = '2' where a.id = :id and a.status = '0'", ImmutableMap.of("id", lotteryId));
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public int setLotteryFinished(final String lotteryId) {
        return this.dao.executeUpdate("update GcLottery a set a.status = '1' where a.id = :id and a.status = '0'", ImmutableMap.of("id", lotteryId));
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public int setRoomStatus(final String roomId, final String status) {
        return this.dao.executeUpdate("update GcRoom a set a.status =:status where a.id =:roomId", ImmutableMap.of("status", status,"roomId", roomId));
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public int moneyDown(final Integer uid, final Double money) {
        System.out.println("uid:" + uid + "  money:" + money);
        return this.dao.executeUpdate("update PubUser a set a.money = coalesce(a.money,0) - :money,a.exp=coalesce(exp,0)+:exp where a.id=:uid and a.money>=:money", ImmutableMap.of("money", money,"exp", money,"uid", uid));
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public int moneyUp(final Integer uid, final Double money) {
        return this.dao.executeUpdate("update PubUser a set a.money = coalesce(a.money,0) + :money where a.id=:uid ", ImmutableMap.of("money", money,"uid", uid));
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public int waterDown(final String roomId, final Double money) {
        return this.dao.executeUpdate("update GcRoom a set a.sumFee = coalesce(a.sumFee,0) - :money where a.id=:roomId ", ImmutableMap.of("money", money,"roomId", roomId));
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public int waterUp(final String roomId, final Double money) {
        return this.dao.executeUpdate("update GcRoom a set a.sumFee = coalesce(a.sumFee,0) + :money,a.sumPack = coalesce(a.sumPack,0)+1 where a.id=:roomId ", ImmutableMap.of("money", money,"roomId", roomId));
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void saveLotteryDetail(final Lottery lottery, final LotteryDetail detail) {
        final GcLotteryDetail gcLotteryDetail = BeanUtils.map(detail, GcLotteryDetail.class);
        gcLotteryDetail.setLotteryid(lottery.getId());
        gcLotteryDetail.setLotteryid(lottery.getId());
        this.dao.save(GcLotteryDetail.class, gcLotteryDetail);
        final BigDecimal money = detail.getCoin();
        this.dao.executeUpdate("update PubUser set money=money+:money where id = :uid", ImmutableMap.of("money", money.doubleValue(),"uid", detail.getUid()));
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public BigDecimal giftLotteryExpired(final Lottery lottery) {
        final Integer sender = lottery.getSender();
        if (sender <= 0) {
            return null;
        }
        final Map<Integer, LotteryDetail> dts = lottery.getDetail();
        final Collection<LotteryDetail> c = dts.values();
        BigDecimal bd = lottery.getMoney();
        for (final LotteryDetail ld : c) {
            bd = bd.subtract(ld.getCoin());
        }
        this.dao.executeUpdate("update PubUser a set a.money =coalesce(a.money,0) + :rest where a.id = :uid ", ImmutableMap.of("rest", bd.doubleValue(),"uid", sender));
        this.dao.executeUpdate("update GcLottery a set a.status = '2' where a.id = :id and a.status = '0'", ImmutableMap.of("id", lottery.getId()));
        return bd;
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void changeMoney(final Map<Integer, Double> data) {
        for (final Integer uid : data.keySet()) {
            final Double v = data.get(uid);
            this.dao.executeUpdate("update PubUser a set a.money =coalesce(a.money,0) + :money where a.id = :uid ", ImmutableMap.of("money", v,"uid", uid));
        }
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void createLottery(final Lottery lottery, final double deposit) {
        final Integer uid = lottery.getSender();
        final GcLottery gcLottery = BeanUtils.map(lottery, GcLottery.class);
        this.dao.save(GcLottery.class, gcLottery);
        this.moneyDown(uid, deposit);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public Lottery loadLottery(final String lotteryId) {
        final GcLottery gcLottery = this.dao.get(GcLottery.class, lotteryId);
        if (gcLottery == null) {
            return null;
        }
        final Lottery lottery = BeanUtils.map(gcLottery, Lottery.class);
        final List<GcLotteryDetail> detailList = this.dao.findByProperty(GcLotteryDetail.class, "lotteryId", lotteryId);
        for (final GcLotteryDetail gld : detailList) {
            final LotteryDetail detail = BeanUtils.map(gld, LotteryDetail.class);
            lottery.addDetail(detail);
        }
        lottery.setStatus(gcLottery.getStatus());
        if ("1".equals(lottery.getStatus())) {
            try {
                lottery.finished();
            }
            catch (GameException e) {
                e.printStackTrace();
            }
        }
        else if ("2".equals(lottery.getStatus())) {
            try {
                lottery.expired();
            }
            catch (GameException e) {
                e.printStackTrace();
            }
        }
        return lottery;
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public List<GcLottery> loadRecentLottery(final String roomId) {
        final List<GcLottery> lotteries = this.dao.findByHqlPaging("from GcLottery where  roomId =:roomId and status<>0 order by id desc", ImmutableMap.of("roomId", roomId), 10, 1);
        return lotteries;
    }
    
    @Transactional
    public void test() {
        final List<Double> l = this.dao.findByHql("select sum(money)+(select sum(sumFee) from GcRoom ) from PubUser");
        System.out.println("\u7ad9\u5185\u603b\u91d1\u989d:" + l.get(0));
    }
    
    @Transactional
    public Double setProxyWater(final User child, final Double water, final String roomId, final String gameType, final String lotteryId) {
        if (water <= 0.0) {
            return water;
        }
        final Room r = this.roomStore.get(roomId);
        if (!"G022".equals(r.getType())) {
            return water;
        }
        final Double rate = Double.valueOf(SystemConfigService.getInstance().getValue("water"));
        Double restWater = water * rate;
        final List<GcRoomMember> ls = this.dao.findByProperties(GcRoomMember.class, ImmutableMap.of("roomId", roomId,"isPartner", "1"));
        for (final GcRoomMember grm : ls) {
            final Double partnerRate = grm.getRate();
            if (partnerRate > 0.0) {
                final Double partnerWater = water * rate * (partnerRate / 100.0);
                restWater -= partnerWater;
                this.dao.executeUpdate("update PubUser a set a.money =coalesce(a.money,0) + :money where a.id = :uid ", ImmutableMap.of("money", partnerWater, "uid", grm.getUid()));
                final GcWaterLog gwl = new GcWaterLog();
                gwl.setUid(child.getId());
                gwl.setUserId(child.getUserId());
                gwl.setFullWater(water);
                gwl.setWater(partnerWater);
                gwl.setCreateDate(new Date());
                gwl.setGameType(gameType);
                gwl.setRoomId(roomId);
                gwl.setLotteryId(lotteryId);
                gwl.setParentId(grm.getUid());
                this.dao.save(GcWaterLog.class, gwl);
            }
        }
        if (restWater > 0.0) {
            final GcWaterLog gwl2 = new GcWaterLog();
            gwl2.setUid(child.getId());
            gwl2.setUserId(child.getUserId());
            gwl2.setFullWater(water);
            gwl2.setWater(restWater);
            gwl2.setCreateDate(new Date());
            gwl2.setGameType(gameType);
            gwl2.setRoomId(roomId);
            gwl2.setLotteryId(lotteryId);
            gwl2.setParentId(r.getOwner());
            this.dao.save(GcWaterLog.class, gwl2);
            this.dao.executeUpdate("update PubUser a set a.money =coalesce(a.money,0) + :money where a.id = :uid ", ImmutableMap.of("money", restWater,"uid", r.getOwner()));
        }
        return water * (1.0 - rate);
    }
}
