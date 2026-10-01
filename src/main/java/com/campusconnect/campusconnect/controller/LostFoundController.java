package com.campusconnect.campusconnect.controller;

import com.campusconnect.campusconnect.entity.*;
import com.campusconnect.campusconnect.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController @RequestMapping("/api/lost-found")
public class LostFoundController {
    private final LostFoundItemRepository items; private final UserRepository users;
    public LostFoundController(LostFoundItemRepository i,UserRepository u){items=i;users=u;}
    private User me(HttpSession s){Object id=s.getAttribute("userId");if(id==null)throw new IllegalArgumentException("Log in first");return users.findById(((Number)id).longValue()).orElseThrow();}
    private Map<String,Object> dto(LostFoundItem x){Map<String,Object> m=new LinkedHashMap<>();m.put("id",x.getId());m.put("type",x.getType());m.put("title",x.getTitle());m.put("desc",x.getDescription());m.put("place",x.getLocation());m.put("photo",x.getPhotoUrl());m.put("createdAt",x.getCreatedAt());m.put("expiresAt",x.getExpiresAt());m.put("createdBy",x.getCreatedBy().getFullName());return m;}
    @GetMapping public List<Map<String,Object>> list(){return items.findByExpiresAtAfterOrderByCreatedAtDesc(LocalDateTime.now()).stream().map(this::dto).toList();}
    @PostMapping public Map<String,Object> create(@RequestBody Map<String,String> b,HttpSession s){User u=me(s);String type=Optional.ofNullable(b.get("type")).orElse("Lost");if(!Set.of("Lost","Found").contains(type))throw new IllegalArgumentException("Invalid item type");String title=Optional.ofNullable(b.get("title")).orElse("").trim(),desc=Optional.ofNullable(b.get("desc")).orElse("").trim();if(title.isBlank()||desc.isBlank())throw new IllegalArgumentException("Item name and description are required");LostFoundItem x=new LostFoundItem();x.setType(type);x.setTitle(title);x.setDescription(desc);x.setLocation(b.getOrDefault("place","").trim());x.setPhotoUrl(b.get("photo"));x.setCreatedBy(u);return dto(items.save(x));}
}
