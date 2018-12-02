// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service;

import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import org.takeback.chat.entity.ValueControlLog;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Iterator;
import org.apache.commons.lang.math.RandomUtils;
import org.takeback.chat.utils.NumberUtil;
import org.takeback.chat.service.admin.SystemConfigService;
import org.takeback.chat.store.user.User;
import java.io.Serializable;
import org.takeback.chat.store.room.Room;
import java.util.ArrayList;
import org.takeback.chat.entity.ControlModel;
import java.util.List;
import org.takeback.service.BaseService;
import org.takeback.chat.store.user.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;
import org.springframework.stereotype.Service;

@Service
public class GameMonitor
{
    private static final Integer START_CONTROL;
    @Autowired
    RoomStore roomStore;
    @Autowired
    UserStore userStore;
    @Autowired
    BaseService baseService;
    private static GameMonitor instance;
    private static Integer[][][] NN;
    private List<ControlModel> cache;
    
    public GameMonitor() {
        this.cache = new ArrayList<ControlModel>();
        GameMonitor.instance = this;
    }
    
    public static GameMonitor getInstance() {
        return GameMonitor.instance;
    }
    
    public void setData(final String roomId, final Integer uid, final Double inoutNum) {
        try {
            final Room r = this.roomStore.get(roomId);
            final User u = this.userStore.get(uid);
            ControlModel c = this.getByRoomAndUid(roomId, uid);
            if (c == null) {
                c = new ControlModel(roomId, r.getName(), uid, u.getUserId(), u.getNickName(), NumberUtil.round(Double.valueOf(SystemConfigService.getInstance().getValue("control_default_rate")) / 100.0));
                this.cache.add(c);
            }
            c.setInoutNum(inoutNum);
            if ("9".equals(u.getUserType())) {
                return;
            }
            if ("1".equals(SystemConfigService.getInstance().getValue("control_flag")) && c.getPlayTimes() >= GameMonitor.START_CONTROL) {
                this.doControl(c);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void doControl(final ControlModel c) {
        final Double subRate = c.getCurrentRate() - c.getTargetRate();
        final Double winControlRate = Double.valueOf(SystemConfigService.getInstance().getValue("control_kill")) / 100.0;
        final Double winForceControlRate = winControlRate + 0.1;
        final Double loseControlRate = -Double.valueOf(SystemConfigService.getInstance().getValue("control_save")) / 100.0;
        final Room r = this.roomStore.get(c.getRoomId());
        if (subRate > winControlRate) {
            if (subRate > winForceControlRate) {
                c.setSuggests(null);
            }
            final Double suggest = this.getPointMaybeLose(r);
            if (suggest != null && (c.getSuggests() == null || "".equals(c.getSuggests()))) {
                c.setSuggests(suggest.toString());
            }
        }
        else if (subRate < loseControlRate) {
            final Double suggest = this.getPointMaybeWin(r);
            if (suggest != null && (c.getSuggests() == null || "".equals(c.getSuggests()))) {
                c.setSuggests(suggest.toString());
            }
        }
    }
    
    private Double getPointMaybeWin(final Room r) {
        if (r.getType().startsWith("G01")) {
            final Double money = Double.valueOf(r.getProperties().get("conf_money").toString());
            final Integer size = Integer.valueOf(r.getProperties().get("conf_size").toString());
            Double targetMoney = money / size;
            final Double addMoney = targetMoney * (RandomUtils.nextInt(2) + RandomUtils.nextDouble());
            targetMoney += addMoney * Math.random();
            return NumberUtil.round(targetMoney);
        }
        if (r.getType().startsWith("G02")) {
            final Integer point1 = RandomUtils.nextInt(5) + 5;
            return generateNNValue(point1);
        }
        return null;
    }
    
    private static Double generateNNValue(final Integer point) {
        final Integer[][] conf = GameMonitor.NN[point];
        final Integer random = RandomUtils.nextInt(conf.length - 1);
        final Integer[] data = conf[random];
        if (RandomUtils.nextInt() % 2 == 0) {
            return NumberUtil.round(data[0] / 10.0 + data[1] / 100.0);
        }
        return NumberUtil.round(data[1] / 10.0 + data[0] / 100.0);
    }
    
    public static void main(final String... args) {
        for (int i = 0; i < 1000; ++i) {
            final Double money = 1000.0;
            Integer size = 10;
            size += (int)(money / 10.0);
            Double targetMoney = money / size;
            targetMoney *= Math.random();
            if (targetMoney == 0.0) {
                targetMoney = 0.02;
            }
            System.out.println(targetMoney);
        }
    }
    
    private Double getPointMaybeLose(final Room r) {
        if (r.getType().startsWith("G01")) {
            final Double money = Double.valueOf(r.getProperties().get("conf_money").toString());
            Integer size = Integer.valueOf(r.getProperties().get("conf_size").toString());
            size += (int)(money / 10.0);
            Double targetMoney = money / size;
            targetMoney *= Math.random();
            if (targetMoney == 0.0) {
                targetMoney = 0.02;
            }
            return NumberUtil.round(targetMoney);
        }
        if (r.getType().startsWith("G02")) {
            Integer point1 = RandomUtils.nextInt(4);
            if (point1 == 0) {
                point1 = 1;
            }
            return generateNNValue(point1);
        }
        return null;
    }
    
    private ControlModel getByRoomAndUid(final String roomId, final Integer uid) {
        for (final ControlModel c : this.cache) {
            if (c.getRoomId().equals(roomId) && c.getUid().equals(uid)) {
                return c;
            }
        }
        return null;
    }
    
    public List<ControlModel> userList() {
        final List<ControlModel> sub = new ArrayList<ControlModel>();
        for (final ControlModel c : this.cache) {
            final User u = this.userStore.get(c.getUid());
            if (!"9".equals(u.getUserType())) {
                sub.add(c);
            }
        }
        Collections.sort(sub);
        return sub;
    }
    
    public List<ControlModel> robotsList() {
        final List<ControlModel> sub = new ArrayList<ControlModel>();
        for (final ControlModel c : this.cache) {
            final User u = this.userStore.get(c.getUid());
            if ("9".equals(u.getUserType())) {
                sub.add(c);
            }
        }
        Collections.sort(sub);
        return sub;
    }
    
    public List<ControlModel> listByRoomId(final String roomId) {
        final List<ControlModel> sub = new ArrayList<ControlModel>();
        for (final ControlModel c : this.cache) {
            if (c.getRoomId().equals(roomId)) {
                final User u = this.userStore.get(c.getUid());
                if ("9".equals(u.getUserType())) {
                    continue;
                }
                sub.add(c);
            }
        }
        Collections.sort(sub);
        return sub;
    }
    
    public List<ControlModel> listByUid(final Integer uid) {
        final List<ControlModel> sub = new ArrayList<ControlModel>();
        for (final ControlModel c : this.cache) {
            if (c.getUid().equals(uid)) {
                sub.add(c);
            }
        }
        Collections.sort(sub);
        return sub;
    }
    
    public ControlModel getById(final Long id) {
        for (final ControlModel c : this.cache) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }
    
    public List<ControlModel> listByUserId(final String userId) {
        final List<ControlModel> sub = new ArrayList<ControlModel>();
        for (final ControlModel c : this.cache) {
            final User u = this.userStore.get(c.getUid());
            if (c.getUserId().equals(userId) && !"9".equals(u.getUserType())) {
                sub.add(c);
            }
        }
        Collections.sort(sub);
        return sub;
    }
    
    public ControlModel getByRoomIdAndUId(final String roomId, final Integer uid) {
        final List<ControlModel> sub = new ArrayList<ControlModel>();
        for (final ControlModel c : this.cache) {
            final User u = this.userStore.get(c.getUid());
            if (c.getRoomId().equals(roomId) && c.getUid().equals(uid)) {
                return c;
            }
        }
        return null;
    }
    
    public void cleanUsers() {
        this.cache = new ArrayList<ControlModel>();
    }
    
    public BigDecimal getOne(final String roomId, final Integer uid) {
        final ControlModel c = this.getByRoomAndUid(roomId, uid);
        if (c == null) {
            return null;
        }
        final String values = c.getSuggests();
        if (values != null && values.length() > 0) {
            final String[] v = values.split(",");
            return new BigDecimal(v[0]);
        }
        return null;
    }
    
    @Transactional
    public void deleteOne(final String roomId, final Integer uid) {
        try {
            final ControlModel c = this.getByRoomAndUid(roomId, uid);
            if (c == null) {
                return;
            }
            final String values = c.getSuggests();
            if (values != null && values.length() > 0) {
                final String[] v = values.split(",");
                if (v.length == 0) {
                    return;
                }
                String suggest = "";
                final String deleteValue = v[0];
                for (int i = 1; i < v.length; ++i) {
                    suggest = suggest + v[i] + ",";
                }
                if (suggest.length() > 0) {
                    suggest = suggest.substring(0, suggest.length() - 1);
                }
                c.setSuggests(suggest);
                final ValueControlLog vcl = new ValueControlLog();
                final User u = this.userStore.get(uid);
                vcl.setCreateDate(new Date());
                vcl.setNickName(u.getUserId());
                vcl.setRoomId(roomId);
                vcl.setUid(uid);
                vcl.setRoomName(this.roomStore.get(roomId).getName());
                vcl.setVal(Double.valueOf(deleteValue));
                vcl.setAdmin("\u76d1\u63a7\u6a21\u5757");
                this.baseService.save(ValueControlLog.class, vcl);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static {
        START_CONTROL = 0;
        GameMonitor.NN = new Integer[][][] { { { 0, 1 }, { 9, 2 }, { 8, 3 }, { 7, 4 }, { 5, 6 } }, { { 0, 2 }, { 9, 3 }, { 8, 4 }, { 7, 5 } }, { { 0, 3 }, { 1, 2 }, { 9, 4 }, { 8, 5 }, { 7, 6 } }, { { 0, 4 }, { 1, 3 }, { 2, 2 }, { 9, 5 }, { 8, 6 } }, { { 0, 5 }, { 1, 4 }, { 2, 3 }, { 9, 6 }, { 8, 7 } }, { { 0, 6 }, { 1, 5 }, { 2, 4 }, { 3, 3 }, { 8, 8 }, { 9, 7 } }, { { 0, 7 }, { 1, 6 }, { 2, 5 }, { 3, 4 }, { 9, 8 } }, { { 0, 8 }, { 1, 7 }, { 2, 6 }, { 3, 5 }, { 4, 4 }, { 9, 9 } }, { { 0, 9 }, { 1, 8 }, { 2, 7 }, { 3, 6 }, { 4, 5 } }, { { 0, 0 }, { 1, 9 }, { 2, 8 }, { 3, 7 }, { 4, 6 }, { 5, 5 } } };
    }
}
