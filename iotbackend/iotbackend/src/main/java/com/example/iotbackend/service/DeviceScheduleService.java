package com.example.iotbackend.service;

import com.example.iotbackend.dto.DeviceScheduleRequest;
import com.example.iotbackend.entity.*;
import com.example.iotbackend.mqtt.DeviceScheduleRunner;
import com.example.iotbackend.repository.DeviceRepository;
import com.example.iotbackend.repository.DeviceScheduleRepository;
import com.example.iotbackend.repository.UserDeviceRepository;
import com.example.iotbackend.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceScheduleService {

    private final DeviceScheduleRepository repo;
    private final DeviceScheduleRunner runner;
    private final SecurityUtils securityUtils;
    private final UserDeviceRepository userDeviceRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceLogService deviceLogService;
    private final NotificationService notificationService;

    public DeviceSchedule create(DeviceScheduleRequest req) {

        User user = securityUtils.getCurrentUser();

        boolean hasPermission = userDeviceRepository
                .existsByUserIdAndDeviceId(user.getId(), req.getDeviceId());

        if (!hasPermission) {
            throw new RuntimeException("Không có quyền tạo lịch cho thiết bị này");
        }

        Device device = deviceRepository.findById(req.getDeviceId())
                .orElseThrow(() -> new RuntimeException("Device not found"));

        DeviceSchedule s = new DeviceSchedule();
        s.setDeviceId(req.getDeviceId());
        s.setStartTime(req.getStartTime());
        s.setEndTime(req.getEndTime());
        s.setType(req.getType());
        s.setDaysOfWeek(req.getDaysOfWeek());
        s.setEnabled(req.getEnabled());
        s.setUserId(user.getId());

        DeviceSchedule saved = repo.save(s);

        String msg = user.getUsername() + " đã tạo lịch cho " + device.getName();

        deviceLogService.saveLog(device, user, null, "CREATE_SCHEDULE", msg, "MOBILE");

        notificationService.sendNotification(device, user, "Tạo lịch thành công", msg);

        return saved;
    }

    public List<DeviceSchedule> getSchedulesByDevice(Long deviceId) {

        User user = securityUtils.getCurrentUser();

        boolean hasPermission = userDeviceRepository
                .existsByUserIdAndDeviceId(user.getId(), deviceId);

        if (!hasPermission) {
            throw new RuntimeException("Bạn không có quyền truy cập thiết bị này");
        }

        return repo.findByDeviceId(deviceId);
    }


    public void processSchedules() {

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);

        List<DeviceSchedule> schedules = repo.findByEnabledTrue();

        for (DeviceSchedule s : schedules) {

            if (!isValidDay(s, today)) continue;

            Device device = deviceRepository.findById(s.getDeviceId())
                    .orElseThrow(() -> new RuntimeException("Device not found"));


            if (s.getStartTime() != null
                    && !today.equals(s.getLastRunStart())
                    && now.equals(s.getStartTime().withSecond(0).withNano(0))) {

                runner.turnOn(s.getDeviceId());

                String msg = "Hệ thống tự động bật " + device.getName();

                deviceLogService.saveLog(device, null, null, "AUTO_TURN_ON", msg, "SCHEDULE");

                notifyAllUsers(device, msg, "Lịch tự động");

                s.setLastRunStart(today);

                // ONCE schedule
                if ("ONCE".equals(s.getType()) && s.getEndTime() == null) {
                    s.setEnabled(false);
                }

                repo.save(s);
            }

            if (s.getEndTime() != null
                    && !today.equals(s.getLastRunEnd())
                    && now.equals(s.getEndTime().withSecond(0).withNano(0))) {

                runner.turnOff(s.getDeviceId());

                String msg = "Hệ thống tự động tắt " + device.getName();

                deviceLogService.saveLog(device, null, null, "AUTO_TURN_OFF", msg, "SCHEDULE");

                notifyAllUsers(device, msg, "Lịch tự động");

                s.setLastRunEnd(today);

                if ("ONCE".equals(s.getType())) {s.setEnabled(false);}

                repo.save(s);
            }
        }
    }


    private void notifyAllUsers(Device device, String msg, String title) {

        List<User> users = userDeviceRepository.findByDeviceId(device.getId())
                .stream()
                .map(UserDevice::getUser)
                .toList();

        for (User u : users) {notificationService.sendNotification(device, null, title, msg);}
    }

    private boolean isValidDay(DeviceSchedule s, LocalDate date) {

        if ("DAILY".equals(s.getType())) return true;

        if ("WEEKLY".equals(s.getType()) && s.getDaysOfWeek() != null) {

            DayOfWeek dow = date.getDayOfWeek();
            return s.getDaysOfWeek().contains(dow.name().substring(0, 3));
        }

        return true;
    }
}