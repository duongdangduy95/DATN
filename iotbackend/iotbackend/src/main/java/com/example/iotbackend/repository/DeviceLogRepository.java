package com.example.iotbackend.repository;

import com.example.iotbackend.entity.Device;
import com.example.iotbackend.entity.DeviceLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface DeviceLogRepository
        extends JpaRepository<DeviceLog, Long> {

    List<DeviceLog> findByDeviceIdOrderByCreatedAtDesc(Long deviceId);

    List<DeviceLog> findByDeviceInOrderByCreatedAtDesc(
            List<Device> devices
    );

    List<DeviceLog> findByDevice_IdInOrderByCreatedAtDesc(List<Long> deviceIds);
}