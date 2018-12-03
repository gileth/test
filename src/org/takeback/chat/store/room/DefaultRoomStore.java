// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.store.room;

import org.takeback.chat.store.Item;
import com.google.common.collect.Lists;
import org.takeback.chat.store.user.User;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.takeback.util.BeanUtils;
import org.takeback.util.exception.CodedBaseException;
import java.io.Serializable;
import org.takeback.chat.entity.GcRoom;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.RoomService;

public class DefaultRoomStore implements RoomStore
{
    @Autowired
    private RoomService roomService;
    private LoadingCache<String, Room> roomPojoMap;
    private int pageSize;
    
    public DefaultRoomStore() {
        this.pageSize = 5;
    }
    
    @Override
    public void init() {
        this.roomPojoMap = (LoadingCache<String, Room>)CacheBuilder.newBuilder().build((CacheLoader)new CacheLoader<String, Room>() {
            public Room load(final String s) throws Exception {
                final GcRoom gcRoom = DefaultRoomStore.this.roomService.get(GcRoom.class, s);
                if (gcRoom == null) {
                    throw new CodedBaseException(530, "room " + s + " not exists");
                }
                final Room room = BeanUtils.map(gcRoom, Room.class);
                final Map<String, Object> props = DefaultRoomStore.this.roomService.getRoomProperties(gcRoom.getId());
                room.getProperties().putAll(props);
                return room;
            }
        });
        final List<GcRoom> list = this.roomService.getActivedRooms();
        if (list != null && !list.isEmpty()) {
            for (final GcRoom gcRoom : list) {
                final Room room = BeanUtils.map(gcRoom, Room.class);
                final Map<String, Object> props = this.roomService.getRoomProperties(gcRoom.getId());
                room.getProperties().putAll(props);
                this.roomPojoMap.put(room.getId(),room);
            }
        }
    }
    
    @Override
    public Room get(final Serializable roomId) {
        try {
            return (Room)this.roomPojoMap.get((String) roomId);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void reload(final Serializable roomId) {
        Room room = this.get(roomId);
        final GcRoom gcRoom = this.roomService.get(GcRoom.class, roomId);
        if (gcRoom == null) {
            return;
        }
        if (room == null) {
            room = BeanUtils.map(gcRoom, Room.class);
            this.roomPojoMap.put(room.getId(),room);
        }
        else {
            BeanUtils.copy(gcRoom, room);
            if (StringUtils.isEmpty((CharSequence)gcRoom.getPsw())) {
                room.setPsw(null);
            }
            if (gcRoom.getUnDead() == null) {
                room.setUnDead(null);
            }
        }
        final Map<String, Object> props = this.roomService.getRoomProperties(gcRoom.getId());
        room.getProperties().putAll(props);
    }
    
    @Override
    public void fireUserLeft(final User user) {
        final Map<String, Room> rooms = (Map<String, Room>)this.roomPojoMap.asMap();
        if (rooms != null && !rooms.isEmpty()) {
            for (final Room room : rooms.values()) {
                room.left(user);
            }
        }
    }
    
    @Override
    public List<Room> getByType(final String type) {
        final List<Room> ls = new ArrayList<Room>();
        for (final String s : this.roomPojoMap.asMap().keySet()) {
            final Room room = this.get((Serializable)s);
            if (room.getType().equals(type)) {
                ls.add(room);
            }
        }
        return ls;
    }
    
    @Override
    public List<Room> getByCatalog(final String catalog) {
        final List<Room> ls = new ArrayList<Room>();
        for (final String s : this.roomPojoMap.asMap().keySet()) {
            final Room room = this.get((Serializable)s);
            if (StringUtils.isEmpty((CharSequence)catalog)) {
                ls.add(room);
            }
            else {
                if (!catalog.equals(room.getCatalog())) {
                    continue;
                }
                ls.add(room);
            }
        }
        return ls;
    }
    
    @Override
    public List<Room> getByCatalog(final String catalog, final int pageNo) {
        final List<Room> rooms = this.getByCatalog(catalog);
        if (rooms == null) {
            return null;
        }
        final int start = this.pageSize * (pageNo - 1);
        if (start > rooms.size() - 1) {
            return null;
        }
        if (start + this.pageSize > rooms.size() - 1) {
            return rooms.subList(start, rooms.size() - 1);
        }
        return rooms.subList(start, start + this.pageSize);
    }
    
    @Override
    public List<Room> query(final String condition) {
        return null;
    }
    
    public int getPageSize() {
        return this.pageSize;
    }
    
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }
    
    @Override
    public void delete(final String roomId) {
        this.roomPojoMap.invalidate(roomId);
    }
}
