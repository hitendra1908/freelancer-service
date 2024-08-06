package com.api.freelancer.controller;

import com.api.freelancer.document.DocumentApi;
import com.api.freelancer.document.DocumentResponseDto;
import com.api.freelancer.document.DocumentRequestDto;
import com.api.freelancer.service.DocumentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("/api/freelancer")
public class DocumentsController implements DocumentApi {

    @Autowired
    DocumentsService documentsService;

    @PostMapping(value = "/documents", consumes = {MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON})
    @Override
    public ResponseEntity<DocumentResponseDto> uploadDocument(@RequestPart(value = "request") DocumentRequestDto documentRequest,
                                                              @RequestPart(value = "file") MultipartFile file) {
        DocumentResponseDto document = documentsService.createDocument(documentRequest, file);
        return ResponseEntity.ok(document);

    }
}
