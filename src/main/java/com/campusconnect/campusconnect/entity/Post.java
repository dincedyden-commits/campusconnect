package com.campusconnect.campusconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Maps to the existing CampusConnect posts table.
 * The existing database uses user_id/media_url/media_type/view_count.
 */
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(name = "media_url", length = 1000)
    private String mediaUrl;

    @Column(name = "media_type", length = 100)
    private String mediaType;

    @Column(name = "view_count", nullable = false)
    private long viewCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { if (createdAt == null) createdAt = LocalDateTime.now(); }

    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getContent(){return content;}
    public void setContent(String content){this.content=content;}
    public User getAuthor(){return author;}
    public void setAuthor(User author){this.author=author;}
    public String getMediaUrl(){return mediaUrl;}
    public void setMediaUrl(String mediaUrl){this.mediaUrl=mediaUrl;}
    public String getMediaType(){return mediaType;}
    public void setMediaType(String mediaType){this.mediaType=mediaType;}
    public long getViewCount(){return viewCount;}
    public void setViewCount(long viewCount){this.viewCount=viewCount;}
    public LocalDateTime getCreatedAt(){return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt){this.createdAt=createdAt;}

    @Transient public String getImageUrl(){return mediaUrl != null && mediaType != null && mediaType.startsWith("image/") ? mediaUrl : null;}
    @Transient public String getVideoUrl(){return mediaUrl != null && mediaType != null && mediaType.startsWith("video/") ? mediaUrl : null;}
}
