package com.api.freelancer.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Notification {

    private String receiver;

    private String documentName;

    private LocalDateTime timestamp;
}
