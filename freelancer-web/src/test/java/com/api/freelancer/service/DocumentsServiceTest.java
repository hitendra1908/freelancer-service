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
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Captor
    private ArgumentCaptor<Documents> documentCaptor;

    private DocumentRequestDto documentRequestDto;
    private MockMultipartFile multipartFile;
    private Users user;
    private Documents document;


    @BeforeEach
    void setup() {
        documentRequestDto = new DocumentRequestDto("testDocType", "testUser", LocalDate.now().plusMonths(3));
        multipartFile = new MockMultipartFile("file", "testUser_document.pdf", "application/pdf", "test content".getBytes());
        user = new Users();
        user.setUserName("testUser");
        document = Documents.builder()
                .id(1L)
                .name("testUser_document")
                .documentType("testDocType")
                .user(user)
                .fileType("application/pdf")
                .expiryDate(LocalDate.now().plusMonths(3))
                .verified(true)
                .build();
    }

    @Test
    void shouldSuccessfullyCreateDocument() {
        final String expectedMessage = "Document: testUser_document for user: testUser is successfully uploaded and verified.";

        when(userRepository.findByUserName("testUser")).thenReturn(Optional.of(user));
        when(documentsRepository.save(any(Documents.class))).thenReturn(document);

        DocumentResponseDto responseDto = documentsService.createDocument(documentRequestDto, multipartFile);

        assertNotNull(responseDto);
        assertEquals(1L, responseDto.id());
        assertEquals("testUser_document", responseDto.name());
        assertEquals("testDocType", responseDto.documentType());
        assertEquals("testUser", responseDto.userName());
        assertEquals("application/pdf", responseDto.fileType());
        assertEquals(LocalDate.now().plusMonths(3), responseDto.expiryDate());
        assertTrue(responseDto.verified());
        verify(notificationService, times(1)).sendNotification(user, document.getName(), expectedMessage);
    }

    @Test
    void should_ThrowDuplicateDocumentException_WhenUploadingSameDocument() {
        when(userRepository.findByUserName("testUser")).thenReturn(Optional.of(user));
        when(documentsRepository.save(any(Documents.class))).thenThrow(DataIntegrityViolationException.class);

        DuplicateDocumentException exception = assertThrows(DuplicateDocumentException.class, () -> documentsService.createDocument(documentRequestDto, multipartFile));

        assertEquals("Document with name: testUser_document already exists", exception.getMessage());
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhileCreatingADocument_whenEmptyUser() {
        when(userRepository.findByUserName("testUser")).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> documentsService.createDocument(documentRequestDto, multipartFile));

        assertEquals("Wrong userName: No user found for the given userName: testUser", exception.getMessage());
        verifyNoInteractions(documentsRepository);
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhileUpdatingADocument_whenEmptyUser() {
        when(userRepository.findByUserName("testUser")).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> documentsService.updateDocument(1L, documentRequestDto, multipartFile));

        assertEquals("Wrong userName: No user found for the given userName: testUser", exception.getMessage());
        verifyNoInteractions(documentsRepository);
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldUpdateDocument() {
        final String expectedMessage = "Document: testUser_document for user: testUser is successfully updated and verified.";
        DocumentRequestDto updatedRequestDto = new DocumentRequestDto("updatedDocType", "testUser", LocalDate.now().plusMonths(3));
        when(userRepository.findByUserName("testUser")).thenReturn(Optional.of(user));
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(document));
        when(documentsRepository.save(any(Documents.class))).thenReturn(document);

        DocumentResponseDto response = documentsService.updateDocument(1L, updatedRequestDto, multipartFile);

        assertEquals("updatedDocType", response.documentType());
        verify(notificationService, times(1)).sendNotification(user, document.getName(), expectedMessage);
    }

    @Test
    void updateDocument_DocumentNotFound() {
        when(userRepository.findByUserName("testUser")).thenReturn(Optional.of(user));
        when(documentsRepository.findById(1L)).thenReturn(Optional.empty());

        DocumentNotFoundException exception = assertThrows(DocumentNotFoundException.class, () ->
                documentsService.updateDocument(1L, documentRequestDto, multipartFile));

        assertEquals("Document you are trying to update is not found.", exception.getMessage());
    }

    @Test
    void getDocument_Success() {
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(document));

        DocumentResponseDto response = documentsService.getDocument(1L);

        assertEquals("testUser_document", response.name());
        assertEquals("testDocType", response.documentType());
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
        verify(notificationService, times(1)).sendNotification(user, document.getName(), expectedMessage);
    }

    @Test
    void deleteDocument_DocumentNotFound() {
        when(documentsRepository.findById(1L)).thenReturn(Optional.empty());

        DocumentNotFoundException exception = assertThrows(DocumentNotFoundException.class, () ->
                documentsService.deleteDocument(1L));

        assertEquals("Document you are trying to delete is not found.", exception.getMessage());
    }


}
