package com.example.iotbackend.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@Getter
@Setter
public class DeviceLogResponse {

    private Long id;

    private Long deviceId;

    private String deviceName;

    private String actorName;

    private String targetUserName;

    private String action;

    private String detail;

    private String source;

    private LocalDateTime createdAt;
}