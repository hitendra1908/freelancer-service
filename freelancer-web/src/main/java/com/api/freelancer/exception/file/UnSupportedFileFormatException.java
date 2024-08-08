package com.api.freelancer.exception.file;

public non-sealed class UnSupportedFileFormatException extends FileException {

    public UnSupportedFileFormatException(final String message) {
        super(message);
    }
}
