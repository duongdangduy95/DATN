package com.example.iotbackend.controller;

import com.example.iotbackend.dto.VoiceControlResponse;
import com.example.iotbackend.service.VoiceControlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/voice")
@RequiredArgsConstructor
public class VoiceController {

    private final VoiceControlService voiceControlService;

    @PostMapping("/control")
    public ResponseEntity<VoiceControlResponse> control(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(voiceControlService.processVoice(file));
    }
}