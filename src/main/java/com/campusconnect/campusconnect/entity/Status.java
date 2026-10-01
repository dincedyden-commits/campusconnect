package com.campusconnect.campusconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "statuses")
public class Status {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;
    @Column(name = "media_url", nullable = true, length = 500)
    private String mediaUrl;
    @Column(name = "media_type", nullable = true, length = 30)
    private String mediaType;
    @Column(length = 280)
    private String caption;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    @PrePersist protected void onCreate(){ if(createdAt==null) createdAt=LocalDateTime.now(); if(expiresAt==null) expiresAt=createdAt.plusHours(24); }
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public User getUser(){return user;} public void setUser(User user){this.user=user;}
    public String getMediaUrl(){return mediaUrl;} public void setMediaUrl(String mediaUrl){this.mediaUrl=mediaUrl;}
    public String getMediaType(){return mediaType;} public void setMediaType(String mediaType){this.mediaType=mediaType;}
    public String getCaption(){return caption;} public void setCaption(String caption){this.caption=caption;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime createdAt){this.createdAt=createdAt;}
    public LocalDateTime getExpiresAt(){return expiresAt;} public void setExpiresAt(LocalDateTime expiresAt){this.expiresAt=expiresAt;}
}
