package com.api.freelancer.service;

import com.api.freelancer.document.DocumentRequestDto;
import com.api.freelancer.document.DocumentResponseDto;
import com.api.freelancer.exception.document.DocumentNotFoundException;
import com.api.freelancer.exception.document.DuplicateDocumentException;
import com.api.freelancer.exception.user.UserNotFoundException;
import com.api.freelancer.model.Documents;
import com.api.freelancer.model.Users;
import com.api.freelancer.repository.DocumentsRepository;
import com.api.freelancer.repository.UserRepository;
import com.api.freelancer.validator.DocumentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentsService {

    private final DocumentsRepository documentsRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public DocumentResponseDto createDocument(final DocumentRequestDto incomingDoc, final MultipartFile uploadedFile) {
        DocumentValidator.validateDocument(incomingDoc, uploadedFile);

        Users user = userRepository.findByUserName(incomingDoc.userName())
                .orElseThrow(() -> new UserNotFoundException(
                        "Wrong userName: No user found for the given userName: " + incomingDoc.userName()));

        Documents savedDocument = saveDocumentAndSendNotification(incomingDoc, uploadedFile, user);

        return getDocumentResponseDto(savedDocument);
    }

    public DocumentResponseDto updateDocument(final Long id, final DocumentRequestDto incomingDoc, final MultipartFile uploadedFile) {
        DocumentValidator.validateDocument(incomingDoc, uploadedFile);

        Users user = userRepository.findByUserName(incomingDoc.userName())
                .orElseThrow(() -> new UserNotFoundException(
                        "Wrong userName: No user found for the given userName: " + incomingDoc.userName()));

        Documents savedDocument = updateDocumentAndSendNotification(id, incomingDoc, uploadedFile, user);

        return getDocumentResponseDto(savedDocument);
    }

    public DocumentResponseDto getDocument(final Long id) {
        Documents document = documentsRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document you are trying to find is not found."));

        log.debug("Document found with id {}", id);
        return getDocumentResponseDto(document);
    }

    public void deleteDocument(final Long id) {
        Documents document = documentsRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document you are trying to delete is not found."));

        documentsRepository.delete(document);
        log.debug("{} deleted from the db", document.getName());
        sendDeleteNotification(document);
    }

    @Transactional
    private Documents saveDocumentAndSendNotification(final DocumentRequestDto incomingDoc, final MultipartFile uploadedFile, final Users user) {
        Documents document = buildDocument(incomingDoc, uploadedFile, user);

        Documents savedDocument;
        try {
            savedDocument = documentsRepository.save(document);
            log.debug("{} saved in the db", document.getName());
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateDocumentException("Document with name: " + document.getName() + " already exists");
        }

        if (savedDocument.isVerified()) {
            sendUploadNotification(user, savedDocument);
        }
        return savedDocument;
    }

    @Transactional
    private Documents updateDocumentAndSendNotification(final Long id, final DocumentRequestDto incomingDoc, final MultipartFile uploadedFile, final Users user) {
        Documents document = documentsRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document you are trying to update is not found."));

        updateDocumentFields(document, incomingDoc, uploadedFile, user);

        Documents updatedDocument = documentsRepository.save(document);
        log.debug("{} updated in the db", document.getName());
        if (updatedDocument.isVerified()) {
            sendUpdateNotification(user, updatedDocument);
        }
        return updatedDocument;
    }

    private static DocumentResponseDto getDocumentResponseDto(final Documents document) {
        return DocumentResponseDto.builder()
                .id(document.getId())
                .name(document.getName())
                .documentType(document.getDocumentType())
                .userName(document.getUser().getUserName())
                .fileType(document.getFileType())
                .expiryDate(document.getExpiryDate())
                .verified(document.isVerified())
                .build();
    }

    private void sendUploadNotification(Users user, Documents document) {
        String message = String.format("Document: %s for user: %s is successfully uploaded and verified.",
                document.getName(), user.getUserName());
        log.debug("sending notification to {} for successfully uploading and validating {}", user.getUserName(),
                document.getName());
        notificationService.sendNotification(user, document.getName(), message);
    }

    private void sendUpdateNotification(Users user, Documents document) {
        String message = String.format("Document: %s for user: %s is successfully updated and verified.",
                document.getName(), user.getUserName());
        log.debug("sending notification to {} for successfully updating {}", user.getUserName(), document.getName());
        notificationService.sendNotification(user, document.getName(), message);
    }

    private void sendDeleteNotification(Documents document) {
        Users user = document.getUser();
        String message = String.format("Document: %s for user: %s is successfully deleted.",
                document.getName(), user.getUserName());
        log.debug("sending notification to {} for successfully deleting {}", user.getUserName(), document.getName());
        notificationService.sendNotification(user, document.getName(), message);
    }

    private Documents buildDocument(DocumentRequestDto incomingDoc, MultipartFile uploadedFile, Users user) {
        String baseNameOfTheFile = FilenameUtils.getBaseName(uploadedFile.getOriginalFilename());
        return Documents.builder()
                .name(baseNameOfTheFile)
                .documentType(incomingDoc.documentType())
                .fileType(uploadedFile.getContentType())
                .expiryDate(incomingDoc.expiryDate())
                .user(user)
                .verified(true)
                .build();
    }

    private void updateDocumentFields(Documents document, DocumentRequestDto incomingDoc, MultipartFile uploadedFile, Users user) {
        document.setName(FilenameUtils.getBaseName(uploadedFile.getOriginalFilename()));
        document.setDocumentType(incomingDoc.documentType());
        document.setFileType(uploadedFile.getContentType());
        document.setExpiryDate(incomingDoc.expiryDate());
        document.setUser(user);
        document.setVerified(true);
    }
}
