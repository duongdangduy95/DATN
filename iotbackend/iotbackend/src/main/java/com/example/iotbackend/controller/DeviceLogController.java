package com.example.iotbackend.controller;

import com.example.iotbackend.dto.DeviceLogResponse;
import com.example.iotbackend.service.DeviceLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class DeviceLogController {

    private final DeviceLogService deviceLogService;

    @GetMapping
    public List<DeviceLogResponse> getMyLogs() {
        return deviceLogService.getMyLogs();
    }
}