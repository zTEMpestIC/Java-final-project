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
        // 啟用簡單的記憶體 Message Broker (Phase 3 可無縫替換為 Redis / RabbitMQ)
        // /topic 用於廣播 (如房間狀態)，/queue 用於點對點 (如拍一拍)
        config.enableSimpleBroker("/topic", "/queue");
        // 客戶端發送訊息的前綴
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 前端連線的端點，啟用 SockJS 備用方案
        registry.addEndpoint("/ws-studyroom").setAllowedOriginPatterns("*").withSockJS();
    }
}