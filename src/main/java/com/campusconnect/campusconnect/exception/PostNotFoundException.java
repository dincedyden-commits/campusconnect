package com.campusconnect.campusconnect.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(Long id) {
        super("No post found with id " + id);
    }
}
