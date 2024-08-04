package com.api.freelancer.exception.user;

public non-sealed class InvalidUserException extends UserException{
    public InvalidUserException(final String message) {
        super(message);
    }
}
