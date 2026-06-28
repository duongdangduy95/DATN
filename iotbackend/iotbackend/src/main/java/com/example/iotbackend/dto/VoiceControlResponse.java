package com.example.iotbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VoiceControlResponse {

    private boolean success;

    private String message;
}