package com.campusconnect.campusconnect.exception;

public class DuplicateUsernameException extends RuntimeException {
    public DuplicateUsernameException(String username) {
        super("Username \"" + username + "\" is already taken");
    }
}
