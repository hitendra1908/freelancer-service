package com.api.freelancer.service;

import com.api.freelancer.kafka.KafkaProducer;
import com.api.freelancer.model.Documents;
import com.api.freelancer.model.Notification;
import com.api.freelancer.model.Users;
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

    public void sendNotification(Users user, Documents document) {
        Notification notification = Notification.builder()
                        .receiver(user)
                        .document(document)
                        .timestamp(LocalDateTime.now())
                        .build();

        notificationRepository.save(notification);

        kafkaProducer.sendMessage("Document : "+document.getName() +" for user: "+user.getUserName() + " successfully uploaded and verified.");

        log.info("message sent to kafka topic!!");

    }
}
