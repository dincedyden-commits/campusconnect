package com.campusconnect.campusconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="communities")
public class Community {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=100) private String name;
 @Column(nullable=false,length=20) private String type; // GROUP or CHANNEL
 @Column(length=500) private String description;
 @Column(name="profile_picture_url",length=1000) private String profilePictureUrl;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="owner_id") private User owner;
 @Column(name="created_at",nullable=false) private LocalDateTime createdAt;
 @PrePersist void onCreate(){createdAt=LocalDateTime.now();}
 public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;}
 public String getType(){return type;} public void setType(String v){type=v;}
 public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public String getProfilePictureUrl(){return profilePictureUrl;} public void setProfilePictureUrl(String v){profilePictureUrl=v;}
 public User getOwner(){return owner;} public void setOwner(User v){owner=v;} public LocalDateTime getCreatedAt(){return createdAt;}
}
