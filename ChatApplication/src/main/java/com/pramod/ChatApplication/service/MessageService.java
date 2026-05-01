package com.pramod.ChatApplication.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pramod.ChatApplication.entity.Message;
import com.pramod.ChatApplication.repository.MessageRepository;

@Service
public class MessageService {

    @Autowired
    private MessageRepository repo;

    public Message saveMessage(Message message) {
        message.setTimestamp(LocalDateTime.now());
        return repo.save(message);
    }

    public List<Message> getChat(Long user1, Long user2) {
        return repo.getChat(user1, user2);
    }
}
