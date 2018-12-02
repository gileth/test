// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.store.room;

import javax.persistence.Column;
import javax.persistence.Basic;
import org.apache.commons.lang3.StringUtils;
import org.takeback.core.dictionary.DictionaryController;
import org.takeback.core.dictionary.Dictionary;
import org.takeback.util.exception.CodedBaseRuntimeException;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import org.takeback.chat.lottery.listeners.GameException;
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
import com.google.common.cache.CacheLoader;
import java.util.concurrent.TimeUnit;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import org.takeback.chat.lottery.listeners.RoomAndLotteryListener;
import java.util.ArrayList;
import org.takeback.chat.lottery.Lottery;
import com.google.common.cache.LoadingCache;
import org.takeback.chat.store.user.AnonymousUser;
import org.takeback.chat.store.user.User;
import java.util.Map;
import java.util.Date;
import org.takeback.chat.store.Item;

public class Room implements Item
{
    public static final Integer STEP_FREE;
    public static final Integer STEP_MASTER;
    public static final Integer STEP_CHECK1;
    public static final Integer STEP_CHECK2;
    public static final Integer STEP_CHECK3;
    public static final Integer STEP_START_BET;
    public static final Integer STEP_FINISH_BET;
    public static final Integer STEP_SEND_RED;
    public static final Integer STEP_PLAYING;
    public static final Integer STEP_PLAY_FINISHED;
    private String id;
    private String name;
    private String catalog;
    private String type;
    private Integer owner;
    private Integer limitNum;
    private Integer hot;
    private String roomimg;
    private String description;
    private String detail;
    private String rule;
    private String psw;
    private Date createdate;
    private String status;
    private Integer unDead;
    private Double shareRate;
    private Double sumPool;
    private Double poolAdd;
    private Double feeAdd;
    private Double sumFee;
    private String statusText;
    private String ownerText;
    private Map<String, Object> properties;
    private Map<Integer, User> users;
    private Map<String, AnonymousUser> guests;
    private LoadingCache<String, Lottery> lotteries;
    private Integer master;
    private Long masterStamp;
    private Integer masterTimes;
    private Integer manager;
    private Integer step;
    private Integer masterRecordId;
    private Boolean canTalk;
    private Boolean candRed;
    private ArrayList<Integer> shutup;
    private RoomAndLotteryListener roomAndLotteryListener;
    
    public Room() {
        this.status = "0";
        this.master = -1;
        this.masterStamp = 0L;
        this.masterTimes = 0;
        this.manager = 0;
        this.step = 0;
        this.canTalk = true;
        this.candRed = true;
        this.shutup = new ArrayList<Integer>();
        this.properties = new ConcurrentHashMap<String, Object>();
        this.users = new ConcurrentHashMap<Integer, User>();
        this.guests = new ConcurrentHashMap<String, AnonymousUser>();
        this.lotteries = (LoadingCache<String, Lottery>)CacheBuilder.newBuilder().expireAfterAccess(5L, TimeUnit.MINUTES).build((CacheLoader)new CacheLoader<String, Lottery>() {
            public Lottery load(final String s) throws Exception {
                final LotteryService lotteryService = (LotteryService)ApplicationContextHolder.getBean("lotteryService");
                final GcLottery gcLottery = lotteryService.get(GcLottery.class, s);
                if (gcLottery == null) {
                    throw new CodedBaseException(530, "lottery " + s + " not exists");
                }
                final Lottery lottery = new DefaultLottery(gcLottery.getMoney(), gcLottery.getNumber());
                if (Room.this.roomAndLotteryListener != null) {
                    lottery.setRoomAndLotteryListener(Room.this.roomAndLotteryListener);
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
        });
    }
    
    public void setRoomAndLotteryListener(final RoomAndLotteryListener roomAndLotteryListener) {
        this.roomAndLotteryListener = roomAndLotteryListener;
    }
    
    public RoomAndLotteryListener getRoomAndLotteryListener() {
        return this.roomAndLotteryListener;
    }
    
    public synchronized void start() throws GameException {
        if ("0".equals(this.status) && this.roomAndLotteryListener != null) {
            this.roomAndLotteryListener.onStart(this);
        }
    }
    
    public void addLottery(final Lottery lottery) {
        this.lotteries.put(lottery.getId(),lottery);
    }
    
    public Lottery getLottery(final String id) {
        try {
            return (Lottery)this.lotteries.get(id);
        }
        catch (ExecutionException e) {
            return null;
        }
    }
    
    public void showLotteries() {
        System.out.println(">>>>>>>>>>" + this.lotteries.asMap());
    }
    
    @JsonIgnore
    public Map<Integer, User> getUsers() {
        return this.users;
    }
    
    public void setUsers(final Map<Integer, User> users) {
        this.users = users;
    }
    
    @JsonIgnore
    public Map<String, AnonymousUser> getGuests() {
        return this.guests;
    }
    
    public void setGuests(final Map<String, AnonymousUser> guests) {
        this.guests = guests;
    }
    
    public synchronized void join(final User user) {
        if (this.users.size() < this.limitNum) {
            this.users.put(user.getId(), user);
            return;
        }
        throw new CodedBaseRuntimeException(530, "room is full");
    }
    
    public void left(final User user) {
        this.users.remove(user.getId());
    }
    
    public void guestJoin(final AnonymousUser anonymousUserPojo) {
        this.guests.put(anonymousUserPojo.getWebSocketSession().getId(), anonymousUserPojo);
    }
    
    public void guestLeft(final AnonymousUser anonymousUserPojo) {
        this.guests.remove(anonymousUserPojo.getWebSocketSession().getId());
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getCatalog() {
        return this.catalog;
    }
    
    public void setCatalog(final String catalog) {
        this.catalog = catalog;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
        this.roomAndLotteryListener = ApplicationContextHolder.getBean(type, RoomAndLotteryListener.class);
    }
    
    public Integer getOwner() {
        return this.owner;
    }
    
    public void setOwner(final Integer owner) {
        this.owner = owner;
        if (0 == owner) {
            this.ownerText = "\u7cfb\u7edf\u623f\u95f4";
        }
        else {
            this.ownerText = DictionaryController.instance().get("dic.pubuser").getText(String.valueOf(owner));
        }
    }
    
    public Integer getLimitNum() {
        return (this.limitNum == null) ? 50 : this.limitNum;
    }
    
    public void setLimitNum(final Integer limit) {
        this.limitNum = limit;
    }
    
    public Integer getHot() {
        return this.hot;
    }
    
    public void setHot(final Integer hot) {
        this.hot = hot;
    }
    
    public String getRoomimg() {
        return this.roomimg;
    }
    
    public void setRoomimg(final String roomimg) {
        this.roomimg = roomimg;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public Boolean isNeedPsw() {
        return StringUtils.isNotEmpty((CharSequence)this.psw);
    }
    
    @JsonIgnore
    public String getPsw() {
        return this.psw;
    }
    
    public void setPsw(final String psw) {
        this.psw = psw;
    }
    
    public Date getCreatedate() {
        return this.createdate;
    }
    
    public void setCreatedate(final Date createdate) {
        this.createdate = createdate;
    }
    
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(final String status) {
        this.status = status;
        this.statusText = DictionaryController.instance().get("dic.chat.roomStatus").getText(status);
    }
    
    public Integer getPosition() {
        return this.users.size();
    }
    
    public String getStatusText() {
        return this.statusText;
    }
    
    public String getOwnerText() {
        return this.ownerText;
    }
    
    public Map<String, Object> getProperties() {
        return this.properties;
    }
    
    public LoadingCache<String, Lottery> getLotteries() {
        return this.lotteries;
    }
    
    public void setProperties(final Map<String, Object> properties) {
        this.properties = properties;
    }
    
    public Double getFeeAdd() {
        return this.feeAdd;
    }
    
    public void setFeeAdd(final Double feeAdd) {
        this.feeAdd = feeAdd;
    }
    
    public Double getPoolAdd() {
        return this.poolAdd;
    }
    
    public void setPoolAdd(final Double poolAdd) {
        this.poolAdd = poolAdd;
    }
    
    public Double getSumFee() {
        return this.sumFee;
    }
    
    public void setSumFee(final Double sumFee) {
        this.sumFee = sumFee;
    }
    
    public Double getSumPool() {
        return this.sumPool;
    }
    
    public void setSumPool(final Double sumPool) {
        this.sumPool = sumPool;
    }
    
    public Double getShareRate() {
        return this.shareRate;
    }
    
    public void setShareRate(final Double shareRate) {
        this.shareRate = shareRate;
    }
    
    @Basic
    @Column(name = "rule", nullable = true)
    public String getRule() {
        return this.rule;
    }
    
    public void setRule(final String rule) {
        this.rule = rule;
    }
    
    public ArrayList<Integer> getShutup() {
        return this.shutup;
    }
    
    public void setShutup(final ArrayList<Integer> shutup) {
        this.shutup = shutup;
    }
    
    public Integer getMaster() {
        return this.master;
    }
    
    public void setMaster(final Integer master) {
        this.master = master;
    }
    
    public Integer getMasterTimes() {
        return this.masterTimes;
    }
    
    public void setMasterTimes(final Integer masterTimes) {
        this.masterTimes = masterTimes;
    }
    
    public Boolean getCanTalk() {
        return this.canTalk;
    }
    
    public void setCanTalk(final Boolean canTalk) {
        this.canTalk = canTalk;
    }
    
    public Boolean getCandRed() {
        return this.candRed;
    }
    
    public void setCandRed(final Boolean candRed) {
        this.candRed = candRed;
    }
    
    public String getDetail() {
        return this.detail;
    }
    
    public void setDetail(final String detail) {
        this.detail = detail;
    }
    
    public Long getMasterStamp() {
        return this.masterStamp;
    }
    
    public void setMasterStamp(final Long masterStamp) {
        this.masterStamp = masterStamp;
    }
    
    public Integer getUnDead() {
        return this.unDead;
    }
    
    public void setUnDead(final Integer unDead) {
        this.unDead = unDead;
    }
    
    public Integer getManager() {
        return this.manager;
    }
    
    public void setManager(final Integer manager) {
        this.manager = manager;
    }
    
    public Integer getStep() {
        return this.step;
    }
    
    public void setStep(final Integer step) {
        this.step = step;
    }
    
    public Integer getMasterRecordId() {
        return this.masterRecordId;
    }
    
    public void setMasterRecordId(final Integer masterRecordId) {
        this.masterRecordId = masterRecordId;
    }
    
    static {
        STEP_FREE = 0;
        STEP_MASTER = 1;
        STEP_CHECK1 = 2;
        STEP_CHECK2 = 3;
        STEP_CHECK3 = 4;
        STEP_START_BET = 5;
        STEP_FINISH_BET = 6;
        STEP_SEND_RED = 7;
        STEP_PLAYING = 8;
        STEP_PLAY_FINISHED = 9;
    }
}
