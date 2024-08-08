package com.api.freelancer.exception.freelancer;

public non-sealed class UserNotFoundException extends UserException{
    public UserNotFoundException(final String message) {
        super(message);
    }
}
