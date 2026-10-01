package com.campusconnect.campusconnect.dto;

import com.campusconnect.campusconnect.entity.Post;
import java.time.LocalDateTime;

public class PostResponse {
    private Long id;
    private String content;
    private Long authorId;
    private String authorUsername;
    private String authorProfilePictureUrl;
    private String imageUrl;
    private String videoUrl;
    private long viewCount;
    private long likeCount;
    private long commentCount;
    private boolean liked;
    private boolean followingAuthor;
    private LocalDateTime createdAt;

    public static PostResponse from(Post p, long likes, long comments, boolean liked, boolean followingAuthor) {
        PostResponse r = new PostResponse();
        r.id = p.getId(); r.content = p.getContent();
        r.authorId = p.getAuthor().getId(); r.authorUsername = p.getAuthor().getUsername(); r.authorProfilePictureUrl = p.getAuthor().getProfilePictureUrl();
        r.imageUrl = p.getImageUrl(); r.videoUrl = p.getVideoUrl(); r.viewCount = p.getViewCount();
        r.likeCount = likes; r.commentCount = comments; r.liked = liked;
        r.followingAuthor = followingAuthor; r.createdAt = p.getCreatedAt();
        return r;
    }
    public Long getId(){return id;} public String getContent(){return content;} public Long getAuthorId(){return authorId;}
    public String getAuthorUsername(){return authorUsername;} public String getAuthorProfilePictureUrl(){return authorProfilePictureUrl;} public String getImageUrl(){return imageUrl;} public String getVideoUrl(){return videoUrl;}
    public long getViewCount(){return viewCount;} public long getLikeCount(){return likeCount;} public long getCommentCount(){return commentCount;}
    public boolean isLiked(){return liked;} public boolean isFollowingAuthor(){return followingAuthor;} public LocalDateTime getCreatedAt(){return createdAt;}
}
