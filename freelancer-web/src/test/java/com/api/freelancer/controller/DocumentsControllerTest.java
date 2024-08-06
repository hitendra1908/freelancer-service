package com.api.freelancer.controller;

import com.api.freelancer.document.DocumentRequestDto;
import com.api.freelancer.document.DocumentResponseDto;
import com.api.freelancer.service.DocumentsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentsControllerTest {

    @Mock
    private DocumentsService documentsService;

    @InjectMocks
    private DocumentsController documentsController;

    @Test
    void uploadDocument() {

        DocumentRequestDto requestDto = new DocumentRequestDto( "testDocType", "testUser", LocalDate.now().plusMonths(3));
        MockMultipartFile file = new MockMultipartFile("file", "testUser_document.pdf", "application/pdf", "test content".getBytes());


        DocumentResponseDto documentResponseDto = new DocumentResponseDto(1L,
                "testUser_document.pdf",
                "testDocType",
                "testUser",
                "application/pdf",
                LocalDate.now().plusMonths(3),
                true
        );

        when(documentsService.createDocument(requestDto, file)).thenReturn(documentResponseDto);

        DocumentResponseDto actualResponse = documentsController.uploadDocument(requestDto, file).getBody();

        assertNotNull(actualResponse);
        assertEquals("testUser_document.pdf", actualResponse.name());
        assertEquals("testUser", actualResponse.userName());

    }
}
