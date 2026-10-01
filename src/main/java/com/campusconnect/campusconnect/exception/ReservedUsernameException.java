package com.campusconnect.campusconnect.exception;

public class ReservedUsernameException extends RuntimeException {
    public ReservedUsernameException(String username) {
        super("Username \"" + username + "\" is reserved and can't be registered");
    }
}
