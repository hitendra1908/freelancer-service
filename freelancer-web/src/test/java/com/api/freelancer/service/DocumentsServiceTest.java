package com.api.freelancer.service;

import com.api.freelancer.document.DocumentRequestDto;
import com.api.freelancer.document.DocumentResponseDto;
import com.api.freelancer.entity.Documents;
import com.api.freelancer.entity.Freelancer;
import com.api.freelancer.exception.document.DocumentNotFoundException;
import com.api.freelancer.exception.document.DuplicateDocumentException;
import com.api.freelancer.model.Notification;
import com.api.freelancer.repository.DocumentsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentsServiceTest {

    @InjectMocks
    private DocumentsService documentsService;

    @Mock
    private DocumentsRepository documentsRepository;

    @Mock
    private FreelancerService freelancerService;

    @Mock
    private NotificationService notificationService;

    @Captor
    private ArgumentCaptor<Documents> documentCaptor;

    private DocumentRequestDto documentRequestDto;
    private MockMultipartFile multipartFile;
    private Freelancer freelancer;
    private Documents document;

    @BeforeEach
    void setup() {
        multipartFile = new MockMultipartFile("file", "testUser_document.pdf",
                "application/pdf", "test content".getBytes());

        documentRequestDto = DocumentRequestDto.builder()
                .documentType("testDocType")
                .userName("testUser")
                .expiryDate(LocalDate.now().plusMonths(3))
                .build();

        freelancer = new Freelancer();
        freelancer.setUserName("testUser");

        document = Documents.builder()
                .id(1L)
                .name("testUser_document")
                .documentType("testDocType")
                .freelancer(freelancer)
                .fileType("application/pdf")
                .expiryDate(LocalDate.now().plusMonths(3))
                .verified(true)
                .build();
    }

    @Test
    void shouldSuccessfullyCreateDocument() {
        when(freelancerService.findUserByUserName("testUser")).thenReturn(freelancer);
        when(documentsRepository.save(any(Documents.class))).thenReturn(document);

        DocumentResponseDto responseDto = documentsService.createDocument(documentRequestDto, multipartFile);

        assertNotNull(responseDto);
        assertEquals(1L, responseDto.getId());
        assertEquals("testUser_document", responseDto.getName());
        assertEquals("testDocType", responseDto.getDocumentType());
        assertEquals("testUser", responseDto.getUserName());
        assertEquals("application/pdf", responseDto.getFileType());
        assertEquals(LocalDate.now().plusMonths(3), responseDto.getExpiryDate());
        assertTrue(responseDto.isVerified());
        verify(notificationService, times(1)).sendNotification(any());
    }

    @Test
    void should_ThrowDuplicateDocumentException_WhenUploadingSameDocument() {
        when(freelancerService.findUserByUserName("testUser")).thenReturn(freelancer);
        when(documentsRepository.save(any(Documents.class))).thenThrow(DataIntegrityViolationException.class);

        DuplicateDocumentException exception = assertThrows(DuplicateDocumentException.class, () -> documentsService.createDocument(documentRequestDto, multipartFile));

        assertEquals("Document with name: testUser_document already exists", exception.getMessage());
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldUpdateDocument() {
        final String expectedMessage = "Document: testUser_document for user: testUser " +
                "is successfully updated and verified.";
        Notification notification = Notification.builder()
                .receiver("testUser")
                .documentName("updatedDocType")
                .timestamp(LocalDateTime.now())
                .build();
        DocumentRequestDto updatedRequestDto = DocumentRequestDto.builder()
                .documentType("updatedDocType")
                .userName("testUser")
                .expiryDate(LocalDate.now().plusMonths(3))
                .build();

        when(freelancerService.findUserByUserName("testUser")).thenReturn(freelancer);
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(document));
        when(documentsRepository.save(any(Documents.class))).thenReturn(document);

        DocumentResponseDto response = documentsService.updateDocument(1L, updatedRequestDto, multipartFile);

        assertEquals("updatedDocType", response.getDocumentType());

        verify(notificationService, times(1)).sendNotification(any());    }

    @Test
    void updateDocument_DocumentNotFound() {
        when(freelancerService.findUserByUserName("testUser")).thenReturn(freelancer);
        when(documentsRepository.findById(1L)).thenReturn(Optional.empty());

        DocumentNotFoundException exception = assertThrows(DocumentNotFoundException.class, () ->
                documentsService.updateDocument(1L, documentRequestDto, multipartFile));

        assertEquals("Document you are trying to update is not found.", exception.getMessage());
    }

    @Test
    void getDocument_Success() {
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(document));

        DocumentResponseDto response = documentsService.getDocument(1L);

        assertEquals("testUser_document", response.getName());
        assertEquals("testDocType", response.getDocumentType());
    }


    @Test
    void getDocument_DocumentNotFound() {
        when(documentsRepository.findById(1L)).thenReturn(Optional.empty());

        DocumentNotFoundException exception = assertThrows(DocumentNotFoundException.class, () ->
                documentsService.getDocument(1L));

        assertEquals("Document you are trying to find is not found.", exception.getMessage());
    }

    @Test
    void deleteDocument_Success() {
        final String expectedMessage = "Document: testUser_document for user: testUser is successfully deleted.";
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(document));

        documentsService.deleteDocument(1L);

        verify(documentsRepository).delete(documentCaptor.capture());
        assertEquals(document, documentCaptor.getValue());
        verify(notificationService, times(1)).sendNotification(any());
    }

    @Test
    void deleteDocument_DocumentNotFound() {
        when(documentsRepository.findById(1L)).thenReturn(Optional.empty());

        DocumentNotFoundException exception = assertThrows(DocumentNotFoundException.class, () ->
                documentsService.deleteDocument(1L));

        assertEquals("Document you are trying to delete is not found.", exception.getMessage());
    }
}
