package com.campusconnect.campusconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="lost_found_items")
public class LostFoundItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=20) private String type;
    @Column(nullable=false, length=100) private String title;
    @Column(nullable=false, length=1000) private String description;
    @Column(length=180) private String location;
    @Column(length=500) private String photoUrl;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="created_by_id") private User createdBy;
    @Column(nullable=false, updatable=false) private LocalDateTime createdAt;
    @Column(nullable=false) private LocalDateTime expiresAt;
    @PrePersist void onCreate(){ if(createdAt==null) createdAt=LocalDateTime.now(); if(expiresAt==null) expiresAt=createdAt.plusDays(60); }
    public Long getId(){return id;} public String getType(){return type;} public void setType(String v){type=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public String getLocation(){return location;} public void setLocation(String v){location=v;}
    public String getPhotoUrl(){return photoUrl;} public void setPhotoUrl(String v){photoUrl=v;}
    public User getCreatedBy(){return createdBy;} public void setCreatedBy(User v){createdBy=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getExpiresAt(){return expiresAt;}
}
