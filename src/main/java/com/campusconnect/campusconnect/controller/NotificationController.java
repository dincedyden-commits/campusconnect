package com.campusconnect.campusconnect.controller;
import com.campusconnect.campusconnect.entity.Notification; import com.campusconnect.campusconnect.repository.NotificationRepository; import org.springframework.web.bind.annotation.*; import jakarta.servlet.http.HttpSession; import java.util.*;
@RestController @RequestMapping("/api/notifications")
public class NotificationController {
 final NotificationRepository repo; public NotificationController(NotificationRepository r){repo=r;}
 private Long me(HttpSession s){Object x=s.getAttribute("userId");if(x==null)throw new IllegalArgumentException("Log in first");return ((Number)x).longValue();}
 @GetMapping public List<Map<String,Object>> all(HttpSession s){return repo.findByUser_IdOrderByCreatedAtDesc(me(s)).stream().map(n->{Map<String,Object> out=new LinkedHashMap<>();out.put("id",n.getId());out.put("message",n.getMessage());out.put("type",n.getType()==null?"":n.getType());out.put("read",n.isReadFlag());out.put("createdAt",n.getCreatedAt());return out;}).collect(java.util.stream.Collectors.toList());}
 @GetMapping("/unread-count") public Map<String,Object> unreadCount(HttpSession s){Long uid=me(s);Map<String,Object> m=new LinkedHashMap<>();m.put("count",repo.countByUser_IdAndReadFlagFalse(uid));m.put("messages",repo.findByUser_IdOrderByCreatedAtDesc(uid).stream().filter(n->!n.isReadFlag()&&"message".equalsIgnoreCase(n.getType())).count());return m;}
 @PostMapping("/read-all") public void readAll(HttpSession s){repo.findByUser_IdOrderByCreatedAtDesc(me(s)).forEach(n->{n.setReadFlag(true);repo.save(n);});}
}