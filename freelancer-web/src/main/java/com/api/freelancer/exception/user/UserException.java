package com.api.freelancer.exception.user;

public sealed class UserException extends RuntimeException permits InvalidUserException, UserNotFoundException{

    public UserException(final String message) {
        super(message);
    }
}
