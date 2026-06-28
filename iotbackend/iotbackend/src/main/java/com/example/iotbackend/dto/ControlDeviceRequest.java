package com.example.iotbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ControlDeviceRequest {

    private Long deviceId;

    private Boolean state;
}