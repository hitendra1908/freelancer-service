package com.api.freelancer.exception;

import com.api.freelancer.exception.document.*;
import com.api.freelancer.exception.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationExceptionHandlerTest {

    private ApplicationExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ApplicationExceptionHandler();
    }

    @Test
    void handleFileNotFoundException() {
        FileNotFoundException exception = new FileNotFoundException("File not found");
        ProblemDetail response = handler.handleFileException(exception);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("File not found", response.getTitle());
        assertEquals("File not found", response.getDetail());
    }

    @Test
    void handleUnSupportedFileFormatException() {
        UnSupportedFileFormatException exception = new UnSupportedFileFormatException("Unsupported file format");
        ProblemDetail response = handler.handleFileException(exception);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Wrong file format", response.getTitle());
        assertEquals("Unsupported file format", response.getDetail());
    }

    @Test
    void handleDocumentNameException() {
        DocumentNameException exception = new DocumentNameException("Invalid document name");
        ProblemDetail response = handler.handleDocumentException(exception);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Wrong document name", response.getTitle());
        assertEquals("Invalid document name", response.getDetail());
    }

    @Test
    void handleDocumentExpiryException() {
        DocumentExpiryException exception = new DocumentExpiryException("Document expiring soon");
        ProblemDetail response = handler.handleDocumentException(exception);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Document expiring too soon", response.getTitle());
        assertEquals("Document expiring soon", response.getDetail());
    }

    @Test
    void handleDuplicateDocumentException() {
        DuplicateDocumentException exception = new DuplicateDocumentException("Duplicate document");
        ProblemDetail response = handler.handleDocumentException(exception);
        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        assertEquals("Document already exists", response.getTitle());
        assertEquals("Duplicate document", response.getDetail());
    }

    @Test
    void handleDocumentNotFoundException() {
        DocumentNotFoundException exception = new DocumentNotFoundException("Document not found");
        ProblemDetail response = handler.handleDocumentException(exception);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Document not found", response.getTitle());
        assertEquals("Document not found", response.getDetail());
    }

    @Test
    void handleInvalidUserException() {
        InvalidUserException exception = new InvalidUserException("Invalid user");
        ProblemDetail response = handler.handleUserException(exception);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Invalid user", response.getTitle());
        assertEquals("Invalid user", response.getDetail());
    }

    @Test
    void handleUserNotFoundException() {
        UserNotFoundException exception = new UserNotFoundException("User not found");
        ProblemDetail response = handler.handleUserException(exception);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("User not found", response.getTitle());
        assertEquals("User not found", response.getDetail());
    }

    @Test
    void handleDuplicateUserException() {
        DuplicateUserException exception = new DuplicateUserException("Duplicate user");
        ProblemDetail response = handler.handleUserException(exception);
        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        assertEquals("Username must be unique", response.getTitle());
        assertEquals("Duplicate user", response.getDetail());
    }

    @Test
    void handleUserNameException() {
        UserNameException exception = new UserNameException("Username cannot be updated");
        ProblemDetail response = handler.handleUserException(exception);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Username cannot be updated", response.getTitle());
        assertEquals("Username cannot be updated", response.getDetail());
    }

    @Test
    void handleAllOtherException() {
        Exception exception = new Exception("Generic exception");
        ProblemDetail response = handler.handleAllOtherException(exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
        assertEquals("Something went wrong!", response.getTitle());
        assertEquals("Generic exception", response.getDetail());
    }
}
