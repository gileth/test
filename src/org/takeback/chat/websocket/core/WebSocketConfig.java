// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.websocket.core;

import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer
{
    private String uri;
    
    public WebSocketConfig() {
        this.uri = "chat";
    }
    
    public WebSocketConfig(final String uri) {
        this.uri = "chat";
        this.uri = uri;
    }
    
    public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
        registry.addHandler(this.chatCoreWebSocketHandler(), new String[] { "/" + this.uri }).addInterceptors(new HandshakeInterceptor[] { this.httpSessionHandshakeInterceptor() }).setAllowedOrigins(new String[] { "*" });
    }
    
    @Bean(name = { "chatCoreWebSocketHandler" })
    public WebSocketHandler chatCoreWebSocketHandler() {
        return (WebSocketHandler)new ChatCoreWebSocketHandler();
    }
    
    @Bean(name = { "handshakeInterceptor" })
    public HttpSessionHandshakeInterceptor httpSessionHandshakeInterceptor() {
        return new org.takeback.chat.websocket.core.HandshakeInterceptor();
    }
    
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        final ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        return container;
    }
}
