package com.campusconnect.campusconnect.dto;

import jakarta.validation.constraints.Size;

public class PostRequest {
    @Size(max = 2000, message = "Post is too long")
    private String content;
    private String imageUrl;
    private String videoUrl;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
}
