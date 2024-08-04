package com.api.freelancer.exception.document;

public non-sealed class UnSupportedFileFormatException extends FileException {

    public UnSupportedFileFormatException(final String message) {
        super(message);
    }
}
