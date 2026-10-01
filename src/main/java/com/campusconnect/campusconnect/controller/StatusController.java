package com.campusconnect.campusconnect.controller;

import com.campusconnect.campusconnect.entity.Status;
import com.campusconnect.campusconnect.entity.User;
import com.campusconnect.campusconnect.repository.StatusRepository;
import com.campusconnect.campusconnect.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/statuses")
public class StatusController {
    private final StatusRepository statuses; private final UserRepository users;
    public StatusController(StatusRepository s, UserRepository u){statuses=s;users=u;}
    private User me(HttpSession s){Object id=s.getAttribute("userId"); if(id==null) throw new IllegalArgumentException("Log in first"); return users.findById(((Number)id).longValue()).orElseThrow();}
    private Map<String,Object> dto(Status x){Map<String,Object> m=new LinkedHashMap<>();m.put("id",x.getId());m.put("userId",x.getUser().getId());m.put("username",x.getUser().getUsername());m.put("profilePictureUrl",x.getUser().getProfilePictureUrl());m.put("mediaUrl",x.getMediaUrl());m.put("mediaType",x.getMediaType());m.put("caption",x.getCaption());m.put("createdAt",x.getCreatedAt());m.put("expiresAt",x.getExpiresAt());return m;}
    @GetMapping public List<Map<String,Object>> list(){return statuses.findByExpiresAtAfterOrderByCreatedAtDesc(LocalDateTime.now()).stream().map(this::dto).toList();}
    @PostMapping public Map<String,Object> create(@RequestBody Map<String,String> b,HttpSession s){String url=Optional.ofNullable(b.get("mediaUrl")).orElse("").trim();String caption=Optional.ofNullable(b.get("caption")).orElse("").trim();if(url.isBlank()&&caption.isBlank())throw new IllegalArgumentException("Write a status or choose media");if(caption.length()>280)throw new IllegalArgumentException("Status text is limited to 280 characters");Status x=new Status();x.setUser(me(s));x.setMediaUrl(url);x.setMediaType(Optional.ofNullable(b.get("mediaType")).orElse("image"));x.setCaption(caption);return dto(statuses.save(x));}
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id,HttpSession s){Status x=statuses.findById(id).orElseThrow();if(!x.getUser().getId().equals(me(s).getId()))throw new IllegalArgumentException("Only the owner can delete this status");statuses.delete(x);}
}
