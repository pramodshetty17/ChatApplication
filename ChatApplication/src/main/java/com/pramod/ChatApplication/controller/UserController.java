package com.pramod.ChatApplication.controller;

import java.util.List;
import java.util.Map;
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


@GetMapping("/{id}")
public ResponseEntity<User> getUser(@PathVariable Long id) {
    return repo.findById(id)
               .map(ResponseEntity::ok)
               .orElse(ResponseEntity.notFound().build());
}


@PostMapping("/{id}/photo")
public ResponseEntity<User> updatePhoto(
        @PathVariable Long id,
        @RequestBody Map<String, String> body) {
    return repo.findById(id).map(user -> {
        user.setProfilePhoto(body.get("photo")); 
        return ResponseEntity.ok(repo.save(user));
    }).orElse(ResponseEntity.notFound().build());
}


@PatchMapping("/{id}/profile")
public ResponseEntity<User> updateProfile(
        @PathVariable Long id,
        @RequestBody Map<String, String> body) {
    return repo.findById(id).map(user -> {
        if (body.containsKey("name")  && body.get("name")  != null) user.setName(body.get("name"));
        if (body.containsKey("about") && body.get("about") != null) user.setAbout(body.get("about"));
        return ResponseEntity.ok(repo.save(user));
    }).orElse(ResponseEntity.notFound().build());
}
}
