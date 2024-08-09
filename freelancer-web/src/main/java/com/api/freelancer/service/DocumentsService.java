package com.api.freelancer.service;

import com.api.freelancer.document.DocumentRequestDto;
import com.api.freelancer.document.DocumentResponseDto;
import com.api.freelancer.entity.Documents;
import com.api.freelancer.entity.Freelancer;
import com.api.freelancer.exception.document.DocumentNotFoundException;
import com.api.freelancer.exception.document.DuplicateDocumentException;
import com.api.freelancer.model.Notification;
import com.api.freelancer.repository.DocumentsRepository;
import com.api.freelancer.validator.DocumentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentsService {

    private final DocumentsRepository documentsRepository;
    private final FreelancerService freelancerService;
    private final NotificationService notificationService;

    public DocumentResponseDto createDocument(final DocumentRequestDto incomingDoc, final MultipartFile uploadedFile) {
        DocumentValidator.validateDocument(incomingDoc, uploadedFile);

        Freelancer freelancer = freelancerService.findUserByUserName(incomingDoc.getUserName());

        Documents savedDocument = saveDocumentAndSendNotification(incomingDoc, uploadedFile, freelancer);

        return getDocumentResponseDto(savedDocument);
    }

    public DocumentResponseDto updateDocument(final Long id, final DocumentRequestDto incomingDoc, final MultipartFile uploadedFile) {
        DocumentValidator.validateDocument(incomingDoc, uploadedFile);

        Freelancer freelancer = freelancerService.findUserByUserName(incomingDoc.getUserName());

        Documents savedDocument = updateDocumentAndSendNotification(id, incomingDoc, uploadedFile, freelancer);

        return getDocumentResponseDto(savedDocument);
    }

    public DocumentResponseDto getDocument(final Long id) {
        Documents document = getDocumentById(id, "Document you are trying to find is not found.");

        log.debug("Document found with id {}", id);
        return getDocumentResponseDto(document);
    }

    public void deleteDocument(final Long id) {
        Documents document = getDocumentById(id, "Document you are trying to delete is not found.");
        documentsRepository.delete(document);
        log.debug("{} deleted from the db", document.getName());
        sendDeleteNotification(document);
    }

    private Documents getDocumentById(Long id, String errMsg) {
        return documentsRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(errMsg));
    }

    @Transactional
    private Documents saveDocumentAndSendNotification(final DocumentRequestDto incomingDoc, final MultipartFile uploadedFile, final Freelancer freelancer) {
        Documents document = buildDocument(incomingDoc, uploadedFile, freelancer);

        Documents savedDocument;
        try {
            savedDocument = documentsRepository.save(document);
            log.debug("{} saved in the db", document.getName());
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateDocumentException("Document with name: " + document.getName() + " already exists");
        }

        if (savedDocument.isVerified()) {
            sendUploadNotification(freelancer, savedDocument);
        }
        return savedDocument;
    }

    @Transactional
    private Documents updateDocumentAndSendNotification(final Long id, final DocumentRequestDto incomingDoc, final MultipartFile uploadedFile, final Freelancer freelancer) {
        Documents document = documentsRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document you are trying to update is not found."));

        updateDocumentFields(document, incomingDoc, uploadedFile, freelancer);

        Documents updatedDocument = documentsRepository.save(document);
        log.debug("{} updated in the db", document.getName());
        if (updatedDocument.isVerified()) {
            sendUpdateNotification(freelancer, updatedDocument);
        }
        return updatedDocument;
    }

    private static DocumentResponseDto getDocumentResponseDto(final Documents document) {
        return DocumentResponseDto.builder()
                .id(document.getId())
                .name(document.getName())
                .documentType(document.getDocumentType())
                .userName(document.getFreelancer().getUserName())
                .fileType(document.getFileType())
                .expiryDate(document.getExpiryDate())
                .verified(document.isVerified())
                .build();
    }

    private void sendUploadNotification(Freelancer freelancer, Documents document) {
        String message = String.format("Document: %s for user: %s is successfully uploaded and verified.",
                document.getName(), freelancer.getUserName());
        log.debug("sending notification to {} for successfully uploading and validating {}", freelancer.getUserName(),
                document.getName());
        sendNotification(freelancer, document.getName(), message);
    }

    private void sendUpdateNotification(Freelancer freelancer, Documents document) {
        String message = String.format("Document: %s for user: %s is successfully updated and verified.",
                document.getName(), freelancer.getUserName());
        log.debug("sending notification to {} for successfully updating {}", freelancer.getUserName(), document.getName());
        sendNotification(freelancer, document.getName(), message);
    }

    private void sendDeleteNotification(Documents document) {
        Freelancer freelancer = document.getFreelancer();
        String message = String.format("Document: %s for user: %s is successfully deleted.",
                document.getName(), freelancer.getUserName());
        log.debug("sending notification to {} for successfully deleting {}", freelancer.getUserName(), document.getName());
        sendNotification(freelancer, document.getName(), message);
    }

    private void sendNotification(Freelancer freelancer, String documentName, String message) {
        Notification notification = Notification.builder()
                .receiver(freelancer.getUserName())
                .documentName(documentName)
                .timestamp(LocalDateTime.now())
                .build();
        notificationService.sendNotification(notification);
    }

    private Documents buildDocument(DocumentRequestDto incomingDoc, MultipartFile uploadedFile, Freelancer freelancer) {
        String baseNameOfTheFile = FilenameUtils.getBaseName(uploadedFile.getOriginalFilename());
        return Documents.builder()
                .name(baseNameOfTheFile)
                .documentType(incomingDoc.getDocumentType())
                .fileType(uploadedFile.getContentType())
                .expiryDate(incomingDoc.getExpiryDate())
                .freelancer(freelancer)
                .verified(true)
                .build();
    }

    private void updateDocumentFields(Documents document, DocumentRequestDto incomingDoc, MultipartFile uploadedFile, Freelancer freelancer) {
        document.setName(FilenameUtils.getBaseName(uploadedFile.getOriginalFilename()));
        document.setDocumentType(incomingDoc.getDocumentType());
        document.setFileType(uploadedFile.getContentType());
        document.setExpiryDate(incomingDoc.getExpiryDate());
        document.setFreelancer(freelancer);
        document.setVerified(true);
    }
}
