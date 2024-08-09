package com.api.freelancer.kafka;

import com.api.freelancer.model.Notification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaProducerTest {

    private static final String TOPIC = "freelancer_notification";
    Notification notification = Notification.builder()
            .receiver("ironMan")
                .documentName("testUser_document")
                .timestamp(LocalDateTime.now())
            .build();

    @Mock
    private KafkaTemplate<String, Notification> kafkaTemplate;

    @InjectMocks
    private KafkaProducer kafkaProducer;

    @Test
    void sendMessage_shouldCallKafkaTemplateSendWithCorrectTopicAndMessage() {
        kafkaProducer.sendMessage(notification);

        verify(kafkaTemplate).send(TOPIC, notification);
    }
}
