package com.campusconnect.campusconnect.controller;

import com.campusconnect.campusconnect.entity.*;
import com.campusconnect.campusconnect.repository.*;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    final MessageRepository messages; final UserRepository users; final NotificationRepository notifications;
    final ListingRepository listings; final BlockRepository blocks;

    public MessageController(MessageRepository m,UserRepository u,NotificationRepository n,ListingRepository l,BlockRepository b){
        messages=m; users=u; notifications=n; listings=l; blocks=b;
    }

    private Long me(HttpSession s){
        Object x=s.getAttribute("userId");
        if(x==null) throw new IllegalArgumentException("Log in first");
        return ((Number)x).longValue();
    }

    private boolean hiddenFor(Message m, Long uid){
        return m.getSender().getId().equals(uid) ? m.isDeletedBySender() : m.isDeletedByRecipient();
    }

    private Map<String,Object> dto(Message m){
        Map<String,Object> out=new LinkedHashMap<>();
        out.put("id",m.getId());
        out.put("senderId",m.getSender().getId());
        out.put("senderUsername",m.getSender().getUsername());
        out.put("recipientId",m.getRecipient().getId());
        out.put("recipientUsername",m.getRecipient().getUsername());
        out.put("content",m.getContent());
        out.put("viewOnce",m.isViewOnce()); out.put("viewed",m.isViewed());
        out.put("createdAt",m.getCreatedAt());
        out.put("productId",m.getProductId()); out.put("productTitle",m.getProductTitle());
        out.put("productImageUrl",m.getProductImageUrl()); out.put("productPrice",m.getProductPrice());
        return out;
    }

    @GetMapping("/with/{userId}")
    public List<Map<String,Object>> conversation(@PathVariable Long userId,HttpSession s){
        Long me=me(s);
        return messages.findBySender_IdOrRecipient_IdOrderByCreatedAtDesc(me,me).stream()
            .filter(m -> ((m.getSender().getId().equals(me)&&m.getRecipient().getId().equals(userId)) ||
                          (m.getSender().getId().equals(userId)&&m.getRecipient().getId().equals(me))))
            .filter(m -> !hiddenFor(m,me))
            .sorted(Comparator.comparing(Message::getCreatedAt))
            .map(this::dto).toList();
    }

    @GetMapping("/conversations")
    public List<Map<String,Object>> conversations(HttpSession s){
        Long me=me(s);
        Map<Long,Message> latest=new LinkedHashMap<>();
        messages.findBySender_IdOrRecipient_IdOrderByCreatedAtDesc(me,me).stream()
            .filter(m -> !hiddenFor(m,me))
            .forEach(m -> {
                Long other=m.getSender().getId().equals(me)?m.getRecipient().getId():m.getSender().getId();
                latest.putIfAbsent(other,m);
            });
        return latest.values().stream().map(m -> {
            Long other=m.getSender().getId().equals(me)?m.getRecipient().getId():m.getSender().getId();
            User u=m.getSender().getId().equals(me)?m.getRecipient():m.getSender();
            Map<String,Object> out=new LinkedHashMap<>();
            out.put("userId",other); out.put("username",u.getUsername());
            out.put("fullName",u.getFullName()); out.put("profilePictureUrl",u.getProfilePictureUrl());
            out.put("lastMessage",m.getContent()); out.put("createdAt",m.getCreatedAt());
            out.put("productId",m.getProductId()); out.put("productTitle",m.getProductTitle());
            out.put("productImageUrl",m.getProductImageUrl()); out.put("productPrice",m.getProductPrice());
            return out;
        }).toList();
    }

    @PostMapping("/to/{userId}")
    public Map<String,Object> send(@PathVariable Long userId,@RequestBody Map<String,Object> b,HttpSession s){
        Long sid=me(s);
        if(sid.equals(userId)) throw new IllegalArgumentException("You cannot message yourself");
        String text=String.valueOf(b.getOrDefault("content","")).trim();
        if(text.isEmpty()) throw new IllegalArgumentException("Message cannot be empty");
        User a=users.findById(sid).orElseThrow(), r=users.findById(userId).orElseThrow();
        if(blocks.existsByBlocker_IdAndBlocked_Id(sid,userId)||blocks.existsByBlocker_IdAndBlocked_Id(userId,sid))
            throw new IllegalArgumentException("Messaging is unavailable because one of you has blocked the other");
        Message m=new Message(); m.setSender(a); m.setRecipient(r); m.setContent(text);
        m.setViewOnce(Boolean.parseBoolean(String.valueOf(b.getOrDefault("viewOnce",false)))); m.setViewed(false);
        Object productIdRaw=b.get("productId");
        if(productIdRaw!=null && !String.valueOf(productIdRaw).isBlank()){
            Long productId=Long.valueOf(String.valueOf(productIdRaw));
            Listing product=listings.findById(productId).orElseThrow();
            m.setProductId(product.getId()); m.setProductTitle(product.getTitle());
            m.setProductImageUrl(product.getImageUrl()); m.setProductPrice(product.getPrice());
        }
        Message saved=messages.save(m);
        Notification n=new Notification(); n.setUser(r); n.setType("message");
        n.setMessage("@"+a.getUsername()+" sent you a message"); notifications.save(n);
        return dto(saved);
    }

    @PutMapping("/{id}")
    public Map<String,Object> edit(@PathVariable Long id,@RequestBody Map<String,String> b,HttpSession s){
        Message m=messages.findById(id).orElseThrow(); Long me=me(s);
        if(!m.getSender().getId().equals(me)) throw new IllegalArgumentException("Only the sender can edit this message");
        if(m.isDeletedBySender()) throw new IllegalArgumentException("Deleted messages cannot be edited");
        if(Duration.between(m.getCreatedAt(),LocalDateTime.now()).toMinutes()>20)
            throw new IllegalArgumentException("Messages can only be edited within 20 minutes");
        String text=b.getOrDefault("content","").trim();
        if(text.isEmpty()) throw new IllegalArgumentException("Message cannot be empty");
        m.setContent(text); return dto(messages.save(m));
    }

    @DeleteMapping("/{id}")
    public void softDelete(@PathVariable Long id,@RequestParam(defaultValue="me") String mode,HttpSession s){
        Message m=messages.findById(id).orElseThrow(); Long uid=me(s);
        if(!m.getSender().getId().equals(uid) && !m.getRecipient().getId().equals(uid))
            throw new IllegalArgumentException("Not allowed");

        // Deliberately no messages.delete(...). This is always a soft delete.
        LocalDateTime now=LocalDateTime.now();
        m.setDeletedByUser(true); m.setDeletedAt(now);
        if(m.getSender().getId().equals(uid)){
            m.setDeletedBySender(true); m.setDeletedAtSender(now);
        }else{
            m.setDeletedByRecipient(true); m.setDeletedAtRecipient(now);
        }
        messages.save(m);
    }

    @PostMapping("/{id}/view")
    public Map<String,Object> view(@PathVariable Long id,HttpSession s){
        Long me=me(s); Message m=messages.findById(id).orElseThrow();
        if(hiddenFor(m,me)) throw new IllegalArgumentException("Message is unavailable");
        if(!m.getRecipient().getId().equals(me)||!m.isViewOnce()) return dto(m);
        if(m.isViewed()) throw new IllegalArgumentException("This view-once message has already been opened");
        m.setViewed(true); Map<String,Object> out=new HashMap<>(dto(m)); messages.save(m); return out;
    }
}
