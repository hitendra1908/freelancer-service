package com.api.freelancer.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaProducerTest {

    private static final String TOPIC = "freelancer_notification";
    private static final String MESSAGE = "Test message";

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private KafkaProducer kafkaProducer;

    @Test
    void sendMessage_shouldCallKafkaTemplateSendWithCorrectTopicAndMessage() {
        kafkaProducer.sendMessage(MESSAGE);

        verify(kafkaTemplate).send(TOPIC, MESSAGE);
    }
}
