package com.example.iotbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private Boolean verified = false;

    private String role = "USER";

    @OneToMany(mappedBy = "owner")
    private List<Device> ownedDevices;

    private LocalDateTime createdAt = LocalDateTime.now();
}