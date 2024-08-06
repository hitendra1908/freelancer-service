package com.api.freelancer.exception.document;

public non-sealed class DuplicateDocumentException extends DocumentException {

    public DuplicateDocumentException(final String message) {
        super(message);
    }
}
