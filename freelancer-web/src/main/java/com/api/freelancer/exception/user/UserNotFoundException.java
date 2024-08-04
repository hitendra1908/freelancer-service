package com.api.freelancer.exception.user;

public non-sealed class UserNotFoundException extends UserException{
    public UserNotFoundException(final String message) {
        super(message);
    }
}
