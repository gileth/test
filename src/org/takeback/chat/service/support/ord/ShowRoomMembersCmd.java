// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.support.ord;

import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import org.takeback.chat.utils.MessageUtils;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import org.takeback.chat.store.user.User;
import org.takeback.chat.store.room.Room;
import org.springframework.web.socket.WebSocketSession;
import org.takeback.chat.entity.Message;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component("showRoomMembersCmd")
public class ShowRoomMembersCmd implements Command
{
    @Override
    public void exec(final Map<String, Object> data, final Message message, final WebSocketSession session, final Room room, final User user) {
        final List<Map<String, Object>> members = (List<Map<String, Object>>)Lists.newArrayList();
        final Collection<User> users = room.getUsers().values();
        for (final User u : users) {
            final Map<String, Object> d = (Map<String, Object>)Maps.newHashMap();
            d.put("id", u.getId());
            d.put("nickName", u.getNickName());
            d.put("headImg", u.getHeadImg());
            d.put("handsUp", u.getHandsUp());
            members.add(d);
        }
        MessageUtils.sendCMD(session, "showRoomMembers", members);
    }
}
