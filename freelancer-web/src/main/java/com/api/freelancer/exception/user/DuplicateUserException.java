package com.api.freelancer.exception.user;

public non-sealed class DuplicateUserException extends UserException{
    public DuplicateUserException(final String message) {
        super(message);
    }
}
