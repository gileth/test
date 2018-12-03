// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.websocket.core;

import org.springframework.http.server.ServletServerHttpRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor
{
    public static final String HTTP_SESSION_ATTR_NAME = "HTTP.SESSION";
    
    public boolean beforeHandshake(final ServerHttpRequest request, final ServerHttpResponse response, final WebSocketHandler wsHandler, final Map<String, Object> attributes) throws Exception {
        if (request.getHeaders().containsKey("Sec-WebSocket-Extensions")) {
            request.getHeaders().set("Sec-WebSocket-Extensions", "permessage-deflate");
        }
        final HttpSession session = this.getSession(request);
        if (session != null) {
            attributes.put("HTTP.SESSION", session);
        }
        return super.beforeHandshake(request, response, wsHandler, (Map)attributes);
    }
    
    public void afterHandshake(final ServerHttpRequest request, final ServerHttpResponse response, final WebSocketHandler wsHandler, final Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }
    
    private HttpSession getSession(final ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            final ServletServerHttpRequest serverRequest = (ServletServerHttpRequest)request;
            return serverRequest.getServletRequest().getSession(this.isCreateSession());
        }
        return null;
    }
}
