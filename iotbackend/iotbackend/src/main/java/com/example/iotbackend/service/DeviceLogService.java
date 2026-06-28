package com.example.iotbackend.service;

import com.example.iotbackend.dto.DeviceLogResponse;
import com.example.iotbackend.entity.*;
import com.example.iotbackend.repository.DeviceLogRepository;
import com.example.iotbackend.repository.UserDeviceRepository;
import com.example.iotbackend.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceLogService {

    private final DeviceLogRepository deviceLogRepository;
    private final UserDeviceRepository userDeviceRepository;
    private final SecurityUtils securityUtils;

    public void saveLog(Device device, User actor, User targetUser, String action, String detail, String source) {

        DeviceLog log = DeviceLog.builder()
                .device(device)
                .actor(actor)
                .targetUser(targetUser)
                .action(action)
                .detail(detail)
                .source(source)
                .createdAt(LocalDateTime.now())
                .build();

        deviceLogRepository.save(log);
    }

    public List<DeviceLogResponse> getMyLogs() {

        User user = securityUtils.getCurrentUser();

        List<Long> deviceIds = userDeviceRepository.findByUserId(user.getId())
                .stream()
                .map(ud -> ud.getDevice().getId())
                .toList();

        return deviceLogRepository.findByDevice_IdInOrderByCreatedAtDesc(deviceIds)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private DeviceLogResponse toResponse(DeviceLog log) {

        return DeviceLogResponse.builder()
                .id(log.getId())
                .deviceId(log.getDevice() != null ? log.getDevice().getId() : null)
                .deviceName(log.getDevice() != null ? log.getDevice().getName() : null)
                .actorName(log.getActor() != null ? log.getActor().getUsername() : null)
                .targetUserName(log.getTargetUser() != null ? log.getTargetUser().getUsername() : null)
                .action(log.getAction())
                .detail(log.getDetail())
                .source(log.getSource())
                .createdAt(log.getCreatedAt())
                .build();
    }
}