package com.example.iotbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShareDeviceRequest {

    private Long deviceId;

    private String email;
}