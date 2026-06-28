package com.example.iotbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "device_schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Long deviceId;

    private Long userId;

    private LocalTime startTime;

    private LocalTime endTime;

    // ONE_TIME / DAILY / WEEKLY
    private String type;

    // MON,TUE,WED
    private String daysOfWeek;

    private LocalDate executeDate;

    private Boolean enabled = true;

    private LocalDate lastRunStart;
    private LocalDate lastRunEnd;
}