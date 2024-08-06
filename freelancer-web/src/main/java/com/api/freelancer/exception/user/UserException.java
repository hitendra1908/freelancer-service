package com.api.freelancer.exception.user;

public sealed class UserException extends RuntimeException permits InvalidUserException, UserNotFoundException,
        DuplicateUserException, UserNameException{

    public UserException(final String message) {
        super(message);
    }
}
