package com.campusconnect.campusconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="community_members", uniqueConstraints=@UniqueConstraint(columnNames={"community_id","user_id"}))
public class CommunityMember {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="community_id") private Community community;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id") private User user;
 @Column(nullable=false,length=20) private String role; // OWNER, ADMIN, MEMBER
 @Column(name="joined_at",nullable=false) private LocalDateTime joinedAt;
 @PrePersist void onCreate(){joinedAt=LocalDateTime.now();}
 public Long getId(){return id;} public Community getCommunity(){return community;} public void setCommunity(Community v){community=v;}
 public User getUser(){return user;} public void setUser(User v){user=v;} public String getRole(){return role;} public void setRole(String v){role=v;} public LocalDateTime getJoinedAt(){return joinedAt;}
}
