// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.websocket;

import java.util.concurrent.CopyOnWriteArraySet;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import java.util.List;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import java.util.Iterator;
import java.io.IOException;
import javax.websocket.Session;
import java.util.Set;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class Chat implements WebSocketMessageBrokerConfigurer
{
    private static final Set<Chat> connections;
    private String nickName;
    private Session session;
    
    private static void broadCast(final String message) {
        for (final Chat chat : Chat.connections) {
            try {
                synchronized (chat) {
                    chat.session.getBasicRemote().sendText(message);
                }
            }
            catch (IOException e) {
                Chat.connections.remove(chat);
                try {
                    chat.session.close();
                }
                catch (IOException ex) {}
                broadCast(String.format("System> %s %s", chat.nickName, " has bean disconnection."));
            }
        }
    }
    
    public void registerStompEndpoints(final StompEndpointRegistry stompEndpointRegistry) {
        stompEndpointRegistry.addEndpoint(new String[] { "/coordination" }).withSockJS();
    }
    
    public void configureWebSocketTransport(final WebSocketTransportRegistration webSocketTransportRegistration) {
    }
    
    public void configureClientInboundChannel(final ChannelRegistration channelRegistration) {
    }
    
    public void configureClientOutboundChannel(final ChannelRegistration channelRegistration) {
    }
    
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> list) {
    }
    
    public void addReturnValueHandlers(final List<HandlerMethodReturnValueHandler> list) {
    }
    
    public boolean configureMessageConverters(final List<MessageConverter> list) {
        return true;
    }
    
    public void configureMessageBroker(final MessageBrokerRegistry messageBrokerRegistry) {
        System.out.println("服务器启动成功");
        messageBrokerRegistry.enableSimpleBroker(new String[] { "/userChat" });
        messageBrokerRegistry.setApplicationDestinationPrefixes(new String[] { "/app" });
    }
    
    static {
        connections = new CopyOnWriteArraySet<Chat>();
    }
}
