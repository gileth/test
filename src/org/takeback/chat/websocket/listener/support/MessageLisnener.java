// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.websocket.listener.support;

import org.slf4j.LoggerFactory;
import org.takeback.chat.service.admin.SystemConfigService;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.entity.Message;
import org.takeback.util.JSONUtils;
import org.takeback.chat.store.user.User;
import org.takeback.util.exception.CodedBaseRuntimeException;
import java.io.Serializable;
import org.takeback.chat.store.room.Room;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.takeback.chat.service.SystemService;
import org.takeback.chat.service.support.OrdMessageProcessor;
import org.takeback.chat.service.support.RedMessageProcessor;
import org.takeback.chat.service.support.TxtMessageProcessor;
import org.takeback.chat.store.user.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;
import org.slf4j.Logger;
import org.takeback.chat.websocket.listener.MessageReceiveListener;

public class MessageLisnener implements MessageReceiveListener
{
    private static final Logger log;
    @Autowired
    private RoomStore roomStore;
    @Autowired
    private UserStore userStore;
    @Autowired
    private TxtMessageProcessor txtMessageProcessor;
    @Autowired
    private RedMessageProcessor redMessageProcessor;
    @Autowired
    private OrdMessageProcessor ordMessageProcessor;
    @Autowired
    private SystemService systemService;
    
    @Override
    public void onMessageReceive(final WebSocketSession session, final WebSocketMessage<?> webSocketMessage) {
        final Integer uid = UserListener.getUid(session);
        final String roomId = UserListener.getRoomId(session);
        final Room room = this.roomStore.get(roomId);
        if (room == null) {
            throw new CodedBaseRuntimeException(522, "room with id " + roomId + " not exists");
        }
        final Message message = this.processMessage(webSocketMessage);
        final String messageType = message.getType();
        if (!messageType.equals("CMD") && !this.isSigned(session)) {
            return;
        }
        if (messageType.equals("TXT") && this.isCloseTalk(session)) {
            return;
        }
        User user = null;
        if (uid != null) {
            user = this.userStore.get(uid);
            message.setSender(user.getId());
            message.setNickName(user.getNickName());
            message.setHeadImg(user.getHeadImg());
        }
        MessageLisnener.log.info("receive msg {}", JSONUtils.toString(message));
        MessageLisnener.log.info("msg messageType {} uid {}",messageType,uid);
        final String s = messageType;
        switch (s) {
            case "TXT": {
                this.txtMessageProcessor.process(message, session, room, user);
                break;
            }
            case "RED": {
                this.redMessageProcessor.process(message, session, room, user);
                break;
            }
            case "CMD":
            	System.out.println("messageType CMD");
            	break;  
            case "ORD": {
                this.ordMessageProcessor.process(message, session, room, user);
                break;
            }
        }
    }
    
    private Message processMessage(final WebSocketMessage<?> webSocketMessage) {
        try {
            return JSONUtils.parse((String)webSocketMessage.getPayload(), Message.class);
        }
        catch (Exception e) {
            throw new CodedBaseRuntimeException(501, "process msg " + webSocketMessage.getPayload() + " failed", e);
        }
    }
    
    protected boolean isSigned(final WebSocketSession session) {
        final Integer uid = UserListener.getUid(session);
        if (uid == null) {
            MessageUtils.sendCMD(session, "notLogin", "注册用户才可以发言和参与游戏");
            return false;
        }
        return true;
    }
    
    protected boolean isCloseTalk(final WebSocketSession session) {
        this.systemService.getProxyConfig();
        final String confTalk = SystemConfigService.getInstance().getValue("conf_talk");
        if ("0".equals(confTalk)) {
            MessageUtils.sendCMD(session, "alert", "禁止发言");
            return true;
        }
        return false;
    }
    
    static {
        log = LoggerFactory.getLogger((Class)MessageLisnener.class);
    }
}
