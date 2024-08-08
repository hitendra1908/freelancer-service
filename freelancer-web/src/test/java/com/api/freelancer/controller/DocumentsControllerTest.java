package com.api.freelancer.controller;

import com.api.freelancer.document.DocumentRequestDto;
import com.api.freelancer.document.DocumentResponseDto;
import com.api.freelancer.service.DocumentsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentsControllerTest {

    @Mock
    private DocumentsService documentsService;

    @InjectMocks
    private DocumentsController documentsController;

    @Mock
    private MockMultipartFile multipartFile;

    private DocumentRequestDto documentRequestDto;
    private DocumentResponseDto documentResponseDto;

    @BeforeEach
    void setup() {
        documentRequestDto = DocumentRequestDto.builder()
                .documentType("testDocType")
                .userName("testUser")
                .expiryDate(LocalDate.now().plusMonths(3))
                .build();

        documentResponseDto = DocumentResponseDto.builder()
                .id(1L)
                .userName("testUser")
                .name("testUser_document")
                .documentType("testDocType")
                .fileType("application/pdf")
                .expiryDate(LocalDate.now().plusMonths(3))
                .verified(true)
                .build();
    }

    @Test
    void shouldCreateDocument() {
        when(documentsService.createDocument(documentRequestDto, multipartFile)).thenReturn(documentResponseDto);

        ResponseEntity<DocumentResponseDto> response = documentsController.uploadDocument(documentRequestDto,
                multipartFile);

        assertEquals(ResponseEntity.ok(documentResponseDto), response);
        verify(documentsService, times(1)).createDocument(documentRequestDto, multipartFile);
    }

    @Test
    public void shouldUpdateDocument() {

        when(documentsService.updateDocument(1L, documentRequestDto, multipartFile))
                .thenReturn(documentResponseDto);

        ResponseEntity<DocumentResponseDto> response =
                documentsController.updateDocument(1L, documentRequestDto, multipartFile);

        assertEquals(ResponseEntity.ok(documentResponseDto), response);
        verify(documentsService, times(1))
                .updateDocument(1L, documentRequestDto, multipartFile);
    }

    @Test
    public void testGetDocument() {

        when(documentsService.getDocument(1L)).thenReturn(documentResponseDto);

        ResponseEntity<DocumentResponseDto> response = documentsController.getDocument(1L);

        assertEquals(ResponseEntity.ok(documentResponseDto), response);
        verify(documentsService, times(1)).getDocument(1L);
    }

    @Test
    public void testDeleteDocument() {
        Long id = 1L;

        doNothing().when(documentsService).deleteDocument(1L);

        ResponseEntity<Void> response = documentsController.deleteDocument(id);

        assertEquals(ResponseEntity.noContent().build(), response);
        verify(documentsService, times(1)).deleteDocument(id);
    }
}
