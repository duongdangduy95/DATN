package com.example.iotbackend.dto;

import lombok.Data;

@Data
public class PairDeviceRequest {

    private String deviceCode;

    private String name;

    private Long groupId;
}