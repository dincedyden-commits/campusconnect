package com.campusconnect.campusconnect.controller;

import com.campusconnect.campusconnect.storage.ObjectStorageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
    private final Optional<ObjectStorageService> storage;
    @Value("${storage.enabled:false}") private boolean storageEnabled;
    public UploadController(Optional<ObjectStorageService> storage){this.storage=storage;}
    @PostMapping
    public Map<String,String> upload(@RequestParam("file") MultipartFile file,HttpSession session) throws Exception {
        if(session.getAttribute("userId")==null) throw new IllegalArgumentException("Log in first");
        if(file==null||file.isEmpty()) throw new IllegalArgumentException("Choose a file");
        if(file.getSize()>200L*1024*1024) throw new IllegalArgumentException("File is larger than 200MB");
        String type=file.getContentType()==null?"application/octet-stream":file.getContentType().toLowerCase();
        if(!(type.startsWith("image/")||type.startsWith("video/")||type.startsWith("audio/"))) throw new IllegalArgumentException("Unsupported media type");
        if(storageEnabled && storage.isPresent()){var saved=storage.get().upload(file);return Map.of("url",saved.url(),"key",saved.key(),"name",saved.key(),"type",saved.contentType());}
        String ext="";String original=file.getOriginalFilename();if(original!=null&&original.contains("."))ext=original.substring(original.lastIndexOf('.')).replaceAll("[^A-Za-z0-9.]","");
        String name=UUID.randomUUID()+ext;Path dir=Paths.get("uploads");Files.createDirectories(dir);Files.copy(file.getInputStream(),dir.resolve(name),StandardCopyOption.REPLACE_EXISTING);
        return Map.of("url","/uploads/"+name,"key",name,"name",name,"type",type);
    }
}
