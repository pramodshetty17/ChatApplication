package com.pramod.ChatApplication.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FileUploadController {

    
    private static final Path UPLOAD_DIR = Paths.get(
            System.getProperty("user.home"), "chat-uploads"
    );

    
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {

        try {
            
            if (!Files.exists(UPLOAD_DIR)) {
                Files.createDirectories(UPLOAD_DIR);
            }

          
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "File is empty"));
            }

          
            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.contains(".")) {
                
                ext = original.substring(original.lastIndexOf('.')).toLowerCase();
               
                if (!ext.matches("\\.(jpg|jpeg|png|gif|webp|mp4|mov|avi|webm|pdf|doc|docx|xls|xlsx|ppt|pptx|txt|zip|rar)")) {
                    ext = ".bin";
                }
            }
            String filename = UUID.randomUUID().toString() + ext;
            Path dest = UPLOAD_DIR.resolve(filename);

            
            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

          
            return ResponseEntity.ok(Map.of("url", "/api/files/view/" + filename));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Upload failed: " + e.getMessage()));
        }
    }

   
    @GetMapping("/view/{filename}")
    public ResponseEntity<byte[]> view(@PathVariable String filename) {

        
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Path filePath = UPLOAD_DIR.resolve(filename);

            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] bytes = Files.readAllBytes(filePath);

           
            String mimeType = Files.probeContentType(filePath);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .header("Cache-Control", "max-age=86400, public")
                    .header("Content-Disposition", "inline; filename=\"" + filename + "\"")
                    .body(bytes);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}