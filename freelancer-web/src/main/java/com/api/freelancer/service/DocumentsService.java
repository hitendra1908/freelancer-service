package com.api.freelancer.service;

import com.api.freelancer.document.DocumentRequestDto;
import com.api.freelancer.document.DocumentResponseDto;
import com.api.freelancer.exception.document.DocumentExpiryException;
import com.api.freelancer.exception.document.DocumentNameException;
import com.api.freelancer.exception.document.FileNotFoundException;
import com.api.freelancer.exception.document.UnSupportedFileFormatException;
import com.api.freelancer.exception.user.UserNotFoundException;
import com.api.freelancer.model.Documents;
import com.api.freelancer.model.Users;
import com.api.freelancer.repository.DocumentsRepository;
import com.api.freelancer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentsService {

    private static final List<String> SUPPORTED_FORMAT = List.of("pdf", "jpeg", "jpg");

    private final DocumentsRepository documentsRepository;
    private final UserRepository userRepository;

    public DocumentResponseDto save(final DocumentRequestDto incomingDoc, final MultipartFile uploadedFile) {

        validateDocument(incomingDoc, uploadedFile);

        Users user = Optional.ofNullable(userRepository.findByUserName(incomingDoc.userName()))
                .orElseThrow(() -> new UserNotFoundException("Wrong userName: No user found for the given userName : " + incomingDoc.userName()));

        Documents savedDocument = saveDocument(incomingDoc, uploadedFile, user);

        return new DocumentResponseDto(
                savedDocument.getId(),
                savedDocument.getName(),
                savedDocument.getDocumentType(),
                savedDocument.getUser().getUserName(),
                savedDocument.getFileType(),
                savedDocument.getExpiryDate(),
                savedDocument.isVerified()
        );
    }

    private Documents saveDocument(final DocumentRequestDto incomingDoc, final MultipartFile uploadedFile, final Users user) {
        Documents document = Documents.builder()
                .name(uploadedFile.getOriginalFilename())
                .documentType(incomingDoc.documentType())
                .fileType(uploadedFile.getContentType())
                .expiryDate(incomingDoc.expiryDate())
                .user(user)
                .verified(true)
                .build();

        return documentsRepository.save(document); // TODO check if we need id? check if want 2 documents with same name?
    }

    private void validateDocument(final DocumentRequestDto incomingDoc, final MultipartFile uploadedFile) {
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

    private void validateFile(final MultipartFile uploadedFile) {
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
