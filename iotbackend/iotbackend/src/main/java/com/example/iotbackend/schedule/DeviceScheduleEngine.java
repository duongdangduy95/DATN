package com.example.iotbackend.schedule;

import com.example.iotbackend.service.DeviceScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeviceScheduleEngine {

    private final DeviceScheduleService service;

    @Scheduled(fixedRate = 3000)
    public void run() {
        service.processSchedules();
    }
}