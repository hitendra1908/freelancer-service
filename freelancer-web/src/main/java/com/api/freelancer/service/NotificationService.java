package com.api.freelancer.service;

import com.api.freelancer.kafka.KafkaProducer;
import com.api.freelancer.model.Notification;
import com.api.freelancer.model.Freelancer;
import com.api.freelancer.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final KafkaProducer kafkaProducer;

    public void sendNotification(final Freelancer freelancer, final String documentName, final String message) {
        Notification notification = Notification.builder()
                        .receiver(freelancer)
                        .documentName(documentName)
                        .timestamp(LocalDateTime.now())
                        .build();

        notificationRepository.save(notification);

        kafkaProducer.sendMessage(message);

        log.info("message sent to kafka topic --> {}", message);

    }
}
