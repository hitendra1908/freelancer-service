package com.api.freelancer.exception.freelancer;

public sealed class UserException extends RuntimeException permits InvalidUserException, UserNotFoundException,
        DuplicateUserException, UserNameException{

    public UserException(final String message) {
        super(message);
    }
}
