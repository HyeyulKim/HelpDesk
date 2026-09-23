package com.helpdesk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 브라우저가 이 URL로 최초 연결을 맺음 (SockJS: 브라우저가 웹소켓을 지원 안 하면 자동으로 폴링 등으로 대체)
        registry.addEndpoint("/ws").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 서버 -> 클라이언트로 내려보내는 채널 (알림은 "/topic/..." 경로로 broadcast)
        registry.enableSimpleBroker("/topic");
        // 클라이언트 -> 서버로 보낼 때 붙는 접두사 (지금은 서버가 push만 하고 클라이언트가 메시지를 안 보내서 실사용은 안 함)
        registry.setApplicationDestinationPrefixes("/app");
    }
}