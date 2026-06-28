package com.example.iotbackend.controller;

import com.example.iotbackend.dto.NotificationResponse;
import com.example.iotbackend.entity.Notification;
import com.example.iotbackend.entity.User;
import com.example.iotbackend.repository.NotificationRepository;
import com.example.iotbackend.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository repository;
    private final SecurityUtils securityUtils;

    @GetMapping
    public List<NotificationResponse> getMyNotifications() {

        User user = securityUtils.getCurrentUser();

        return repository.findByRecipientIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private NotificationResponse toResponse(Notification n) {

        return NotificationResponse.builder()
                .deviceId(n.getDevice() != null ? n.getDevice().getId() : null)
                .deviceName(n.getDevice() != null ? n.getDevice().getName() : null)
                .actorName(n.getActor() != null ? n.getActor().getUsername() : null)
                .recipientName(n.getRecipient() != null ? n.getRecipient().getUsername() : null)
                .title(n.getTitle())
                .content(n.getContent())
                .createdAt(n.getCreatedAt())
                .build();
    }
}