// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import com.google.common.collect.ImmutableMap;
import org.springframework.transaction.annotation.Transactional;
import java.util.Iterator;
import java.util.List;
import java.io.Serializable;
import org.takeback.chat.entity.GcRoomProperty;
import org.takeback.chat.utils.RoomTemplate;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;
import org.springframework.stereotype.Service;
import org.takeback.core.service.MyListService;

@Service("roomAdminService")
public class RoomAdminService extends MyListService
{
    @Autowired
    RoomStore roomStore;
    
    @Transactional
    @Override
    public void save(final Map<String, Object> req) {
        super.save(req);
        final Map<String, Object> data = req.get("data");
        final String id = data.get("id");
        if ("create".equals(req.get("cmd"))) {
            final List<GcRoomProperty> defaults = RoomTemplate.get(data.get("type").toString());
            if (defaults != null) {
                for (final GcRoomProperty prop : defaults) {
                    prop.setRoomId(id);
                    this.dao.getSession().save((Object)prop);
                }
            }
        }
        this.roomStore.reload(id);
    }
    
    @Transactional
    @Override
    public void delete(final Map<String, Object> req) {
        super.delete(req);
        final Object pkey = req.get("id");
        this.dao.executeUpdate("delete from GcRoomProperty where roomId =:roomId", (Map<String, Object>)ImmutableMap.of((Object)"roomId", pkey));
        this.roomStore.delete(pkey.toString());
    }
}
