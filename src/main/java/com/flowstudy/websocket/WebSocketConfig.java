package com.flowstudy.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 啟用記憶體 Broker。
        // /topic 用於一對多的「廣播」（例如：房間動態）
        config.enableSimpleBroker("/topic");
        
        // 客戶端發送訊息給後端時，需加上 /app 前綴
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 註冊 WebSocket 連線端點。前端將會連線至 ws://localhost:8080/ws-studyroom
        registry.addEndpoint("/ws-studyroom")
                .setAllowedOriginPatterns("*") // 允許跨域連線（開發期方便測試）
                .withSockJS();                 // 提供 SockJS 作為降級備用方案
    }
}