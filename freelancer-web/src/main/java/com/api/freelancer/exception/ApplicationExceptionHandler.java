package com.api.freelancer.exception;

import com.api.freelancer.exception.document.DocumentException;
import com.api.freelancer.exception.document.DocumentExpiryException;
import com.api.freelancer.exception.document.DocumentNameException;
import com.api.freelancer.exception.document.DocumentNotFoundException;
import com.api.freelancer.exception.document.DuplicateDocumentException;
import com.api.freelancer.exception.document.FileException;
import com.api.freelancer.exception.document.FileNotFoundException;
import com.api.freelancer.exception.document.UnSupportedFileFormatException;
import com.api.freelancer.exception.user.DuplicateUserException;
import com.api.freelancer.exception.user.InvalidUserException;
import com.api.freelancer.exception.user.UserException;
import com.api.freelancer.exception.user.UserNameException;
import com.api.freelancer.exception.user.UserNotFoundException;
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
        if (exception instanceof FileNotFoundException) {
           return getProblemDetailForBadRequest(exception, "File not found");
        }
        if (exception instanceof UnSupportedFileFormatException) {
            return getProblemDetailForBadRequest(exception, "Wrong file format");
        }
        return handleDefaultFileException(exception);
    }

    @ExceptionHandler(DocumentException.class)
    public ProblemDetail handleDocumentException(DocumentException exception) {
        if (exception instanceof DocumentNameException) {
            return getProblemDetailForBadRequest(exception, "Wrong document name");
        }
        if (exception instanceof DocumentExpiryException) {
            return getProblemDetailForBadRequest(exception, "Document expiring too soon");
        }
        if (exception instanceof DuplicateDocumentException) {
            return getProblemDetailForConflict(exception, "Document already exists");
        }
        if (exception instanceof DocumentNotFoundException) {
            return getProblemDetailForBadRequest(exception, "Document not found");
        }
        return handleDefaultDocumentException(exception);
    }

    @ExceptionHandler(UserException.class)
    public ProblemDetail handleUserException(UserException exception) {
        if (exception instanceof InvalidUserException) {
            return getProblemDetailForBadRequest(exception, "invalid user");
        }
        if (exception instanceof UserNotFoundException) {
            return getProblemDetailForBadRequest(exception, "User not found");
        }
        if (exception instanceof DuplicateUserException) {
            return getProblemDetailForConflict(exception, "username must be unique");
        }
        if (exception instanceof UserNameException) {
            return getProblemDetailForBadRequest(exception, "username cannot be updated");
        }
        return handleDefaultUserException(exception);

    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAllOtherException(Exception exception) {
        log.error("Generic exception occurred while processing the request: ", exception);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        problemDetail.setTitle("Something went wrong!");
        return problemDetail;
    }

    private ProblemDetail handleDefaultFileException(FileException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        problemDetail.setTitle("Exception while processing document");
        return problemDetail;
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

    private static ProblemDetail getProblemDetailForBadRequest(Exception exception, String title) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problemDetail.setTitle(title);
        return problemDetail;
    }

    private static ProblemDetail getProblemDetailForConflict(Exception exception, String title) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        problemDetail.setTitle(title);
        return problemDetail;
    }

}
