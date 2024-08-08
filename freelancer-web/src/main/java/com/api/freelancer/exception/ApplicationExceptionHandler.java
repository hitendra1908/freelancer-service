package com.api.freelancer.exception;

import com.api.freelancer.exception.document.DocumentException;
import com.api.freelancer.exception.document.DocumentExpiryException;
import com.api.freelancer.exception.document.DocumentNameException;
import com.api.freelancer.exception.document.DocumentNotFoundException;
import com.api.freelancer.exception.document.DuplicateDocumentException;
import com.api.freelancer.exception.file.FileException;
import com.api.freelancer.exception.file.FileNotFoundException;
import com.api.freelancer.exception.file.UnSupportedFileFormatException;
import com.api.freelancer.exception.freelancer.DuplicateUserException;
import com.api.freelancer.exception.freelancer.InvalidUserException;
import com.api.freelancer.exception.freelancer.UserException;
import com.api.freelancer.exception.freelancer.UserNameException;
import com.api.freelancer.exception.freelancer.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(FileException.class)
    public ProblemDetail handleFileException(FileException exception) {
        return switch (exception) {
            case FileNotFoundException e -> createProblemDetail(e, HttpStatus.BAD_REQUEST, "File not found");
            case UnSupportedFileFormatException e -> createProblemDetail(e, HttpStatus.BAD_REQUEST, "Wrong file format");
            default -> createProblemDetail(exception, HttpStatus.INTERNAL_SERVER_ERROR, "Exception while processing file");
        };
    }

    @ExceptionHandler(DocumentException.class)
    public ProblemDetail handleDocumentException(DocumentException exception) {
        return switch (exception) {
            case DocumentNameException e -> createProblemDetail(e, HttpStatus.BAD_REQUEST, "Wrong document name");
            case DocumentExpiryException e -> createProblemDetail(e, HttpStatus.BAD_REQUEST, "Document expiring too soon");
            case DuplicateDocumentException e -> createProblemDetail(e, HttpStatus.CONFLICT, "Document already exists");
            case DocumentNotFoundException e -> createProblemDetail(e, HttpStatus.BAD_REQUEST, "Document not found");
            default -> createProblemDetail(exception, HttpStatus.INTERNAL_SERVER_ERROR, "Exception while processing document");
        };
    }

    @ExceptionHandler(UserException.class)
    public ProblemDetail handleUserException(UserException exception) {
        return switch (exception) {
            case InvalidUserException e -> createProblemDetail(e, HttpStatus.BAD_REQUEST, "Invalid user");
            case UserNotFoundException e -> createProblemDetail(e, HttpStatus.BAD_REQUEST, "User not found");
            case DuplicateUserException e -> createProblemDetail(e, HttpStatus.CONFLICT, "Username must be unique");
            case UserNameException e -> createProblemDetail(e, HttpStatus.BAD_REQUEST, "You cannot updated username");
            default -> createProblemDetail(exception, HttpStatus.INTERNAL_SERVER_ERROR, "User Exception occurred");
        };
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAllOtherExceptions(Exception exception) {
        log.error("Generic exception occurred while processing the request: ", exception);
        return createProblemDetail(exception, HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
    }

    private static ProblemDetail createProblemDetail(Exception exception, HttpStatus status, String title) {
        var problemDetail = ProblemDetail.forStatusAndDetail(status, exception.getMessage());
        problemDetail.setTitle(title);
        return problemDetail;
    }
}
