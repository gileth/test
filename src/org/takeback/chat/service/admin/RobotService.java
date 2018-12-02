// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import org.takeback.chat.entity.PubUser;
import org.takeback.util.BeanUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.takeback.chat.store.room.RoomThread;
import java.util.Iterator;
import org.takeback.chat.store.user.User;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import org.takeback.chat.store.room.Room;
import org.takeback.chat.entity.GcRoom;
import java.util.ArrayList;
import org.takeback.util.exp.ExpressionProcessor;
import org.takeback.util.converter.ConversionUtils;
import java.util.List;
import org.takeback.util.exception.CodedBaseRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.collections.map.HashedMap;
import java.util.HashMap;
import org.takeback.chat.store.user.RobotUser;
import java.util.Stack;
import java.util.Map;
import org.takeback.chat.store.user.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;
import org.springframework.stereotype.Service;
import org.takeback.core.service.MyListService;

@Service("robotAdminService")
public class RobotService extends MyListService
{
    @Autowired
    RoomStore roomStore;
    @Autowired
    private UserStore userStore;
    private Map<Integer, Thread> ts;
    private Stack<RobotUser> freeRobots;
    private Map<String, Thread> roomThreads;
    private Map<String, Stack<RobotUser>> workingRobots;
    
    public RobotService() {
        this.ts = new HashMap<Integer, Thread>();
        this.freeRobots = null;
        this.roomThreads = (Map<String, Thread>)new HashedMap();
        this.workingRobots = new HashMap<String, Stack<RobotUser>>();
    }
    
    @Transactional(readOnly = true)
    @Override
    public Map<String, Object> list(final Map<String, Object> req) {
        final String entityName = "GcRoom";
        if (StringUtils.isEmpty((CharSequence)entityName)) {
            throw new CodedBaseRuntimeException(404, "missing entityName");
        }
        final int limit = (Integer) req.get(RobotService.LIMIT);
        final int page =  (Integer)req.get(RobotService.PAGE);
        final List<?> cnd = ConversionUtils.convert(req.get(RobotService.CND),  List.class);
        String filter = null;
        if (cnd != null) {
            filter = ExpressionProcessor.instance().toString(cnd);
        }
        final String orderInfo = (String)req.get("id");
        final List<GcRoom> ls = this.dao.query(entityName, filter, limit, page, orderInfo);
        this.afterList(ls);
        final List<Map> list = new ArrayList<Map>();
        for (int i = 0; i < ls.size(); ++i) {
            final GcRoom room = ls.get(i);
            final Map<String, Object> m = new HashMap<String, Object>();
            final Room rm = this.roomStore.get(room.getId());
            int num = 0;
            if (rm != null) {
                num = this.getRobotSize(rm);
            }
            m.put("id", room.getId());
            m.put("roomName", room.getName());
            m.put("robotNum", num);
            list.add(m);
        }
        final long count = this.dao.totalSize(entityName, filter);
        final Map<String, Object> result = new HashMap<String, Object>();
        result.put("totalSize", count);
        result.put("body", list);
        return result;
    }
    
    private Integer getRobotSize(final Room r) {
        final Map<Integer, User> users = r.getUsers();
        final Iterator<Integer> itr = users.keySet().iterator();
        Integer num = 0;
        while (itr.hasNext()) {
            final Integer uid = itr.next();
            final User u = users.get(uid);
            if (u instanceof RobotUser) {
                ++num;
            }
        }
        return num;
    }
    
    @Transactional(readOnly = true)
    @Override
    public Object load(final Map<String, Object> req) {
        final Object pkey = req.get("id");
        final GcRoom room = this.dao.get(GcRoom.class, pkey.toString());
        final Map<String, Object> entity = new HashMap<String, Object>();
        final Room rm = this.roomStore.get(room.getId());
        int num = 0;
        if (rm != null) {
            num = this.getRobotSize(rm);
        }
        entity.put("id", room.getId());
        entity.put("roomName", room.getName());
        entity.put("robotNum", num);
        return entity;
    }
    
    @Transactional
    @Override
    public void save(final Map<String, Object> req) {
        final Map<String, Object> data = (Map<String, Object>) req.get("data");
        final String id = (String) data.get("id");
        final int num = Integer.valueOf((String) data.get("robotNum"));
        final Room rm = this.roomStore.get(id);
        final int curNum = this.getRobotSize(rm);
        int change = num - curNum;
        if (change > 0) {
            final List<RobotUser> robots = this.getFreeRobots(change);
            final Thread thread = this.roomThreads.get(rm.getId());
            if (thread == null) {
                final RoomThread rt = new RoomThread();
                rt.setRoom(rm);
                final Thread r = new Thread(rt);
                r.start();
                this.roomThreads.put(rm.getId(), r);
            }
            for (final RobotUser ru : robots) {
                this.userStore.reload(ru.getId());
                rm.join(ru);
                this.freeRobots.remove(ru);
            }
        }
        else {
            change = Math.abs(change);
            final Thread thread2 = this.roomThreads.get(rm.getId());
            if (thread2 == null) {
                return;
            }
            for (int i = 0; i < change; ++i) {
                final RobotUser r2 = this.pickRobotInRoom(rm);
                rm.left(r2);
                this.freeRobots.add(r2);
            }
        }
    }
    
    public RobotUser pickRobotInRoom(final Room room) {
        final Map<Integer, User> users = room.getUsers();
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
    
    @Transactional(readOnly = true)
    public List<RobotUser> getFreeRobots(final int num) {
        if (this.freeRobots == null) {
            this.freeRobots = new Stack<RobotUser>();
            final String hql = "from PubUser where userType=9  order by id asc";
            final List<PubUser> rs = this.dao.findByHql(hql);
            for (int i = 0; i < rs.size(); ++i) {
                final RobotUser r = BeanUtils.map(rs.get(i), RobotUser.class);
                this.freeRobots.push(r);
            }
        }
        if (this.freeRobots.size() < num) {
            throw new CodedBaseRuntimeException("\u7a7a\u95f2\u673a\u5668\u4eba:" + this.freeRobots.size() + "\u4e2a");
        }
        final List<RobotUser> robots = new ArrayList<RobotUser>();
        for (int j = 0; j < num; ++j) {
            robots.add(this.freeRobots.pop());
        }
        return robots;
    }
}
