// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.support.ord;

import java.util.Iterator;
import java.util.List;
import org.takeback.chat.utils.MessageUtils;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.takeback.chat.entity.PcEggLog;
import org.takeback.chat.store.pcegg.PcEggStore;
import org.takeback.chat.store.user.User;
import org.takeback.chat.store.room.Room;
import org.springframework.web.socket.WebSocketSession;
import org.takeback.chat.entity.Message;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.PcEggService;
import org.springframework.stereotype.Component;

@Component("latestTermCmd")
public class LatestTermCmd implements Command
{
    @Autowired
    private PcEggService eggService;
    
    @Override
    public void exec(final Map<String, Object> data, final Message message, final WebSocketSession session, final Room room, final User user) {
        PcEggLog egg = PcEggStore.getStore().getLastest();
        final List<PcEggLog> logs = PcEggStore.getStore().getCache();
        if (egg == null && logs.size() > 0) {
            egg = logs.get(0);
        }
        if (egg != null) {
            String simpleWord = "";
            for (final PcEggLog pel : logs) {
                simpleWord = simpleWord + " " + (StringUtils.isNotEmpty((CharSequence)pel.getLucky()) ? pel.getLucky() : "?");
            }
            simpleWord = ((simpleWord.length() > 1) ? simpleWord.substring(1) : simpleWord);
            final long l = egg.getExpireTime().getTime() - System.currentTimeMillis();
            MessageUtils.sendCMD(session, "latestTerm", ImmutableMap.of("termId", egg.getId(), "expireTime", egg.getExpireTime(), "remainSeconds", (int)Math.floor(l / 1000L), "simpleWord", simpleWord, "logs", logs));
        }
    }
}
