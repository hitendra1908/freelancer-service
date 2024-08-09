package com.api.freelancer.service;

import com.api.freelancer.kafka.KafkaProducer;
import com.api.freelancer.model.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void testSendNotification() {
        Notification notification = Notification.builder()
                .receiver("ironMan")
                .documentName("testUser_document")
                .timestamp(LocalDateTime.now())
                .build();

        notificationService.sendNotification(notification);

        verify(kafkaProducer, times(1)).sendMessage(notification);

    }
}