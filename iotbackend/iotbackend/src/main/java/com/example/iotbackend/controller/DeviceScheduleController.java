package com.example.iotbackend.controller;

import com.example.iotbackend.dto.DeviceScheduleRequest;
import com.example.iotbackend.entity.DeviceSchedule;
import com.example.iotbackend.repository.DeviceScheduleRepository;
import com.example.iotbackend.service.DeviceScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class DeviceScheduleController {

    private final DeviceScheduleService service;
    private final DeviceScheduleRepository repo;

    @PostMapping
    public DeviceSchedule create(@RequestBody DeviceScheduleRequest req) {
        return service.create(req);
    }

    @GetMapping("/device/{deviceId}")
    public List<DeviceSchedule> getSchedulesByDevice(@PathVariable Long deviceId) {
        return service.getSchedulesByDevice(deviceId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}