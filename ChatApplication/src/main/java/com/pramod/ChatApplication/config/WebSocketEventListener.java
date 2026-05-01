package com.pramod.ChatApplication.config;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.pramod.ChatApplication.service.OnlineUserService;

@Component
public class WebSocketEventListener {

    private final OnlineUserService onlineService;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventListener(OnlineUserService onlineService,
                                   SimpMessagingTemplate messagingTemplate) {
        this.onlineService = onlineService;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Long userId = (Long) accessor.getSessionAttributes().get("userId");
        if (userId != null) {
            onlineService.userOffline(userId);
            // Push to all connected clients
            Map<String, Object> payload = new HashMap<>();
            payload.put("userId", userId);
            payload.put("online", false);
            messagingTemplate.convertAndSend("/topic/status", payload);
            System.out.println("🔴 OFFLINE (disconnect): " + userId);
        }
    }
}