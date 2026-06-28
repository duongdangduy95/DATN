package com.example.iotbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "devices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String deviceCode;

    private String name;

    private String type;

    private String status = "OFF";

    private Boolean online = false;

    private String firmware;

    private Integer rssi;

    private String ipAddress;

    @Column(unique = true)
    private String macAddress;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private DeviceGroup group;

    // USER SỞ HỮU CHÍNH
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    // ĐÃ PAIR HAY CHƯA
    private Boolean paired = false;

    private LocalDateTime lastSeen;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}