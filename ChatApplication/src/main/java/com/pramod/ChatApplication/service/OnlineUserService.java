package com.pramod.ChatApplication.service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class OnlineUserService {

    private final Set<Long> onlineUsers = ConcurrentHashMap.newKeySet();

public void userOnline(Long userId) {
    onlineUsers.add(userId);
}

public void userOffline(Long userId) {
    onlineUsers.remove(userId);
}

public Set<Long> getOnlineUsers() {
    return onlineUsers;
}
}