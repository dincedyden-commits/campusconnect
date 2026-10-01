package com.campusconnect.campusconnect.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="follows", uniqueConstraints=@UniqueConstraint(columnNames={"follower_id","following_id"}))
public class Follow {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="follower_id",nullable=false) private User follower;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="following_id",nullable=false) private User following;
 @Column(name="created_at",nullable=false) private LocalDateTime createdAt;
 @PrePersist void onCreate(){createdAt=LocalDateTime.now();}
 public Long getId(){return id;} public User getFollower(){return follower;} public void setFollower(User v){follower=v;}
 public User getFollowing(){return following;} public void setFollowing(User v){following=v;}
}