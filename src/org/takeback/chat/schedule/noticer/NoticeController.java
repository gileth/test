// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.schedule.noticer;

import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;

public class NoticeController
{
    @Autowired
    RoomStore roomStore;
    private String rooms;
    
    public String getRooms() {
        return this.rooms;
    }
    
    public void setRooms(final String rooms) {
        this.rooms = rooms;
    }
    
    public void init() {
        final String[] roomArr = this.rooms.split(",");
        final AngryNotice an = new AngryNotice(roomArr, this.roomStore);
        final CuteNotice cn = new CuteNotice(roomArr, this.roomStore);
        an.start();
        cn.start();
    }
}
