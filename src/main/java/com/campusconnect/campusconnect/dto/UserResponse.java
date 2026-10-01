package com.campusconnect.campusconnect.dto;

import com.campusconnect.campusconnect.entity.User;

import java.time.LocalDateTime;

/** What we hand back over the API — deliberately excludes the password hash. */
public class UserResponse {

    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String university;
    private String bio;
    private String course;
    private String yearOfStudy;
    private String profilePictureUrl;
    private String role;
    private LocalDateTime createdAt;

    public static UserResponse from(User u) {
        UserResponse r = new UserResponse();
        r.id = u.getId();
        r.username = u.getUsername();
        r.fullName = u.getFullName();
        r.email = u.getEmail();
        r.university = u.getUniversity();
        r.bio = u.getBio();
        r.course = u.getCourse();
        r.yearOfStudy = u.getYearOfStudy();
        r.profilePictureUrl = u.getProfilePictureUrl();
        r.role = u.getRole();
        r.createdAt = u.getCreatedAt();
        return r;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getUniversity() {
        return university;
    }

    public String getBio() {
        return bio;
    }

    public String getCourse() {
        return course;
    }

    public String getYearOfStudy() {
        return yearOfStudy;
    }

    public String getRole() { return role; }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
