package com.example.iotbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "device_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // thiết bị bị tác động
    @ManyToOne
    private Device device;

    // người thực hiện hành động
    @ManyToOne
    private User actor;

    // người nhận thông báo (nếu có)
    @ManyToOne
    private User targetUser;

    // loại hành động
    private String action;

    // mô tả
    private String detail;

    private String source;

    private LocalDateTime createdAt;
}