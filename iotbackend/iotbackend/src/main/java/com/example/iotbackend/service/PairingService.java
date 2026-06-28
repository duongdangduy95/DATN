package com.example.iotbackend.service;

import com.example.iotbackend.entity.*;
import com.example.iotbackend.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PairingService {

    private final ObjectMapper objectMapper;

    private final PairTokenRepository pairTokenRepository;

    private final DeviceRepository deviceRepository;

    private final UserDeviceRepository userDeviceRepository;

    public void handlePairing(String payload) {

        try {
            JsonNode json = objectMapper.readTree(payload);

            String deviceCode = json.get("deviceCode").asText();

            String pairToken = json.get("pairToken").asText();

            PairToken token = pairTokenRepository.findByToken(pairToken).orElseThrow();

            if (token.getExpiredAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException(
                        "Token expired"
                );
            }

            User user = token.getUser();

            Device device = deviceRepository.findByDeviceCode(deviceCode).orElseGet(Device::new);

            device.setDeviceCode(deviceCode);
            device.setOnline(true);
            device.setOwner(user);

            deviceRepository.save(device);

            userDeviceRepository.deleteByDeviceId(device.getId());

            UserDevice ud = new UserDevice();

            ud.setUser(user);
            ud.setDevice(device);
            ud.setRole("OWNER");

            userDeviceRepository.save(ud);

            pairTokenRepository.delete(token);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}