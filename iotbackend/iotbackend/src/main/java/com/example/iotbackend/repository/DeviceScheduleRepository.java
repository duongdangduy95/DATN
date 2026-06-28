package com.example.iotbackend.repository;

import com.example.iotbackend.entity.DeviceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeviceScheduleRepository
        extends JpaRepository<DeviceSchedule, Long> {

    List<DeviceSchedule> findByUserId(Long userId);

    List<DeviceSchedule> findByEnabledTrue();

    List<DeviceSchedule> findByDeviceId(Long deviceId);
}