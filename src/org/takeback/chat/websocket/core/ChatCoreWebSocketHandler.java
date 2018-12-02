// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.websocket.core;

import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.takeback.util.exception.CodedBaseRuntimeException;
import org.springframework.web.socket.WebSocketMessage;
import java.util.Iterator;
import org.takeback.util.exception.CodedBase;
import org.springframework.web.socket.WebSocketSession;
import org.takeback.chat.websocket.listener.TransportErrorListener;
import org.takeback.chat.websocket.listener.MessageReceiveListener;
import org.takeback.chat.websocket.listener.DisconnectListener;
import javax.annotation.Resource;
import org.takeback.chat.websocket.listener.ConnectListener;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.web.socket.WebSocketHandler;

public class ChatCoreWebSocketHandler implements WebSocketHandler
{
    private static final Logger log;
    @Resource
    private List<ConnectListener> connectListeners;
    @Resource
    private List<DisconnectListener> disconnectListeners;
    @Resource
    private List<MessageReceiveListener> messageReceiveListeners;
    @Resource
    private List<TransportErrorListener> transportErrorListeners;
    
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
        if (this.connectListeners != null) {
            for (final ConnectListener connectListener : this.connectListeners) {
                try {
                    connectListener.onConnect(session);
                }
                catch (Exception e) {
                    if (e instanceof CodedBase) {
                        ChatCoreWebSocketHandler.log.error("{} connect failed. code:{} msg:{}", new Object[] { session, ((CodedBase)e).getCode(), e.getMessage() });
                    }
                    else {
                        ChatCoreWebSocketHandler.log.error("Connect failed", (Throwable)e);
                    }
                }
            }
        }
    }
    
    public void handleMessage(final WebSocketSession session, final WebSocketMessage<?> webSocketMessage) throws Exception {
        if (this.messageReceiveListeners != null) {
            for (final MessageReceiveListener messageReceiveListener : this.messageReceiveListeners) {
                try {
                    messageReceiveListener.onMessageReceive(session, webSocketMessage);
                }
                catch (CodedBaseRuntimeException e) {
                    ChatCoreWebSocketHandler.log.error("{} message received error. code:{} msg:{} body:{}", new Object[] { session, e.getCode(), e.getMessage(), webSocketMessage });
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
    
    public void handleTransportError(final WebSocketSession session, final Throwable throwable) throws Exception {
        if (this.transportErrorListeners != null) {
            for (final TransportErrorListener transportErrorListener : this.transportErrorListeners) {
                transportErrorListener.onTransportError(session, throwable);
            }
        }
    }
    
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus closeStatus) throws Exception {
        if (this.disconnectListeners != null) {
            for (final DisconnectListener disconnectListener : this.disconnectListeners) {
                try {
                    disconnectListener.onDisconnect(session, closeStatus);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public boolean supportsPartialMessages() {
        return false;
    }
    
    static {
        log = LoggerFactory.getLogger((Class)ChatCoreWebSocketHandler.class);
    }
}
