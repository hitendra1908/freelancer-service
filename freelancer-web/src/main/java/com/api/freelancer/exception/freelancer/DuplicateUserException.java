package com.api.freelancer.exception.freelancer;

public non-sealed class DuplicateUserException extends UserException{
    public DuplicateUserException(final String message) {
        super(message);
    }
}
