package com.campusconnect.campusconnect.controller;

import com.campusconnect.campusconnect.entity.*;
import com.campusconnect.campusconnect.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/community-messages")
public class CommunityMessageController {
 private final CommunityMessageRepository messages; private final CommunityRepository communities; private final CommunityMemberRepository members; private final UserRepository users; private final NotificationRepository notifications;
 public CommunityMessageController(CommunityMessageRepository m,CommunityRepository c,CommunityMemberRepository cm,UserRepository u,NotificationRepository n){messages=m;communities=c;members=cm;users=u;notifications=n;}
 private Long me(HttpSession s){Object id=s.getAttribute("userId");if(id==null)throw new IllegalArgumentException("Log in first");return ((Number)id).longValue();}
 private Community community(Long id){return communities.findById(id).orElseThrow(()->new IllegalArgumentException("Community not found"));}
 private CommunityMember member(Long cid,Long uid){return members.findByCommunityIdAndUserId(cid,uid).orElseThrow(()->new IllegalArgumentException("You are not a member"));}
 private Map<String,Object> dto(CommunityMessage m){Map<String,Object> x=new LinkedHashMap<>();x.put("id",m.getId());x.put("communityId",m.getCommunity().getId());x.put("senderId",m.getSender().getId());x.put("senderUsername",m.getSender().getUsername());x.put("content",m.getContent());x.put("createdAt",m.getCreatedAt());return x;}
 @GetMapping("/{communityId}") public List<Map<String,Object>> list(@PathVariable Long communityId,HttpSession s){member(communityId,me(s));return messages.findByCommunity_IdOrderByCreatedAtAsc(communityId).stream().map(this::dto).toList();}
 @PostMapping("/{communityId}") public Map<String,Object> send(@PathVariable Long communityId,@RequestBody Map<String,String> b,HttpSession s){Long uid=me(s);Community community=community(communityId);CommunityMember currentMember=member(communityId,uid);if("CHANNEL".equalsIgnoreCase(community.getType())&&!("OWNER".equalsIgnoreCase(currentMember.getRole())||"ADMIN".equalsIgnoreCase(currentMember.getRole())))throw new IllegalArgumentException("Only channel admins can publish updates");String content=Optional.ofNullable(b.get("content")).orElse("").trim();if(content.isBlank())throw new IllegalArgumentException("Message cannot be empty");if(content.length()>4000)throw new IllegalArgumentException("Message is too long");CommunityMessage m=new CommunityMessage();m.setCommunity(community(communityId));m.setSender(users.findById(uid).orElseThrow());m.setContent(content);Map<String,Object> out=dto(messages.save(m));for(CommunityMember cm:members.findByCommunityIdOrderByJoinedAtAsc(communityId)){if(!cm.getUser().getId().equals(uid)){Notification n=new Notification();n.setUser(cm.getUser());n.setType("message");n.setMessage("@"+m.getSender().getUsername()+" posted in "+community.getName());notifications.save(n);}}return out;}
 @PutMapping("/{id}") public Map<String,Object> edit(@PathVariable Long id,@RequestBody Map<String,String> b,HttpSession s){CommunityMessage m=messages.findById(id).orElseThrow();Long uid=me(s);if(!m.getSender().getId().equals(uid))throw new IllegalArgumentException("Only the sender can edit this message");if(Duration.between(m.getCreatedAt(),LocalDateTime.now()).toMinutes()>20)throw new IllegalArgumentException("Messages can only be edited within 20 minutes");String content=Optional.ofNullable(b.get("content")).orElse("").trim();if(content.isBlank())throw new IllegalArgumentException("Message cannot be empty");m.setContent(content);return dto(messages.save(m));}
 @DeleteMapping("/{id}") public void delete(@PathVariable Long id,@RequestParam(defaultValue="me") String mode,HttpSession s){CommunityMessage m=messages.findById(id).orElseThrow();Long uid=me(s);if(mode.equals("everyone")){if(!m.getSender().getId().equals(uid))throw new IllegalArgumentException("Only the sender can delete for everyone");if(Duration.between(m.getCreatedAt(),LocalDateTime.now()).toMinutes()>20)throw new IllegalArgumentException("Delete for everyone is limited to 20 minutes");messages.delete(m);return;}if(!m.getSender().getId().equals(uid))throw new IllegalArgumentException("Only the sender can delete this message");m.setContent("This message was deleted");messages.save(m);}
}
