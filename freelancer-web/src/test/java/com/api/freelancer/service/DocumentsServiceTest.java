package com.api.freelancer.service;

import com.api.freelancer.document.DocumentRequestDto;
import com.api.freelancer.document.DocumentResponseDto;
import com.api.freelancer.exception.document.DocumentExpiryException;
import com.api.freelancer.exception.document.DocumentNameException;
import com.api.freelancer.exception.document.FileNotFoundException;
import com.api.freelancer.exception.document.UnSupportedFileFormatException;
import com.api.freelancer.exception.user.UserNotFoundException;
import com.api.freelancer.model.Documents;
import com.api.freelancer.model.Users;
import com.api.freelancer.repository.DocumentsRepository;
import com.api.freelancer.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    @Test
    void testCreateDocumentDocument_Success() {
        DocumentRequestDto requestDto = new DocumentRequestDto("testDocType", "testUser", LocalDate.now().plusMonths(3));
        MockMultipartFile file = new MockMultipartFile("file", "testUser_document.pdf", "application/pdf", "test content".getBytes());

        Users user = new Users();
        user.setUserName("testUser");

        Documents document = Documents.builder()
                .id(1L)
                .name("testUser_document.pdf")
                .documentType("testDocType")
                .user(user)
                .fileType("application/pdf")
                .expiryDate(LocalDate.now().plusMonths(3))
                .verified(true)
                .build();

        when(userRepository.findByUserName("testUser")).thenReturn(user);
        when(documentsRepository.save(any(Documents.class))).thenReturn(document);

        DocumentResponseDto responseDto = documentsService.createDocument(requestDto, file);

        assertNotNull(responseDto);
        assertEquals(1L, responseDto.id());
        assertEquals("testUser_document.pdf", responseDto.name());
        assertEquals("testDocType", responseDto.documentType());
        assertEquals("testUser", responseDto.userName());
        assertEquals("application/pdf", responseDto.fileType());
        assertEquals(LocalDate.now().plusMonths(3), responseDto.expiryDate());
        assertTrue(responseDto.verified());
        verify(notificationService, times(1)).sendNotification(user, document);
    }

    @Test
    void testCreateDocumentDocument_UserNotFound() {

        DocumentRequestDto requestDto = new DocumentRequestDto( "testDocType", "testUser", LocalDate.now().plusMonths(3));
        MockMultipartFile file = new MockMultipartFile("file", "testUser_document.pdf", "application/pdf", "test content".getBytes());

        when(userRepository.findByUserName("testUser")).thenReturn(null);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> documentsService.createDocument(requestDto, file));
        assertEquals("Wrong userName: No user found for the given userName : testUser", exception.getMessage());
    }

    @Test
    void testCreateDocumentDocument_InvalidFileFormat() {

        DocumentRequestDto requestDto = new DocumentRequestDto( "testDocType", "testUser", LocalDate.now().plusMonths(3));
        MockMultipartFile file = new MockMultipartFile("file", "testUser_document.txt", "text/plain", "test content".getBytes());

        UnSupportedFileFormatException exception = assertThrows(UnSupportedFileFormatException.class, () -> documentsService.createDocument(requestDto, file));
        assertEquals("Unsupported file: txt format not supported", exception.getMessage());
    }

    @Test
    void testCreateDocumentDocument_InvalidDocumentName() {

        DocumentRequestDto requestDto = new DocumentRequestDto( "testDocType","testUser", LocalDate.now().plusMonths(3));
        MockMultipartFile file = new MockMultipartFile("file", "invalid_document.pdf", "application/pdf", "test content".getBytes());


        DocumentNameException exception = assertThrows(DocumentNameException.class, () -> documentsService.createDocument(requestDto, file));
        assertEquals("Document name must start with the owner's username", exception.getMessage());
    }

    @Test
    void testCreateDocumentDocument_DocumentExpiringSoon() {

        DocumentRequestDto requestDto = new DocumentRequestDto( "testDocType", "testUser", LocalDate.now().plusMonths(1));
        MockMultipartFile file = new MockMultipartFile("file", "testUser_document.pdf", "application/pdf", "test content".getBytes());

        DocumentExpiryException exception = assertThrows(DocumentExpiryException.class, () -> documentsService.createDocument(requestDto, file));
        assertEquals("Document expiry date must be at least 2 months from now", exception.getMessage());
    }

    @Test
    void testCreateDocumentDocument_FileNotFound() {

        DocumentRequestDto requestDto = new DocumentRequestDto("testDocType", "testUser", LocalDate.now().plusMonths(3));

        FileNotFoundException exception = assertThrows(FileNotFoundException.class, () -> documentsService.createDocument(requestDto, null));
        assertEquals("No file found in the request", exception.getMessage());
    }
}
