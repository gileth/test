// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.websocket.listener.support;

import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import org.takeback.chat.utils.FailedResult;
import org.springframework.web.socket.CloseStatus;
import java.util.Iterator;
import java.util.List;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.entity.Message;
import org.takeback.chat.lottery.Lottery;
import org.takeback.chat.store.user.AnonymousUser;
import java.util.Date;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import org.takeback.chat.entity.GcRoomMember;
import org.takeback.chat.store.user.User;
import org.takeback.util.exception.CodedBaseRuntimeException;
import java.io.Serializable;
import org.takeback.chat.store.room.Room;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.socket.WebSocketSession;
import org.takeback.chat.service.LotteryService;
import org.takeback.chat.store.user.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;
import org.slf4j.Logger;
import org.takeback.chat.websocket.listener.TransportErrorListener;
import org.takeback.chat.websocket.listener.DisconnectListener;
import org.takeback.chat.websocket.listener.ConnectListener;

public class UserListener implements ConnectListener, DisconnectListener, TransportErrorListener
{
    private static final Logger log;
    @Autowired
    private RoomStore roomStore;
    @Autowired
    private UserStore userStore;
    @Autowired
    private LotteryService lotteryService;
    public static final String ROOMID = "roomId";
    public static final String LAST_ROOM_ID = "lastRoomId";
    
    @Override
    public void onConnect(final WebSocketSession session) {
        final String roomId = getRoomId(session);
        if (StringUtils.isEmpty(roomId)) {
            return;
        }
        final Room room = this.roomStore.get(roomId);
        if (room == null) {
            throw new CodedBaseRuntimeException(522, "room with id " + roomId + " not exists");
        }
        final Integer uid = getUid(session);
        boolean needNotice = false;
        if (uid != null) {
            final User user = this.userStore.get(uid);
            final String lastRoomId = getLastRoomId(session);
            if (lastRoomId != null) {
                final Room lastRoom = this.roomStore.get(lastRoomId);
                if (lastRoom != null) {
                    this.letUserLeftRoom(user, lastRoom, true);
                    removeAttribute(session, "lastRoomId");
                }
            }
            this.roomStore.fireUserLeft(user);
            user.setWebSocketSession(session);
            user.setHandsUp(false);
            room.join(user);
            if ("G022".equals(room.getType())) {
                final List<GcRoomMember> ls = this.lotteryService.findByProperties(GcRoomMember.class, (Map<String, Object>)ImmutableMap.of("roomId",(Object)roomId, "uid", (Object)uid));
                if (ls.size() == 0) {
                    final GcRoomMember grm = new GcRoomMember();
                    grm.setIsPartner("0");
                    grm.setRate(0.0);
                    grm.setJoinDate(new Date());
                    grm.setRoomId(room.getId());
                    grm.setNickName(user.getNickName());
                    grm.setUid(user.getId());
                    grm.setUserId(user.getUserId());
                    this.lotteryService.save(GcRoomMember.class, grm);
                }
            }
            needNotice = true;
            UserListener.log.info("user {} join in the room {}", (Object)user.getUserId(), (Object)roomId);
        }
        else {
            final User user = new AnonymousUser(session);
            room.guestJoin((AnonymousUser)user);
            UserListener.log.info("anonymous user {} join in the room {}", (Object)session.getId(), (Object)roomId);
        }
        if (needNotice) {
            final Map<String, Lottery> lotterys = (Map<String, Lottery>)room.getLotteries().asMap();
            for (final String k : lotterys.keySet()) {
                final Lottery lottery = lotterys.get(k);
                if (lottery.isOpen() && "0".equals(lottery.getStatus())) {
                    final Date createTime = lottery.getCreateTime();
                    final Room r = this.roomStore.get(lottery.getRoomId());
                    if (r.getType().startsWith("G01")) {
                        final Integer delay = Integer.valueOf(r.getProperties().get("conf_rest_time").toString());
                        if ((new Date().getTime() - createTime.getTime()) / 1000L < delay + 1) {
                            continue;
                        }
                    }
                    final Integer senderId = lottery.getSender();
                    Message lotteryMsg;
                    if (senderId == null || senderId == 0) {
                        lotteryMsg = new Message("RED_SYS", 0, lottery);
                        lotteryMsg.setHeadImg("img/avatar.png");
                        lotteryMsg.setNickName("\u7cfb\u7edf");
                    }
                    else {
                        final User sender = this.userStore.get(senderId);
                        lotteryMsg = new Message("RED", senderId, lottery);
                        lotteryMsg.setNickName(sender.getNickName());
                        lotteryMsg.setHeadImg(sender.getHeadImg());
                    }
                    MessageUtils.send(uid, room, lotteryMsg);
                }
            }
        }
    }
    
    @Override
    public void onDisconnect(final WebSocketSession session, final CloseStatus closeStatus) {
        final String roomId = getRoomId(session);
        if (StringUtils.isEmpty(roomId)) {
            return;
        }
        final Room room = this.roomStore.get(roomId);
        if (room == null) {
            throw new CodedBaseRuntimeException(522, "room with id " + roomId + " not exists");
        }
        final Integer uid = getUid(session);
        if (uid != null) {
            final User user = this.userStore.get(uid);
            this.letUserLeftRoom(user, room, true);
            removeAttribute(session, "roomId");
        }
        else {
            final User user = new AnonymousUser(session);
            room.guestLeft((AnonymousUser)user);
            UserListener.log.info("anonymous user {} left the room {}", (Object)session.getId(), (Object)roomId);
        }
    }
    
    private void letUserLeftRoom(final User user, final Room room, final boolean broadcast) {
        room.left(user);
        UserListener.log.info("user {} left the room {}", (Object)user.getUserId(), (Object)room.getId());
        if (broadcast) {
            final List<FailedResult> results = MessageUtils.broadcast(room, new Message("TXT_SYS", user.getId(), user.getNickName() + " \u79bb\u5f00\u4e86\u623f\u95f4."));
            if (results.size() > 0) {}
        }
    }
    
    @Override
    public void onTransportError(final WebSocketSession session, final Throwable throwable) {
        this.onDisconnect(session, null);
        if (session.isOpen()) {
            try {
                session.close();
            }
            catch (IOException e) {
                throw new CodedBaseRuntimeException(500, "try to close session failed.");
            }
        }
    }
    
    protected static String getRoomId(final WebSocketSession session) {
        final String roomId = (String)getSessionAttribute(session, "roomId");
        return roomId;
    }
    
    protected static Integer getUid(final WebSocketSession session) {
        final HttpSession httpSession = (HttpSession) session.getAttributes().get("HTTP.SESSION");
        if (httpSession == null) {
            return null;
        }
        try {
            return (Integer)httpSession.getAttribute("$uid");
        }
        catch (IllegalStateException e) {
            return null;
        }
    }
    
    protected static String getLastRoomId(final WebSocketSession session) {
        return (String)getSessionAttribute(session, "lastRoomId");
    }
    
    protected static Object getSessionAttribute(final WebSocketSession session, final String name) {
        Object value = session.getAttributes().get(name);
        if (value == null) {
            final HttpSession httpSession = (HttpSession) session.getAttributes().get("HTTP.SESSION");
            try {
                if (httpSession != null) {
                    value = httpSession.getAttribute(name);
                }
            }
            catch (IllegalStateException ex) {}
        }
        return value;
    }
    
    protected static void removeAttribute(final WebSocketSession session, final String name) {
        session.getAttributes().remove(name);
        final HttpSession httpSession = (HttpSession) session.getAttributes().get("HTTP.SESSION");
        try {
            if (httpSession != null) {
                httpSession.removeAttribute(name);
            }
        }
        catch (IllegalStateException ex) {}
    }
    
    static {
        log = LoggerFactory.getLogger((Class)UserListener.class);
    }
}
