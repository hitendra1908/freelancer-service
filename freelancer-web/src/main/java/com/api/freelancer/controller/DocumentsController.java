package com.api.freelancer.controller;

import com.api.freelancer.document.DocumentApi;
import com.api.freelancer.document.DocumentDto;
import com.api.freelancer.document.DocumentRequestDto;
import com.api.freelancer.exception.document.DocumentExpiryException;
import com.api.freelancer.exception.document.DocumentNameException;
import com.api.freelancer.exception.document.FileException;
import com.api.freelancer.exception.document.FileNotFoundException;
import com.api.freelancer.exception.document.UnSupportedFileFormatException;
import com.api.freelancer.model.Documents;
import com.api.freelancer.service.DocumentsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/freelancer")
public class DocumentsController implements DocumentApi {

    private static final List<String> SUPPORTED_FORMAT = Arrays.asList("pdf", "jpeg", "jpg");

    @Autowired
    DocumentsService documentsService;

    @GetMapping("/documents/{id}")
    @Override
    public ResponseEntity<DocumentDto> getDocument(@PathVariable Long id) {
        Optional<Documents> documents = documentsService.findById(id);
        if(documents.isPresent()) {
            var doc = documents.get();
            DocumentDto result = new DocumentDto(doc.getId(), doc.getName(),
                    doc.getDocumentType(),doc.getUserName(),
                    doc.getFileType(), doc.getExpiryDate(),
                    doc.isVerified() );
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/documents", consumes = {MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON})
    @Override
    public ResponseEntity<DocumentDto> uploadDocument(@RequestPart(value = "request") DocumentRequestDto documentRequest,
                                                      @RequestPart(value = "file") MultipartFile file) {
        validatedFile(file);
        DocumentDto result;
        Documents savedDocument;
        try {
            Documents document = Documents.builder()
                    .name(file.getOriginalFilename())
                    .documentType(documentRequest.documentType())
                    .content(file.getBytes())
                    .fileType(file.getContentType())
                    .expiryDate(documentRequest.expiryDate())
                    .userName(documentRequest.userName())
                    .build();
            validateDocument(document);
            savedDocument = documentsService.save(document);
        } catch (IOException exception) {
            throw new FileException("Error occurred while reading the file");
        }

        result = new DocumentDto(savedDocument.getId(),
                savedDocument.getName(),
                savedDocument.getDocumentType(),
                savedDocument.getUserName(),
                savedDocument.getFileType(),
                savedDocument.getExpiryDate(),
                savedDocument.isVerified());
        return ResponseEntity.ok(result);

    }

    private void validatedFile(final MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.error("No file found in the request");
            throw new FileNotFoundException("No file found in the request");
        }
        final String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        if (!SUPPORTED_FORMAT.contains(extension)) {
            log.error("Requested file format not supported.");
            throw new UnSupportedFileFormatException(String.format("Unsupported file: %s format not supported", extension));
        }
    }

    private void validateDocument(Documents documents) {
        String username = documents.getUserName();

        if (!documents.getName().startsWith(username)) {
            log.error("Wrong document name");
            throw new DocumentNameException("Document name must start with the owner's username");
        }
        if (documents.getExpiryDate().isBefore(LocalDate.now().plusMonths(2))) {
            log.error("Document expiring too soon");
            throw new DocumentExpiryException("Document expiry date must be at least 2 months from now");
        }
        documents.setVerified(true);
    }

}
