package com.example.iotbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDeviceRequest {

    private String deviceCode;

    private String name;

    private String type;

    private Long groupId;
}