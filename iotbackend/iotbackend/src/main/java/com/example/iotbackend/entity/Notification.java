package com.example.iotbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // người nhận
    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @ManyToOne
    private Device device;

    // người thực hiện
    @ManyToOne
    @JoinColumn(name = "actor_id")
    private User actor;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Boolean isRead = false;



    private LocalDateTime createdAt;
}