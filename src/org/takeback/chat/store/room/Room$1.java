// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.store.room;

import java.util.Iterator;
import java.util.List;
import org.takeback.chat.lottery.LotteryDetail;
import org.takeback.chat.entity.GcLotteryDetail;
import org.takeback.util.BeanUtils;
import org.takeback.chat.lottery.DefaultLottery;
import org.takeback.util.exception.CodedBaseException;
import java.io.Serializable;
import org.takeback.chat.entity.GcLottery;
import org.takeback.util.ApplicationContextHolder;
import org.takeback.chat.service.LotteryService;
import org.takeback.chat.lottery.Lottery;
import com.google.common.cache.CacheLoader;

class Room$1 extends CacheLoader<String, Lottery> {
    public Lottery load(final String s) throws Exception {
        final LotteryService lotteryService = (LotteryService)ApplicationContextHolder.getBean("lotteryService");
        final GcLottery gcLottery = lotteryService.get(GcLottery.class, s);
        if (gcLottery == null) {
            throw new CodedBaseException(530, "lottery " + s + " not exists");
        }
        final Lottery lottery = new DefaultLottery(gcLottery.getMoney(), gcLottery.getNumber());
        if (Room.access$000(Room.this) != null) {
            lottery.setRoomAndLotteryListener(Room.access$000(Room.this));
        }
        BeanUtils.copy(gcLottery, lottery);
        final List<GcLotteryDetail> ls = lotteryService.findByProperty(GcLotteryDetail.class, "lotteryid", s);
        for (final GcLotteryDetail gcLotteryDetail : ls) {
            final LotteryDetail lotteryDetail = new LotteryDetail(gcLotteryDetail.getUid(), gcLotteryDetail.getCoin());
            BeanUtils.copy(gcLotteryDetail, lotteryDetail);
            lottery.addDetail(lotteryDetail);
        }
        return lottery;
    }
}