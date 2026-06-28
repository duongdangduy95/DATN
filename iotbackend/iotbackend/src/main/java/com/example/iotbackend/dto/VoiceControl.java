package com.example.iotbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoiceControl {
    private Long deviceId;

    private Boolean state;
}
