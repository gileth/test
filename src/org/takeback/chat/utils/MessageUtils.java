// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.utils;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.TextMessage;
import org.takeback.util.JSONUtils;
import org.springframework.web.socket.WebSocketSession;
import org.takeback.chat.store.user.RobotUser;
import com.google.common.collect.Lists;
import org.takeback.chat.store.user.AnonymousUser;
import java.util.Map;
import java.util.Collection;
import org.takeback.chat.store.user.User;
import java.util.Iterator;
import java.util.List;
import org.takeback.chat.entity.Message;
import org.takeback.chat.store.room.Room;
import java.util.concurrent.ThreadPoolExecutor;

public class MessageUtils
{
    private static ThreadPoolExecutor threadPoolExecutor;
    
    public static List<FailedResult> broadcast(final Room room, final Message message) {
        return broadcast(room, message, false);
    }
    
    public static void broadcastDelay(final Room room, final Message message, final long delay) {
        MessageUtils.threadPoolExecutor.execute(new DelayDeliver(delay, room, message));
    }
    
    public static void broadcastDelay(final Room room, final Message message) {
        MessageUtils.threadPoolExecutor.execute(new DelayDeliver(room, message));
    }
    
    public static List<FailedResult> broadcastWithoutGuests(final Room room, final Message message) {
        return broadcast(room, message, true);
    }
    
    private static List<FailedResult> broadcast(final Room room, final Message message, final boolean isSigned) {
        final Map<Integer, User> users = room.getUsers();
        final List<FailedResult> results = broadcast(users.values().iterator(), message);
        if (!isSigned) {
            final Map<String, AnonymousUser> guests = room.getGuests();
            final List<FailedResult> resultsOfGuests = broadcast(guests.values().iterator(), message);
            if (resultsOfGuests.size() > 0) {
                results.addAll(resultsOfGuests);
            }
        }
        return results;
    }
    
    private static List<FailedResult> broadcast(final Iterator<? extends User> iterator, final Message message) {
        final List<FailedResult> results = (List<FailedResult>)Lists.newArrayList();
        while (iterator.hasNext()) {
            final User user = (User)iterator.next();
            if (user instanceof RobotUser) {
                continue;
            }
            final WebSocketSession session = user.getWebSocketSession();
            if (session == null) {
                continue;
            }
            if (!session.isOpen()) {
                iterator.remove();
            }
            else {
                final FailedResult result = send(session, message);
                if (result == null) {
                    continue;
                }
                results.add(result);
            }
        }
        return results;
    }
    
    public static FailedResult send(final WebSocketSession session, final Message message) {
        try {
            session.sendMessage((WebSocketMessage)new TextMessage((CharSequence)JSONUtils.toString(message)));
            return null;
        }
        catch (IOException e) {
            return new FailedResult(session, message, e);
        }
        catch (IllegalStateException e2) {
            return new FailedResult(session, message, e2);
        }
    }
    
    public static FailedResult send(final Integer uid, final Room room, final Message message) {
        final User user = room.getUsers().get(uid);
        if (user != null && !(user instanceof RobotUser)) {
            return send(user.getWebSocketSession(), message);
        }
        return null;
    }
    
    public static FailedResult sendCMD(final WebSocketSession session, final String cmd, final Object cmdContent) {
        final Message message = new Message(cmd, cmdContent);
        return send(session, message);
    }
    
    public static FailedResult sendCMD(final User user, final String cmd, final Object cmdContent) {
        final Message message = new Message(cmd, cmdContent);
        if (user != null && !(user instanceof RobotUser)) {
            return send(user.getWebSocketSession(), message);
        }
        return null;
    }
    
    static {
        MessageUtils.threadPoolExecutor = new ThreadPoolExecutor(50, 100, 3L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5), new ThreadPoolExecutor.AbortPolicy());
    }
    
    static class DelayDeliver implements Runnable
    {
        private long delay;
        private Room room;
        private Message message;
        
        DelayDeliver(final long delay, final Room room, final Message message) {
            this(room, message);
            this.delay = delay;
        }
        
        DelayDeliver(final Room room, final Message message) {
            this.delay = 5L;
            this.room = room;
            this.message = message;
        }
        
        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(this.delay);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            MessageUtils.broadcast(this.room, this.message);
        }
    }
}
