package com.example.iotbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeviceGuestResponse {

    private Long userId;

    private String username;

    private String email;

    private String role;
}