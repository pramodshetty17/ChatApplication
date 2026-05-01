package com.pramod.ChatApplication.websocket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import com.pramod.ChatApplication.entity.Message;
import com.pramod.ChatApplication.service.MessageService;
import com.pramod.ChatApplication.service.OnlineUserService;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService service;

    @Autowired
private OnlineUserService onlineService;


   @MessageMapping("/send")
public void send(Message message) {


    if (message.getContent() != null || "LOCATION".equals(message.getType())) {
        service.saveMessage(message);
    }

   
    messagingTemplate.convertAndSendToUser(
            String.valueOf(message.getReceiverId()),
            "/queue/messages",
            message
    );

    
    messagingTemplate.convertAndSendToUser(
            String.valueOf(message.getSenderId()),
            "/queue/messages",
            message
    );
}

    @MessageMapping("/typing")
    public void typing(Message message) {
        messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getReceiverId()),
                "/queue/typing",
                (message.getSenderName() != null ? message.getSenderName() : "Someone") + " is typing..."
        );
    }
  

@MessageMapping("/userOffline")
public void userOffline(Message message, SimpMessageHeaderAccessor headerAccessor) {
    onlineService.userOffline(message.getSenderId());
    
    broadcastStatus(message.getSenderId(), false);
    System.out.println("🔴 OFFLINE (explicit): " + message.getSenderId());
}

@MessageMapping("/addUser")
public void addUser(Message message, SimpMessageHeaderAccessor headerAccessor) {
    headerAccessor.getSessionAttributes().put("userId", message.getSenderId());
    onlineService.userOnline(message.getSenderId());
    broadcastStatus(message.getSenderId(), true);
    System.out.println("🟢 ONLINE: " + message.getSenderId());
}

private void broadcastStatus(Long userId, boolean online) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("userId", userId);
    payload.put("online", online);
    messagingTemplate.convertAndSend("/topic/status", payload);
}



    @GetMapping("/history")
    public ResponseEntity<List<Message>> getChat(
            @RequestParam long senderId,
            @RequestParam long receiverId) {
        return ResponseEntity.ok(service.getChat(senderId, receiverId));
    }

    
}


