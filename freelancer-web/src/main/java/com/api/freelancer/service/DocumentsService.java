package com.api.freelancer.service;

import com.api.freelancer.document.DocumentRequestDto;
import com.api.freelancer.document.DocumentResponseDto;
import com.api.freelancer.exception.user.UserNotFoundException;
import com.api.freelancer.model.Documents;
import com.api.freelancer.model.Users;
import com.api.freelancer.repository.DocumentsRepository;
import com.api.freelancer.repository.UserRepository;
import com.api.freelancer.validator.DocumentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentsService {

    private final DocumentsRepository documentsRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public DocumentResponseDto createDocument(final DocumentRequestDto incomingDoc, final MultipartFile uploadedFile) {

        DocumentValidator.validateDocument(incomingDoc, uploadedFile);

        Users user = Optional.ofNullable(userRepository.findByUserName(incomingDoc.userName()))
                .orElseThrow(() -> new UserNotFoundException("Wrong userName: No user found for the given userName : " + incomingDoc.userName()));

        Documents savedDocument = saveDocumentAndSendNotification(incomingDoc, uploadedFile, user);

        return getDocumentResponseDto(savedDocument);
    }

    @Transactional
    private Documents saveDocumentAndSendNotification(final DocumentRequestDto incomingDoc, final MultipartFile uploadedFile, final Users user) {
        final String baseNameOfTheFile = FilenameUtils.getBaseName(uploadedFile.getOriginalFilename());
        Documents document = Documents.builder()
                .name(baseNameOfTheFile)
                .documentType(incomingDoc.documentType())
                .fileType(uploadedFile.getContentType())
                .expiryDate(incomingDoc.expiryDate())
                .user(user)
                .verified(true)
                .build();

        Documents savedDocument = documentsRepository.save(document);
        if(savedDocument.isVerified()) {
            notificationService.sendNotification(user, savedDocument);
        }
        return savedDocument;
    }

    private static DocumentResponseDto getDocumentResponseDto(final Documents savedDocument) {
        return DocumentResponseDto.builder()
                .id(savedDocument.getId())
                .name(savedDocument.getName())
                .documentType(savedDocument.getDocumentType())
                .userName(savedDocument.getUser().getUserName())
                .fileType(savedDocument.getFileType())
                .expiryDate(savedDocument.getExpiryDate())
                .verified(savedDocument.isVerified())
                .build();
    }

}
