// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.store.room;

import org.takeback.chat.store.user.User;
import java.util.List;
import org.takeback.chat.store.Store;

public interface RoomStore extends Store<Room>
{
    List<Room> getByType(final String p0);
    
    List<Room> getByCatalog(final String p0);
    
    List<Room> getByCatalog(final String p0, final int p1);
    
    List<Room> query(final String p0);
    
    void delete(final String p0);
    
    void fireUserLeft(final User p0);
}
