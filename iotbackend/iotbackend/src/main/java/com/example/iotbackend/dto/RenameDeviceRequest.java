package com.example.iotbackend.dto;

import lombok.Data;

@Data
public class RenameDeviceRequest {

    private Long deviceId;

    private String name;
}
