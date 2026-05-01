package com.pramod.ChatApplication.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pramod.ChatApplication.entity.User;
import com.pramod.ChatApplication.repository.UserRepository;
import com.pramod.ChatApplication.service.OnlineUserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    @Autowired
    private UserRepository repo;

    @Autowired
    private OnlineUserService onlineService;

    
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(repo.findAll());
    }

   
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User saved = repo.findByName(user.getName())
                         .orElseGet(() -> repo.save(user));
        return ResponseEntity.ok(saved);
    }

    
   @GetMapping("/online")
public ResponseEntity<Set<Long>> onlineUsers() {
    return ResponseEntity.ok(onlineService.getOnlineUsers());
}
}
