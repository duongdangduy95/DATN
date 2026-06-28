package com.example.iotbackend.mqtt;

import com.example.iotbackend.dto.DeviceRegisterDTO;
import com.example.iotbackend.entity.*;
import com.example.iotbackend.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MqttConsumer {

    private final DeviceRepository deviceRepository;
    private final PairTokenRepository pairTokenRepository;
    private final UserDeviceRepository userDeviceRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handle(Message<String> message) {

        String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);

        String payload = message.getPayload();

        if (topic == null || payload == null) {return;}


        try {

            String deviceId = extractDeviceId(topic, payload);

            if (topic.endsWith("/register")) {

                DeviceRegisterDTO dto = objectMapper.readValue(payload, DeviceRegisterDTO.class);

                Device device = deviceRepository.findByDeviceCode(dto.getDeviceId())
                                .orElseGet(() -> {

                                    Device d = new Device();

                                    d.setDeviceCode(dto.getDeviceId());
                                    d.setOnline(false);
                                    d.setPaired(false);

                                    return d;
                                });


                device.setType(dto.getType());
                device.setIpAddress(dto.getIp());
                device.setMacAddress(dto.getMac());
                device.setRssi(dto.getRssi());
                device.setOnline(true);
                device.setLastSeen(LocalDateTime.now());


                if (device.getOwner() != null) {

                    boolean exists = userDeviceRepository.existsByUserIdAndDeviceId(device.getOwner().getId(), device.getId());

                    if (!exists) {

                        UserDevice ud = new UserDevice();

                        ud.setUser(device.getOwner());
                        ud.setDevice(device);
                        ud.setRole("OWNER");

                        userDeviceRepository.save(ud);
                    }
                }
                if (!Boolean.TRUE.equals(device.getPaired()) && dto.getPairToken() != null && !dto.getPairToken().isBlank()) {
                    PairToken token = pairTokenRepository.findByToken(dto.getPairToken()).orElse(null);

                    if (token != null) {

                        boolean valid = Boolean.FALSE.equals(token.getUsed()) && token.getExpiredAt().isAfter(LocalDateTime.now());

                        if (valid) {

                            User owner = token.getUser();
                            device.setOwner(owner);
                            device.setPaired(true);

                            deviceRepository.save(device);

                            boolean exists = userDeviceRepository.existsByUserIdAndDeviceId(owner.getId(), device.getId());

                            if (!exists) {
                                UserDevice ud = new UserDevice();

                                ud.setUser(owner);
                                ud.setDevice(device);
                                ud.setRole("OWNER");

                                userDeviceRepository.save(ud);

                            }

                            token.setUsed(true);
                            pairTokenRepository.save(token);

                        } else {

//                            System.out.println(
//                                    "⚠ INVALID PAIR TOKEN"
//                            );
                        }
                    }
                }

                deviceRepository.save(device);

                return;
            }

            if (topic.endsWith("/online")) {

                Optional<Device> opt = deviceRepository.findByDeviceCode(deviceId);

                if (opt.isEmpty()) {return;}

                JsonNode node = objectMapper.readTree(payload);

                boolean online = node.has("online") && node.get("online").asBoolean();

                Device device = opt.get();

                if (device.getOwner() != null) {System.out.println("OWNER ID    = " + device.getOwner().getId());}

                System.out.println("PAIRED      = " + device.getPaired());

                device.setOnline(online);
                device.setLastSeen(LocalDateTime.now());

                deviceRepository.save(device);

                return;
            }


            if (topic.endsWith("/state")) {

                Optional<Device> opt = deviceRepository.findByDeviceCode(deviceId);

                if (opt.isEmpty()) {return;}

                JsonNode node = objectMapper.readTree(payload);

                boolean state = node.has("state") && node.get("state").asBoolean();

                Device device = opt.get();

                device.setStatus(String.valueOf(state));
                device.setLastSeen(LocalDateTime.now());

                deviceRepository.save(device);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extractDeviceId(String topic, String payload) {

        try {
            JsonNode node = objectMapper.readTree(payload);

            if (node.has("deviceId")) {return node.get("deviceId").asText();}

        } catch (Exception ignored) {
        }

        String[] parts = topic.split("/");

        return parts.length > 1 ? parts[1] : topic;
    }
}