package com.example.iotbackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceResponse {

    private Long id;

    private String deviceCode;

    private String name;

    private String type;

    private String status;

    private Boolean online;

    private String role;
}