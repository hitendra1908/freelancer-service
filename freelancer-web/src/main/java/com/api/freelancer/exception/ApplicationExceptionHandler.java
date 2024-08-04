package com.api.freelancer.exception;

import com.api.freelancer.exception.document.DocumentException;
import com.api.freelancer.exception.document.DocumentExpiryException;
import com.api.freelancer.exception.document.DocumentNameException;
import com.api.freelancer.exception.document.FileException;
import com.api.freelancer.exception.document.FileNotFoundException;
import com.api.freelancer.exception.document.UnSupportedFileFormatException;
import com.api.freelancer.exception.user.InvalidUserException;
import com.api.freelancer.exception.user.UserException;
import com.api.freelancer.exception.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(FileException.class)
    public ProblemDetail handleFileException(FileException exception) {
        if (exception instanceof FileNotFoundException) {
            return handleFileNotFoundException(exception);
        }
        if (exception instanceof UnSupportedFileFormatException) {
           return handleUnSupportedFileFormatException(exception);
        }
        return handleDefaultFileException(exception);
    }

    @ExceptionHandler(DocumentException.class)
    public ProblemDetail handleDocumentException(DocumentException exception) {
        if (exception instanceof DocumentNameException) {
            return handleDocumentNameException(exception);
        }
        if (exception instanceof DocumentExpiryException) {
            return handleDocumentExpiryException(exception);
        }
        return handleDefaultDocumentException(exception);
    }

    @ExceptionHandler(UserException.class)
    public ProblemDetail handleUserException(UserException exception) {
        if (exception instanceof InvalidUserException) {
            return getProblemDetail(exception, "invalid user");
        }
        if (exception instanceof UserNotFoundException) {
            return getProblemDetail(exception, "User not found");
        }
        return handleDefaultUserException(exception);

    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAllOtherException(Exception exception) {
        log.error("Generic exception occurred while processing the request: {}", exception.getCause().toString());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        problemDetail.setTitle("Something went wrong!");
        return problemDetail;
    }

    private ProblemDetail handleFileNotFoundException(FileException exception) {
        return getProblemDetail(exception, "File not found");
    }
    private ProblemDetail handleUnSupportedFileFormatException(FileException exception) {
        return getProblemDetail(exception, "Wrong file format");
    }
    private ProblemDetail handleDefaultFileException(FileException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        problemDetail.setTitle("Exception while processing document");
        return problemDetail;
    }

    private ProblemDetail handleDocumentNameException(DocumentException exception) {
        return getProblemDetail(exception, "Wrong document name");
    }
    private ProblemDetail handleDocumentExpiryException(DocumentException exception) {
        return getProblemDetail(exception, "Document expiring too soon");
    }
    private ProblemDetail handleDefaultDocumentException(DocumentException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        problemDetail.setTitle("Business Error occurred");
        return problemDetail;
    }

    private ProblemDetail handleDefaultUserException(UserException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        problemDetail.setTitle("User Exception occurred");
        return problemDetail;
    }

    private static ProblemDetail getProblemDetail(Exception exception, String title) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problemDetail.setTitle(title);
        return problemDetail;
    }

}
