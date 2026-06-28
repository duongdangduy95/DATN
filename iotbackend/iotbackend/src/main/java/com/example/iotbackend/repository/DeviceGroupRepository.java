package com.example.iotbackend.repository;

import com.example.iotbackend.entity.DeviceGroup;
import com.example.iotbackend.entity.Home;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceGroupRepository extends JpaRepository<DeviceGroup, Long> {

}
