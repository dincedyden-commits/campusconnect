package com.campusconnect.campusconnect.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import com.campusconnect.campusconnect.call.SignalingHandler;

@Configuration @EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
 private final SignalingHandler handler;
 public WebSocketConfig(SignalingHandler handler){this.handler=handler;}
 @Override public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){registry.addHandler(handler,"/ws/call").setAllowedOriginPatterns("*");}
}
