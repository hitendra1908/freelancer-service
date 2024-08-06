package com.api.freelancer.controller;

import com.api.freelancer.document.DocumentApi;
import com.api.freelancer.document.DocumentRequestDto;
import com.api.freelancer.document.DocumentResponseDto;
import com.api.freelancer.service.DocumentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.core.MediaType;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/freelancer")
public class DocumentsController implements DocumentApi {

    private final DocumentsService documentsService;

    @PostMapping(value = "/documents", consumes = {MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON} )
    @Override
    public ResponseEntity<DocumentResponseDto> uploadDocument(
            @RequestPart(value = "request") DocumentRequestDto documentRequest,
            @RequestPart(value = "file") MultipartFile file) {
        DocumentResponseDto document = documentsService.createDocument(documentRequest, file);
        return ResponseEntity.ok(document);

    }

    @PutMapping(value = "/documents/{id}",
                consumes = {MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON},
                produces = {MediaType.APPLICATION_JSON } )
    @Override
    public ResponseEntity<DocumentResponseDto> updateDocument(
            @PathVariable("id") Long id,
            @RequestPart(value = "request") DocumentRequestDto documentRequest,
            @RequestPart(value = "file") MultipartFile file) {
        DocumentResponseDto document = documentsService.updateDocument(id, documentRequest, file);
        return ResponseEntity.ok(document);
    }

    @GetMapping(value = "/documents/{id}", produces = {MediaType.APPLICATION_JSON } )
    @Override
    public ResponseEntity<DocumentResponseDto> getDocument(@PathVariable("id") Long id) {
        DocumentResponseDto document = documentsService.getDocument(id);
        return ResponseEntity.ok(document);
    }

    @DeleteMapping(value = "/documents/{id}")
    @Override
    public ResponseEntity<Void> deleteDocument(@PathVariable("id") Long id) {
        documentsService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
