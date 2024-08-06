package com.api.freelancer.validator;

import com.api.freelancer.document.DocumentRequestDto;
import com.api.freelancer.exception.document.DocumentExpiryException;
import com.api.freelancer.exception.document.DocumentNameException;
import com.api.freelancer.exception.document.FileNotFoundException;
import com.api.freelancer.exception.document.UnSupportedFileFormatException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentValidatorTest {

    @Test
    void testCreateDocumentDocument_InvalidDocumentName() {

        DocumentRequestDto requestDto = new DocumentRequestDto( "testDocType","testUser", LocalDate.now().plusMonths(3));
        MockMultipartFile file = new MockMultipartFile("file", "invalid_document.pdf", "application/pdf", "test content".getBytes());


        DocumentNameException exception = assertThrows(DocumentNameException.class, () -> DocumentValidator.validateDocument(requestDto, file));
        assertEquals("Document name must start with the owner's username", exception.getMessage());
    }

    @Test
    void testCreateDocumentDocument_DocumentExpiringSoon() {

        DocumentRequestDto requestDto = new DocumentRequestDto( "testDocType", "testUser", LocalDate.now().plusMonths(1));
        MockMultipartFile file = new MockMultipartFile("file", "testUser_document.pdf", "application/pdf", "test content".getBytes());

        DocumentExpiryException exception = assertThrows(DocumentExpiryException.class, () -> DocumentValidator.validateDocument(requestDto, file));
        assertEquals("Document expiry date must be at least 2 months from now", exception.getMessage());
    }

    @Test
    void testCreateDocumentDocument_InvalidFileFormat() {

        DocumentRequestDto requestDto = new DocumentRequestDto( "testDocType", "testUser", LocalDate.now().plusMonths(3));
        MockMultipartFile file = new MockMultipartFile("file", "testUser_document.txt", "text/plain", "test content".getBytes());

        UnSupportedFileFormatException exception = assertThrows(UnSupportedFileFormatException.class, () -> DocumentValidator.validateDocument(requestDto, file));
        assertEquals("Unsupported file: txt format not supported", exception.getMessage());
    }

    @Test
    void testCreateDocumentDocument_FileNotFound() {

        DocumentRequestDto requestDto = new DocumentRequestDto("testDocType", "testUser", LocalDate.now().plusMonths(3));

        FileNotFoundException exception = assertThrows(FileNotFoundException.class, () -> DocumentValidator.validateDocument(requestDto, null));
        assertEquals("No file found in the request", exception.getMessage());
    }

}