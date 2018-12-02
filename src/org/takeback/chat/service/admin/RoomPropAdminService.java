// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;
import org.springframework.stereotype.Service;
import org.takeback.core.service.MyListServiceInt;

@Service("roomPropAdminService")
public class RoomPropAdminService extends MyListServiceInt
{
    @Autowired
    RoomStore roomStore;
    
    @Transactional
    @Override
    public void save(final Map<String, Object> req) {
        super.save(req);
        final Map<String, Object> data = (Map<String, Object>) req.get("data");
        final String roomId = (String) data.get("roomId");
        this.roomStore.reload(roomId);
    }
}
