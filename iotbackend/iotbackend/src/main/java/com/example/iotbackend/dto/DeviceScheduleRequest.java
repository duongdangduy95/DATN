package com.example.iotbackend.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceScheduleRequest {

    private Long deviceId;

    private LocalTime startTime;

    private LocalTime endTime;

    private String type;

    private String daysOfWeek;

    private LocalDate executeDate;

    private Boolean enabled;
}