package com.api.freelancer.service;

import com.api.freelancer.kafka.KafkaProducer;
import com.api.freelancer.model.Documents;
import com.api.freelancer.model.Notification;
import com.api.freelancer.model.Users;
import com.api.freelancer.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private NotificationService notificationService;

    private Users validUser;
    private Documents validDocument;

    @BeforeEach
    void setUp() {
        validUser = Users.builder()
                .userName("ironMan")
                .firstName("Tony")
                .lastName("Stark")
                .email("tony.stark@example.com")
                .build();
        validDocument = Documents.builder()
                .id(1L)
                .name("testUser_document.pdf")
                .documentType("testDocType")
                .user(validUser)
                .fileType("application/pdf")
                .expiryDate(LocalDate.now().plusMonths(3))
                .verified(true)
                .build();
    }

    @Test
    void testSendNotification() {
        String message = "Document : testUser_document.pdf for user: ironMan successfully uploaded and verified.";
        notificationService.sendNotification(validUser, validDocument.getName(), message);

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(1)).save(notificationCaptor.capture());
        Notification savedNotification = notificationCaptor.getValue();

        assertEquals(validUser, savedNotification.getReceiver());
        assertEquals(validDocument.getName(), savedNotification.getDocumentName());
        assertEquals(LocalDateTime.now().getDayOfMonth(), savedNotification.getTimestamp().getDayOfMonth());

        verify(kafkaProducer, times(1)).sendMessage(message);
        verify(notificationRepository, times(1)).save(any());
    }
}