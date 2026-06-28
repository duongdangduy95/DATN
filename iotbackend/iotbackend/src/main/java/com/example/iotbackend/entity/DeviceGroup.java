package com.example.iotbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "device_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "group")
    private List<Device> devices;

    private LocalDateTime createdAt = LocalDateTime.now();
}