package com.example.iotbackend.controller;

import com.example.iotbackend.dto.*;
import com.example.iotbackend.entity.Device;
import com.example.iotbackend.entity.PairToken;
import com.example.iotbackend.entity.User;
import com.example.iotbackend.repository.PairTokenRepository;
import com.example.iotbackend.repository.UserDeviceRepository;
import com.example.iotbackend.security.WebUserDetails;
import com.example.iotbackend.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    private final PairTokenRepository pairTokenRepository;



    @PostMapping("/pair")
    public ResponseEntity<?> pairDevice(@RequestBody PairDeviceRequest req) {

        deviceService.pairDevice(req);

        return ResponseEntity.ok("Device paired successfully");
    }


    @GetMapping
    public ResponseEntity<?> myDevices() {
        return ResponseEntity.ok(deviceService.myDevices());
    }

    @GetMapping("/my")
    public List<DeviceResponse> getMyDevices() {
        return deviceService.getMyDevices();
    }

    @PostMapping("/pair-token")
    public ResponseEntity<?> createToken(Authentication authentication) {

        WebUserDetails webUserDetails = (WebUserDetails) authentication.getPrincipal();

        User user = webUserDetails.getUser();

        PairToken token = new PairToken();

        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setUsed(false);
        token.setExpiredAt(LocalDateTime.now().plusMinutes(5));

        pairTokenRepository.save(token);

        return ResponseEntity.ok(token.getToken());
    }

    @PostMapping("/share")
    public String shareDevice(@RequestBody ShareDeviceRequest req) {

        deviceService.shareDevice(req);
        return "Share success";
    }

    @PostMapping("/control")
    public String controlDevice(@RequestBody ControlDeviceRequest req) {

        deviceService.controlDevice(req);
        return "OK";
    }

    @GetMapping("/{deviceId}/guests")
    public List<DeviceGuestResponse> getGuests(@PathVariable Long deviceId) {

        return deviceService.getGuests(deviceId);
    }

    @DeleteMapping("/{deviceId}/guest/{guestUserId}")
    public String removeGuest(@PathVariable Long deviceId, @PathVariable Long guestUserId) {

        deviceService.removeGuest(deviceId, guestUserId);
        return "Removed";
    }

    @PostMapping("/rename")
    public String renameDevice(@RequestBody RenameDeviceRequest req) {
        deviceService.renameDevice(req);
        return "Rename success";
    }
}