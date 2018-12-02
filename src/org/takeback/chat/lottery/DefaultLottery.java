// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.lottery;

import org.slf4j.LoggerFactory;
import org.takeback.util.JSONUtils;
import java.util.Iterator;
import org.joda.time.LocalDateTime;
import org.takeback.chat.lottery.listeners.GameException;
import org.apache.commons.lang3.StringUtils;
import com.google.common.collect.Maps;
import org.takeback.util.identity.SerialNumberGenerator;
import org.takeback.util.exception.CodedBaseRuntimeException;
import java.util.concurrent.locks.ReentrantLock;
import org.takeback.chat.lottery.listeners.RoomAndLotteryListener;
import java.util.Map;
import org.takeback.chat.service.GameMonitor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import org.slf4j.Logger;

public class DefaultLottery implements Lottery
{
    private static final Logger log;
    private static final String ID_PREFIX = "LT";
    private Lock lock;
    private String id;
    private BigDecimal money;
    private Integer number;
    private AtomicInteger restNumber;
    private BigDecimal restMoney;
    private Integer sender;
    private Random random;
    private String roomId;
    private String description;
    private String title;
    private Date createTime;
    private String status;
    private String type;
    private Integer expiredSeconds;
    private AtomicBoolean open;
    private GameMonitor monitor;
    private Map<Integer, LotteryDetail> detail;
    private RoomAndLotteryListener roomAndLotteryListener;
    
    public DefaultLottery(final BigDecimal money, final Integer number) {
        this.lock = new ReentrantLock();
        this.status = "0";
        this.type = "1";
        this.expiredSeconds = 60;
        this.open = new AtomicBoolean(true);
        if (money.doubleValue() / number < 0.01) {
            throw new CodedBaseRuntimeException(500, "lottery create failed, each is must more than 0.01");
        }
        this.id = SerialNumberGenerator.generateSequenceNo("LT");
        this.restMoney = money;
        this.money = money;
        this.number = number;
        this.restNumber = new AtomicInteger(number);
        this.random = new Random();
        this.createTime = new Date();
        this.detail = (Map<Integer, LotteryDetail>)Maps.newLinkedHashMap();
        this.monitor = GameMonitor.getInstance();
    }
    
    private BigDecimal allocate(final int uid) {
        final int temp = this.restNumber.getAndDecrement();
        if (temp < 1) {
            throw new CodedBaseRuntimeException(515, String.format("lottery %s is already finished", this.id));
        }
        if (temp == 1) {
            final BigDecimal last = this.restMoney;
            this.restMoney = BigDecimal.ZERO;
            return last;
        }
        BigDecimal expect = null;
        if (StringUtils.isNotEmpty((CharSequence)this.roomId)) {
            expect = this.monitor.getOne(this.roomId, uid);
        }
        BigDecimal result = null;
        if (expect != null && expect.doubleValue() > 0.0 && expect.doubleValue() < this.restMoney.doubleValue() / 2.0) {
            result = expect;
            this.monitor.deleteOne(this.roomId, uid);
        }
        else {
            final Integer restCoin = this.restMoney.multiply(BigDecimal.valueOf(100L)).intValue();
            final Integer maxCoin = this.restMoney.multiply(BigDecimal.valueOf(200L)).divide(BigDecimal.valueOf(temp), 0, 3).intValue();
            Integer getCoin = this.random.nextInt(maxCoin) + 1;
            if (restCoin - getCoin < temp - 1) {
                getCoin = restCoin - (temp - 1);
            }
            result = new BigDecimal(getCoin).divide(BigDecimal.valueOf(100L));
        }
        this.restMoney = this.restMoney.subtract(result);
        return result;
    }
    
    @Override
    public BigDecimal open(final int uid) throws GameException {
        this.lock.lock();
        try {
            if (!this.isOpen()) {
                throw new GameException(510, String.format("lottery %s is already closed", this.id));
            }
            if (0 == this.restNumber.get()) {
                throw new GameException(514, String.format("lottery %s is already finished", this.id));
            }
            if (this.isExpired()) {
                this.expired();
                throw new GameException(511, String.format("lottery %s is already expired", this.id));
            }
            if (this.detail.containsKey(uid)) {
                throw new GameException(512, String.format("user %s have opend this lottery %s", uid, this.id));
            }
            if (this.roomAndLotteryListener != null && !this.roomAndLotteryListener.onBeforeOpen(uid, this)) {
                throw new GameException(513, String.format("lottery %s is not allow to open", this.id));
            }
            final BigDecimal result = this.allocate(uid);
            final LotteryDetail lotteryDetail = new LotteryDetail(uid, result);
            this.detail.put(uid, lotteryDetail);
            if (this.roomAndLotteryListener != null) {
                this.roomAndLotteryListener.onOpen(this, lotteryDetail);
            }
            if (0 == this.restNumber.get()) {
                this.finished();
            }
            return result;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public BigDecimal fakeOpen(final int uid) throws GameException {
        this.lock.lock();
        try {
            if (0 == this.restNumber.get()) {
                this.finished();
                throw new GameException(514, String.format("lottery %s is already finished", this.id));
            }
            if (this.detail.containsKey(uid)) {
                throw new GameException(512, String.format("user %s have opend this lottery %s", uid, this.id));
            }
            if (this.roomAndLotteryListener != null && !this.roomAndLotteryListener.onBeforeOpen(uid, this)) {
                throw new GameException(513, String.format("lottery %s is not allow to open", this.id));
            }
            final BigDecimal result = this.allocate(uid);
            final LotteryDetail lotteryDetail = new LotteryDetail(uid, result);
            this.detail.put(uid, lotteryDetail);
            if (this.roomAndLotteryListener != null) {
                this.roomAndLotteryListener.onOpen(this, lotteryDetail);
            }
            if (0 == this.restNumber.get()) {
                this.finished();
            }
            return result;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public Integer getRestNumber() {
        return this.restNumber.get();
    }
    
    @Override
    public void finished() throws GameException {
        if (this.open.compareAndSet(true, false)) {
            if (this.roomAndLotteryListener != null) {
                this.roomAndLotteryListener.onFinished(this);
            }
            this.setStatus("1");
        }
    }
    
    @Override
    public void expired() throws GameException {
        if (this.expiredSeconds == 0) {
            return;
        }
        if (this.open.compareAndSet(true, false)) {
            if (this.roomAndLotteryListener != null) {
                this.roomAndLotteryListener.onExpired(this);
            }
            this.setStatus("2");
        }
    }
    
    @Override
    public boolean isExpired() {
        if (this.expiredSeconds == 0) {
            return false;
        }
        if (this.status.equals("2")) {
            return true;
        }
        final LocalDateTime now = LocalDateTime.now();
        return now.minusSeconds((int)this.expiredSeconds).toDate().after(this.createTime);
    }
    
    @Override
    public Integer getExpiredSeconds() {
        return this.expiredSeconds;
    }
    
    @Override
    public void setExpiredSeconds(final Integer expiredSeconds) {
        this.expiredSeconds = expiredSeconds;
    }
    
    @Override
    public void setRoomAndLotteryListener(final RoomAndLotteryListener roomAndLotteryListener) {
        this.roomAndLotteryListener = roomAndLotteryListener;
    }
    
    @Override
    public String getType() {
        return this.type;
    }
    
    @Override
    public void setType(final String type) {
        this.type = type;
    }
    
    @Override
    public BigDecimal getMoney() {
        return this.money;
    }
    
    @Override
    public void setMoney(final BigDecimal money) {
        this.money = money;
        this.restMoney = money;
    }
    
    @Override
    public Integer getNumber() {
        return this.number;
    }
    
    @Override
    public void setNumber(final Integer number) {
        this.number = number;
    }
    
    @Override
    public Integer getSender() {
        return this.sender;
    }
    
    @Override
    public void setSender(final Integer sender) {
        this.sender = sender;
    }
    
    @Override
    public String getId() {
        return this.id;
    }
    
    @Override
    public void setId(final String id) {
        this.id = id;
    }
    
    @Override
    public String getRoomId() {
        return this.roomId;
    }
    
    @Override
    public void setRoomId(final String roomId) {
        this.roomId = roomId;
    }
    
    @Override
    public String getDescription() {
        return StringUtils.isEmpty((CharSequence)this.description) ? "\u606d\u559c\u53d1\u8d22,\u5927\u5409\u5927\u5229!" : this.description;
    }
    
    @Override
    public void setDescription(final String description) {
        this.description = description;
    }
    
    @Override
    public Date getCreateTime() {
        return this.createTime;
    }
    
    @Override
    public void setCreateTime(final Date createTime) {
        this.createTime = createTime;
    }
    
    @Override
    public String getStatus() {
        return this.status;
    }
    
    @Override
    public void setStatus(final String status) {
        this.status = status;
    }
    
    @Override
    public BigDecimal getRestMoney() {
        return this.restMoney;
    }
    
    @Override
    public boolean isOpen() {
        return this.open.get();
    }
    
    @Override
    public void addDetail(final LotteryDetail lotteryDetail) {
        this.detail.put(lotteryDetail.getUid(), lotteryDetail);
    }
    
    @Override
    public Map<Integer, LotteryDetail> getDetail() {
        return this.detail;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("id:").append(this.id).append("-").append(this.status).append("  ");
        final Map<Integer, LotteryDetail> lts = this.getDetail();
        final Iterator itr = lts.keySet().iterator();
        while (itr.hasNext()) {
            final Integer key = Integer.valueOf(itr.next().toString());
            final LotteryDetail detail = lts.get(key);
            if (detail != null) {
                sb.append(detail.toString()).append("\r\n");
            }
        }
        return sb.toString();
    }
    
    public static void main(final String[] args) {
        final DefaultLottery lottery = new DefaultLottery(BigDecimal.valueOf(100L), 6);
        int i = 1;
        try {
            while (true) {
                final BigDecimal result = lottery.open(i);
                System.out.println(result);
                ++i;
            }
        }
        catch (Exception e) {
            System.out.println(JSONUtils.toString(lottery.getDetail()));
        }
    }
    
    @Override
    public String getTitle() {
        return this.title;
    }
    
    @Override
    public void setTitle(final String title) {
        this.title = title;
    }
    
    static {
        log = LoggerFactory.getLogger((Class)DefaultLottery.class);
    }
}
