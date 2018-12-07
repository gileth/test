// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service;

import org.takeback.chat.store.room.Room;
import org.takeback.util.exception.CodedBaseRuntimeException;
import org.takeback.chat.entity.GcRoomMoney;
import org.apache.commons.lang.StringUtils;
import java.util.ArrayList;
import java.io.Serializable;
import org.apache.commons.collections.map.HashedMap;
import com.google.common.collect.ImmutableMap;
import java.util.Iterator;
import org.takeback.chat.entity.GcRoomProperty;
import java.util.HashMap;
import org.springframework.transaction.annotation.Transactional;
import org.takeback.chat.entity.GcRoom;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;
import org.springframework.stereotype.Service;
import org.takeback.service.BaseService;

@Service
public class RoomService extends BaseService
{
    @Autowired
    RoomStore roomStore;
    private static int ONCE_FETCH_COUNT;
    public static int KICK_TIME;
    
    @Transactional(readOnly = true)
    public List<GcRoom> getRooms(final int pageNo, final Map<String, Object> params) {
        return this.dao.find(GcRoom.class, params, RoomService.ONCE_FETCH_COUNT, pageNo, "hot desc,createdate desc");
    }
    
    @Transactional(readOnly = true)
    public List<GcRoom> getActivedRooms() {
        return this.dao.findByHql("from GcRoom a where a.status != '9' order by a.hot desc,a.createdate desc", null);
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getRoomProperties(final String roomId) {
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put("roomId", roomId);
        final List<GcRoomProperty> list = this.dao.findByHql("from GcRoomProperty a where roomId=:roomId order by id asc ", param);
        final Map<String, Object> res = new HashMap<String, Object>();
        for (final GcRoomProperty prop : list) {
            res.put(prop.getConfigKey(), prop.getConfigValue());
        }
        return res;
    }
    
    @Transactional(readOnly = true)
    public int getUserRoomCount(final Integer userId) {
        return (int)this.dao.count(GcRoom.class, ImmutableMap.of( "owner",  userId));
    }
    
    @Transactional(readOnly = true)
    public List<GcRoom> getUserRooms(final Integer userId, final int pageSize, final int pageNo) {
        return this.dao.findByHqlPaging("from GcRoom where owner=:owner", ImmutableMap.of( "owner", userId), pageSize, pageNo);
    }
    
    @Transactional
    public void initRoomStatus() {
        this.dao.executeUpdate("update GcRoom set status ='0' ", (Map<String, Object>)new HashedMap());
        final List<GcRoom> list = this.dao.findByHql("from GcRoom");
        for (final GcRoom rm : list) {
            this.roomStore.reload(rm.getId());
        }
    }
    
    @Transactional(readOnly = true)
    public List<Map<String, String>> getRoomProps(final String roomId) {
        final List<GcRoomProperty> list = this.dao.findByHql("from GcRoomProperty where roomId=:roomId order by id",  ImmutableMap.of( "roomId", roomId));
        if (list == null || list.isEmpty()) {
            return new ArrayList<Map<String, String>>();
        }
        final List<Map<String, String>> props = new ArrayList<Map<String, String>>(list.size());
        for (final GcRoomProperty gcRoomProperty : list) {
            final String alias = StringUtils.isEmpty(gcRoomProperty.getAlias()) ? gcRoomProperty.getConfigKey() : gcRoomProperty.getAlias();
            final Map<String, String> map = ImmutableMap.of( "key",  gcRoomProperty.getConfigKey(),  "value",  gcRoomProperty.getConfigValue(), "alias", alias);
            props.add(map);
        }
        return props;
    }
    
    @Transactional(readOnly = true)
    public Map<String, String> getQunInfo(final String roomId) {
        final List<GcRoomMoney> ls = this.dao.findByProperty(GcRoomMoney.class, "roomId", roomId);
        final Map info = (Map)new HashedMap();
        if (ls.size() == 1) {
            final GcRoomMoney grm = ls.get(0);
            info.put("restMoney", grm.getRestMoney());
        }
        return (Map<String, String>)info;
    }
    
    @Transactional
    public void addMoney(final String roomId, final Integer uid, final int money) {
        final List<GcRoomMoney> ls = this.dao.findByProperty(GcRoomMoney.class, "roomId", roomId);
        if (ls.size() <= 0) {
            throw new CodedBaseRuntimeException("房间不存在!");
        }
        final int effected = this.dao.executeUpdate("update PubUser set money =money -:money where id = :uid and  money > :money",  ImmutableMap.of( "money",  (money + 0.0),  "uid", uid));
        if (effected == 0) {
            throw new CodedBaseRuntimeException("账户金币不足,请及时充值!");
        }
        this.dao.executeUpdate("update GcRoomMoney set totalMoney=totalMoney+:money , restMoney = restMoney+:money where roomId = :roomId",  ImmutableMap.of( "money",  (money + 0.0),  "roomId", roomId));
    }
    
    @Transactional
    public Double dismissRoom(final String roomId, final Integer uid) {
        Double restMoney = 0.0;
        final Room room = this.roomStore.get(roomId);
        if (room == null) {
            throw new CodedBaseRuntimeException("房间不存在!");
        }
        if (!room.getOwner().equals(uid)) {
            throw new CodedBaseRuntimeException("无权解散房间!");
        }
        final List<GcRoomMoney> ls = this.dao.findByProperty(GcRoomMoney.class, "roomId", roomId);
        if (ls.size() > 0) {
            final GcRoomMoney rec = ls.get(0);
            if (rec.getRestMoney() > 0.0) {
                this.dao.executeUpdate("update PubUser set money =money +:money where id = :uid",  ImmutableMap.of( "money", rec.getRestMoney(),  "uid",  uid));
            }
            this.dao.executeUpdate("delete from GcRoom where id=:roomId", ImmutableMap.of( "roomId",  roomId));
            this.dao.executeUpdate("delete from GcRoomProperty where roomId=:roomId", ImmutableMap.of( "roomId",  roomId));
            this.dao.executeUpdate("delete from GcRoomMember where roomId=:roomId", ImmutableMap.of( "roomId",  roomId));
            this.dao.executeUpdate("delete from GcRoomMoney where roomId=:roomId", ImmutableMap.of( "roomId",  roomId));
            restMoney = rec.getRestMoney();
        }
        return restMoney;
    }
    
    @Transactional
    public void modifyRoomInfo(final String roomId, final String key, final String value) {
        if (key.equals("name")) {
            if (value.length() > 10) {
                throw new CodedBaseRuntimeException("房间名不能超过10个字符!");
            }
            final List<GcRoom> ls = this.dao.findByProperty(GcRoom.class, "name", value);
            if (ls.size() > 0 && !ls.get(0).getId().equals(roomId)) {
                throw new CodedBaseRuntimeException("房间名已经存在!");
            }
            this.dao.executeUpdate("update GcRoom set name = :name where id=:id",  ImmutableMap.of( "name",  value,  "id", roomId));
            this.roomStore.reload(roomId);
        }
        else if (key.equals("id")) {
            if (value.length() > 10) {
                throw new CodedBaseRuntimeException("房间ID不能超过10个字符!");
            }
            final GcRoom r = this.dao.get(GcRoom.class, value);
            if (r != null && !r.getId().equals(roomId)) {
                throw new CodedBaseRuntimeException("房间ID已经存在!");
            }
            this.dao.executeUpdate("update GcRoom set id = :newId where id=:id", ImmutableMap.of( "newId", value,  "id",  roomId));
            this.dao.executeUpdate("update GcRoomProperty set roomId = :newId where roomId=:id",  ImmutableMap.of( "newId",  value, "id", roomId));
            this.dao.executeUpdate("update GcRoomMember set roomId = :newId where roomId=:id",  ImmutableMap.of( "newId", value,  "id",  roomId));
            this.dao.executeUpdate("update GcRoomMoney set roomId = :newId where roomId=:id",  ImmutableMap.of( "newId", value,  "id", roomId));
            this.roomStore.reload(roomId);
            this.roomStore.reload(value);
        }
        else if (key.equals("psw")) {
            if (value.length() > 6) {
                throw new CodedBaseRuntimeException("密码不能超过6个字符!");
            }
            this.dao.executeUpdate("update GcRoom set psw = :psw where id=:id",  ImmutableMap.of( "psw",  value,  "id", roomId));
            this.roomStore.reload(roomId);
        }
        else {
            Integer v = 0;
            try {
                v = Integer.valueOf(value);
            }
            catch (Exception e) {
                throw new CodedBaseRuntimeException("必须是整数数字!");
            }
            if (v < 1) {
                throw new CodedBaseRuntimeException("请填写大于0的整数!");
            }
            final Room r2 = this.roomStore.get(roomId);
            if (key.startsWith("conf_n") && !"conf_n15".equals(key)) {
                final Double d = Double.valueOf(r2.getProperties().get("conf_n15").toString());
                if (v > d) {
                    throw new CodedBaseRuntimeException("任何牛牛点数赔率不能超过豹子赔率!");
                }
            }
            else if ("conf_n15".equals(key)) {
                for (int i = 1; i < 15; ++i) {
                    final Double d2 = Double.valueOf(r2.getProperties().get("conf_n" + i).toString());
                    if (v < d2) {
                        throw new CodedBaseRuntimeException("豹子赔率必须大于其他点数赔率!");
                    }
                }
            }
            this.dao.executeUpdate("update GcRoomProperty set configValue=:value where roomId=:roomId and configKey=:key",  ImmutableMap.of( "value", v.toString(),  "roomId",  roomId,  "key", key));
            this.roomStore.reload(roomId);
        }
    }
    
    static {
        RoomService.ONCE_FETCH_COUNT = 5;
        RoomService.KICK_TIME = 1800;
    }
}