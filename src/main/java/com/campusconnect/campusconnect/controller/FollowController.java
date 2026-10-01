package com.campusconnect.campusconnect.controller;
import com.campusconnect.campusconnect.entity.*; import com.campusconnect.campusconnect.repository.*; import org.springframework.web.bind.annotation.*; import jakarta.servlet.http.HttpSession; import java.util.*;
@RestController @RequestMapping("/api/follows")
public class FollowController {
 final FollowRepository follows; final UserRepository users; final NotificationRepository notifications;
 public FollowController(FollowRepository f,UserRepository u,NotificationRepository n){follows=f;users=u;notifications=n;}
 private Long me(HttpSession s){Object x=s.getAttribute("userId"); if(x==null) throw new IllegalArgumentException("Log in first"); return ((Number)x).longValue();}
 @PostMapping("/{id}") public Map<String,Object> follow(@PathVariable Long id,HttpSession s){Long a=me(s); if(a.equals(id)) throw new IllegalArgumentException("You cannot follow yourself"); User f=users.findById(a).orElseThrow(); User t=users.findById(id).orElseThrow(); if(!follows.existsByFollower_IdAndFollowing_Id(a,id)){Follow x=new Follow();x.setFollower(f);x.setFollowing(t);follows.save(x);Notification n=new Notification();n.setUser(t);n.setType("follow");n.setMessage("@"+f.getUsername()+" followed you");notifications.save(n);} return status(a,id);}
 @DeleteMapping("/{id}") public Map<String,Object> unfollow(@PathVariable Long id,HttpSession s){Long a=me(s); follows.findByFollower_IdAndFollowing_Id(a,id).ifPresent(follows::delete); return status(a,id);}
 @GetMapping("/status/{id}") public Map<String,Object> status(@PathVariable Long id,HttpSession s){Long a=me(s);return status(a,id);}
 @GetMapping("/followers/{id}") public Map<String,Object> followers(@PathVariable Long id){return Map.of("count",follows.countByFollowing_Id(id));}
 @GetMapping("/following/{id}") public Map<String,Object> following(@PathVariable Long id){return Map.of("count",follows.countByFollower_Id(id));}
 private Map<String,Object> status(Long a,Long b){return Map.of("following",follows.existsByFollower_IdAndFollowing_Id(a,b),"followers",follows.countByFollowing_Id(b),"followingCount",follows.countByFollower_Id(b));}
}