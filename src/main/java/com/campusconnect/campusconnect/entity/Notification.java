package com.campusconnect.campusconnect.entity;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="notifications")
public class Notification {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id",nullable=false) private User user;
 @Column(nullable=false,length=500) private String message;
 @Column(length=100) private String type;
 @Column(name="read_flag",nullable=false) private boolean readFlag;
 @Column(name="created_at",nullable=false) private LocalDateTime createdAt;
 @PrePersist void onCreate(){createdAt=LocalDateTime.now();}
 public Long getId(){return id;} public User getUser(){return user;} public void setUser(User v){user=v;} public String getMessage(){return message;} public void setMessage(String v){message=v;}
 public String getType(){return type;} public void setType(String v){type=v;} public boolean isReadFlag(){return readFlag;} public void setReadFlag(boolean v){readFlag=v;} public LocalDateTime getCreatedAt(){return createdAt;}
}