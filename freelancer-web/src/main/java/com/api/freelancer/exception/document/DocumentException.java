package com.api.freelancer.exception.document;

public sealed class DocumentException extends RuntimeException permits DocumentNameException, DocumentExpiryException {

    public DocumentException(final String message) {
        super(message);
    }
}
