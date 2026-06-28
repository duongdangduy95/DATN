package com.example.iotbackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceRegisterDTO {

    private String deviceId;

    private String type;

    private String ip;

    private Integer rssi;

    private String mac;

    // THÊM
    private String pairToken;
}