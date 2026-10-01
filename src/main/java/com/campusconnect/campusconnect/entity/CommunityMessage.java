package com.campusconnect.campusconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="community_messages")
public class CommunityMessage {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="community_id") private Community community;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="sender_id") private User sender;
 @Column(nullable=false,length=4000) private String content;
 @Column(name="created_at",nullable=false) private LocalDateTime createdAt;
 @PrePersist void onCreate(){if(createdAt==null)createdAt=LocalDateTime.now();}
 public Long getId(){return id;} public Community getCommunity(){return community;} public void setCommunity(Community v){community=v;}
 public User getSender(){return sender;} public void setSender(User v){sender=v;} public String getContent(){return content;} public void setContent(String v){content=v;}
 public LocalDateTime getCreatedAt(){return createdAt;}
}
