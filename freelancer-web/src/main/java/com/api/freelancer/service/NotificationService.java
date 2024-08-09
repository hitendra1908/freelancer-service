package com.api.freelancer.service;

import com.api.freelancer.kafka.KafkaProducer;
import com.api.freelancer.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationService {

    private final KafkaProducer kafkaProducer;

    public void sendNotification(Notification notification) {

        kafkaProducer.sendMessage(notification);

        log.info("message sent to kafka topic --> {}", notification);

    }
}
