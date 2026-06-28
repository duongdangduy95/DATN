package com.example.iotbackend.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    private Long deviceId;
    private String deviceName;
    private String actorName;
    private String recipientName;
    private String title;
    private String content;
    private LocalDateTime createdAt;
}