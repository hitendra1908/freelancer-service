package com.api.freelancer.controller;

import com.api.freelancer.document.DocumentApi;
import com.api.freelancer.document.DocumentDto;
import com.api.freelancer.document.DocumentRequestDto;
import com.api.freelancer.model.Documents;
import com.api.freelancer.service.DocumentsService;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/freelancer")
public class DocumentsController implements DocumentApi {

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
        if(file.isEmpty()) {
         throw new RuntimeException("File is empty"); //TODO create custom exception
        }
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
            throw new RuntimeException("throw proper customer error with msg");
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

    private void validateDocument(Documents documents) {
        String username = documents.getUserName();

        if (!documents.getName().startsWith(username)) {
            throw new IllegalArgumentException("Document name must start with the owner's username.");
        }
        if (documents.getExpiryDate().isAfter(LocalDate.now().plusMonths(2))) {
            throw new IllegalArgumentException("Document expiry date must be at least 2 months from now.");
        }
        documents.setVerified(true);
    }

}
