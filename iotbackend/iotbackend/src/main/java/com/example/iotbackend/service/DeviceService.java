package com.example.iotbackend.service;

import com.example.iotbackend.dto.*;
import com.example.iotbackend.entity.*;
import com.example.iotbackend.repository.*;
import com.example.iotbackend.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    private final DeviceGroupRepository groupRepository;

    private final UserDeviceRepository userDeviceRepository;

    private final UserRepository userRepository;

    private final SecurityUtils securityUtils;

    private final MqttService mqttService;

    private final DeviceLogService deviceLogService;
    private final NotificationService notificationService;

    public void pairDevice(PairDeviceRequest req) {

        User user = securityUtils.getCurrentUser();

        Device device = deviceRepository.findByDeviceCode(req.getDeviceCode())
                        .orElseThrow(() -> new RuntimeException("Device not found"));

        boolean owned = userDeviceRepository.existsByDeviceId(device.getId());

        if (owned) { throw new RuntimeException("Device already paired");}


        device.setName(req.getName());

        if (req.getGroupId() != null) {

            DeviceGroup group = groupRepository.findById(req.getGroupId())
                            .orElseThrow(() -> new RuntimeException("Group not found"));


            if (!group.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Không có quyền group");
            }

            device.setGroup(group);
        }


        deviceRepository.save(device);

        UserDevice ud = new UserDevice();

        ud.setUser(user);
        ud.setDevice(device);
        ud.setRole("OWNER");

        userDeviceRepository.save(ud);

        deviceLogService.saveLog(device, user, null, "PAIR_DEVICE", user.getUsername() + " đã ghép thiết bị " + device.getName(), "MOBILE");

        notificationService.sendNotification(device, user, "Ghép thiết bị thành công", "Bạn đã ghép thiết bị " + device.getName());
    }

    public List<Device> myDevices() {

        User user = securityUtils.getCurrentUser();

        List<UserDevice> list = userDeviceRepository.findByUserId(user.getId());
        return list.stream().map(UserDevice::getDevice).toList();
    }

    public List<DeviceResponse> getMyDevices() {

        User user = securityUtils.getCurrentUser();

        List<UserDevice> userDevices = userDeviceRepository.findByUserId(user.getId());

        return userDevices.stream()
                .map(UserDevice::getDevice)
                .map(device -> {

                    DeviceResponse res = new DeviceResponse();

                    res.setId(device.getId());
                    res.setDeviceCode(device.getDeviceCode());
                    res.setName(device.getName());
                    res.setType(device.getType());
                    res.setStatus(device.getStatus());
                    res.setOnline(device.getOnline());

                    return res;
                })
                .toList();
    }

    public void shareDevice(ShareDeviceRequest req) {

        User currentUser = securityUtils.getCurrentUser();


        Device device = deviceRepository.findById(req.getDeviceId())
                        .orElseThrow(() -> new RuntimeException("Device not found"));


        if (!device.getOwner().getId().equals(currentUser.getId())) {

            throw new RuntimeException("Không có quyền share");
        }

        User guestUser = userRepository.findByEmail(req.getEmail())
                        .orElseThrow(() -> new RuntimeException("User not found"));


        boolean exists = userDeviceRepository.existsByUserIdAndDeviceId(guestUser.getId(), device.getId());

        if (exists) {
            throw new RuntimeException("User already has device");
        }

        UserDevice ud = new UserDevice();

        ud.setUser(guestUser);
        ud.setDevice(device);
        ud.setRole("GUEST");

        userDeviceRepository.save(ud);

        deviceLogService.saveLog(device, currentUser, guestUser, "SHARE_DEVICE", currentUser.getUsername() + " đã chia sẻ " + device.getName() + " cho " + guestUser.getUsername(), "MOBILE");

        notificationService.sendNotification(device, currentUser, "Thiết bị mới được chia sẻ", currentUser.getUsername() + " đã chia sẻ " + device.getName() + " cho bạn");

        notificationService.sendNotification(device, currentUser, "Chia sẻ thành công", "Bạn đã chia sẻ " + device.getName() + " cho " + guestUser.getUsername());
    }

    public void controlDevice(ControlDeviceRequest req) {

        User user = securityUtils.getCurrentUser();


        UserDevice ud = userDeviceRepository.findByUserIdAndDeviceId(user.getId(), req.getDeviceId())
                        .orElseThrow(() -> new RuntimeException("Không có quyền"));

        Device device = ud.getDevice();


        String topic = "devices/" + device.getDeviceCode() + "/set";

        String payload = """
                {
                  "state": %s
                }
                """.formatted(req.getState());


        mqttService.publish(topic, payload);


        device.setStatus(req.getState().toString());

        deviceRepository.save(device);

        String action = Boolean.TRUE.equals(req.getState()) ? "TURN_ON" : "TURN_OFF";

        String msg = user.getUsername() + (Boolean.TRUE.equals(req.getState()) ? " đã bật " : " đã tắt ") + device.getName();

        deviceLogService.saveLog(device, user, null, action, msg, "MOBILE");


        List<UserDevice> users = userDeviceRepository.findByDeviceId(device.getId());

        for (UserDevice x : users) {
            notificationService.sendNotification(device, x.getUser(), "Thiết bị thay đổi", msg);
        }
    }

    public List<DeviceGuestResponse> getGuests(Long deviceId) {

        User currentUser = securityUtils.getCurrentUser();


        Device device = deviceRepository.findById(deviceId)
                        .orElseThrow(() -> new RuntimeException("Device not found"));


        if (!device.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Không có quyền");
        }
        List<UserDevice> list = userDeviceRepository.findByDeviceId(deviceId);

        return list.stream().filter(x -> !x.getRole().equals("OWNER"))
                .map(x ->
                        new DeviceGuestResponse(x.getUser().getId(), x.getUser().getUsername(), x.getUser().getEmail(), x.getRole())
                )
                .toList();
    }
     @Transactional
    public void removeGuest(Long deviceId, Long guestUserId) {

        User currentUser = securityUtils.getCurrentUser();

        Device device = deviceRepository.findById(deviceId)
                        .orElseThrow(() -> new RuntimeException("Device not found"));

        if (!device.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Không có quyền");
        }
        UserDevice ud = userDeviceRepository.findByUserIdAndDeviceId(guestUserId, deviceId)
                        .orElseThrow(() -> new RuntimeException("Guest not found"));

        User guest = ud.getUser();
        if (ud.getRole().equals("OWNER")) {
            throw new RuntimeException(
                    "Không thể xóa owner"
            );
        }


        userDeviceRepository.deleteByUserIdAndDeviceId(guestUserId, deviceId);
         deviceLogService.saveLog(device, currentUser, guest, "REMOVE_GUEST", currentUser.getUsername() + " đã xóa " + guest.getUsername() + " khỏi " + device.getName(), "MOBILE");

         notificationService.sendNotification(device, currentUser, "Quyền truy cập bị thu hồi", "Bạn đã bị xóa khỏi " + device.getName());
         notificationService.sendNotification(device, currentUser, "Đã xóa người dùng", "Đã xóa " + guest.getUsername() + " khỏi " + device.getName());
    }

    public void renameDevice(RenameDeviceRequest req) {

        User currentUser = securityUtils.getCurrentUser();

        Device device = deviceRepository.findById(req.getDeviceId())
                        .orElseThrow(() -> new RuntimeException("Device not found"));


        if (!device.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException(
                    "Không có quyền đổi tên thiết bị"
            );
        }

        String oldName = device.getName();

        device.setName(req.getName());
        deviceRepository.save(device);

        deviceLogService.saveLog(device, currentUser, null, "RENAME_DEVICE", currentUser.getUsername() + " đã đổi tên " + oldName + " thành " + req.getName(), "MOBILE");

        List<UserDevice> users = userDeviceRepository.findByDeviceId(device.getId());

        for (UserDevice x : users) {

            notificationService.sendNotification(device, currentUser, "Thiết bị đổi tên", currentUser.getUsername() + " đã đổi tên " + oldName + " thành " + req.getName());
        }

        deviceRepository.save(device);
    }
}