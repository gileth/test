// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.store.room;

import java.util.Map;
import org.takeback.util.BeanUtils;
import org.takeback.util.exception.CodedBaseException;
import java.io.Serializable;
import org.takeback.chat.entity.GcRoom;
import com.google.common.cache.CacheLoader;

class DefaultRoomStore$1 extends CacheLoader<String, Room> {
    public Room load(final String s) throws Exception {
        final GcRoom gcRoom = DefaultRoomStore.access$000(DefaultRoomStore.this).get(GcRoom.class, s);
        if (gcRoom == null) {
            throw new CodedBaseException(530, "room " + s + " not exists");
        }
        final Room room = BeanUtils.map(gcRoom, Room.class);
        final Map<String, Object> props = DefaultRoomStore.access$000(DefaultRoomStore.this).getRoomProperties(gcRoom.getId());
        room.getProperties().putAll(props);
        return room;
    }
}