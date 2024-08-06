package com.api.freelancer.validator;

import com.api.freelancer.document.DocumentRequestDto;
import com.api.freelancer.exception.document.DocumentExpiryException;
import com.api.freelancer.exception.document.DocumentNameException;
import com.api.freelancer.exception.document.FileNotFoundException;
import com.api.freelancer.exception.document.UnSupportedFileFormatException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Slf4j
public class DocumentValidator {

    private static final List<String> SUPPORTED_FORMAT = List.of("pdf", "jpeg", "jpg");

    public static void validateDocument(final DocumentRequestDto incomingDoc, final MultipartFile uploadedFile) {
        validateFile(uploadedFile);

        if (!Objects.requireNonNull(uploadedFile.getOriginalFilename()).startsWith(incomingDoc.userName())) {
            log.error("Wrong document name");
            throw new DocumentNameException("Document name must start with the owner's username");
        }
        if (incomingDoc.expiryDate().isBefore(LocalDate.now().plusMonths(2))) {
            log.error("Document expiring too soon");
            throw new DocumentExpiryException("Document expiry date must be at least 2 months from now");
        }
    }

    private static void validateFile(final MultipartFile uploadedFile) {
        if (uploadedFile == null || uploadedFile.isEmpty()) {
            log.error("No file found in the request");
            throw new FileNotFoundException("No file found in the request");
        }

        String extension = FilenameUtils.getExtension(uploadedFile.getOriginalFilename());
        if (!SUPPORTED_FORMAT.contains(extension)) {
            log.error("Requested file format not supported.");
            throw new UnSupportedFileFormatException(String.format("Unsupported file: %s format not supported", extension));
        }
    }
}
