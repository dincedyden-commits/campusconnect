package com.campusconnect.campusconnect.entity;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="comments")
public class Comment {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=1000) private String content;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="post_id",nullable=false) private Post post;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id",nullable=false) private User user;
 @Column(name="created_at",nullable=false) private LocalDateTime createdAt;
 @PrePersist void onCreate(){createdAt=LocalDateTime.now();}
 public Long getId(){return id;} public String getContent(){return content;} public void setContent(String v){content=v;}
 public Post getPost(){return post;} public void setPost(Post v){post=v;} public User getUser(){return user;} public void setUser(User v){user=v;}
 public LocalDateTime getCreatedAt(){return createdAt;}
}