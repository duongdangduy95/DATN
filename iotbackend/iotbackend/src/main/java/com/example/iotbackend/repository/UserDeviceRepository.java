package com.example.iotbackend.repository;

import com.example.iotbackend.entity.User;
import com.example.iotbackend.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

    void deleteByDeviceId(Long id);

    boolean existsByUserIdAndDeviceId(Long id, Long id1);

    List<UserDevice> findByUserId(Long id);

    boolean existsByDeviceId(Long id);

    List<UserDevice> findByDeviceId(Long deviceId);

//    List<Device> findByUserId(Long id);
    Optional<UserDevice> findByUserIdAndDeviceId(Long userId, Long deviceId);

    void deleteByUserIdAndDeviceId(Long userId, Long deviceId);

    List<UserDevice> findByUser(User user);

}
