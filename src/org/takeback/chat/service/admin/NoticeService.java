// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import org.springframework.transaction.annotation.Transactional;
import java.util.Iterator;
import java.util.List;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.entity.Message;
import org.takeback.chat.store.room.Room;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;
import org.springframework.stereotype.Service;
import org.takeback.core.service.MyListServiceInt;

@Service("noticeService")
public class NoticeService extends MyListServiceInt
{
    @Autowired
    private RoomStore roomStore;
    
    @Transactional
    @Override
    public void save(final Map<String, Object> req) {
        super.save(req);
        final Map<String, Object> data = (Map<String, Object>) req.get("data");
        System.out.println(data);
        final String content = (String) data.get("content");
        final String meg = "<span style='color:#B22222'>系统消息：" + content + " </span>";
        final List<Room> rms = this.roomStore.getByCatalog(null);
        for (final Room r : rms) {
            final Message msg = new Message("TXT_SYS", 0, meg);
            MessageUtils.broadcast(r, msg);
        }
    }
}
