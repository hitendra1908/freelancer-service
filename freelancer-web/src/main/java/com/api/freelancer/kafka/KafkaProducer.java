package com.api.freelancer.kafka;

import com.api.freelancer.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private static final String TOPIC = "freelancer_notification";

    private final KafkaTemplate<String, Notification> kafkaTemplate;

    public void sendMessage(Notification message) {
        kafkaTemplate.send(TOPIC, message);
    }
}
