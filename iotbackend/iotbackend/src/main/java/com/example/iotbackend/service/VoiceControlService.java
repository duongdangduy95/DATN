package com.example.iotbackend.service;

import com.example.iotbackend.dto.ControlDeviceRequest;
import com.example.iotbackend.dto.VoiceControlResponse;
import com.example.iotbackend.entity.Device;
import com.example.iotbackend.entity.User;
import com.example.iotbackend.entity.UserDevice;
import com.example.iotbackend.repository.UserDeviceRepository;
import com.example.iotbackend.security.SecurityUtils;
import com.example.iotbackend.util.DeviceVocabulary;
import com.example.iotbackend.util.TextNormalizer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoiceControlService {

    private final SecurityUtils securityUtils;
    private final UserDeviceRepository userDeviceRepository;
    private final DeviceService controlDeviceService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public VoiceControlResponse processVoice(MultipartFile file) {

        try {

            String text = speechToText(file);
            System.out.println("RAW SPEECH TEXT = " + text);

            return executeCommand(text);

        } catch (Exception e) {
            e.printStackTrace();

            return new VoiceControlResponse(false, e.getMessage());
        }
    }

    private String speechToText(MultipartFile file) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                        "https://voicemodel-production.up.railway.app/speech-to-text",
                        request,
                        String.class
                );

        String raw = response.getBody();

        if (raw == null || raw.isBlank()) {
            throw new RuntimeException("Speech-to-text trả về rỗng");
        }

        raw = raw.trim();

        if (raw.startsWith("{")) {
            JsonNode node = objectMapper.readTree(raw);
            if (node.has("text")) {
                return node.get("text").asText().trim();
            }
            return raw;
        }
        return raw;
    }

    private VoiceControlResponse executeCommand(String text) {

        String command = TextNormalizer.normalize(text);
        System.out.println("NORMALIZED = " + command);

        boolean state;

        if (command.contains("bat")) {
            state = true;
        } else if (command.contains("tat")) {
            state = false;
        } else {
            return new VoiceControlResponse(false, "Không nhận diện được lệnh bật/tắt");
        }

        String deviceName = command
                .replace("bat", "")
                .replace("tat", "")
                .trim();

        deviceName = DeviceVocabulary.normalizeDeviceName(deviceName);

        System.out.println("DEVICE AFTER NORMALIZE = " + deviceName);

        return controlDeviceByName(deviceName, state);
    }

    private VoiceControlResponse controlDeviceByName(String deviceName, boolean state) {

        User user = securityUtils.getCurrentUser();

        List<UserDevice> userDevices = userDeviceRepository.findByUserId(user.getId());

        Device device = userDevices.stream()
                        .map(UserDevice::getDevice)
                        .filter(d -> DeviceVocabulary.normalizeDeviceName(d.getName()).equals(deviceName))
                        .findFirst()
                        .orElse(null);

        if (device == null) {
            return new VoiceControlResponse(false, "Không tìm thấy thiết bị: " + deviceName);
        }

        ControlDeviceRequest req = new ControlDeviceRequest();
        req.setDeviceId(device.getId());
        req.setState(state);

        controlDeviceService.controlDevice(req);

        return new VoiceControlResponse(true, "Đã " + (state ? "bật " : "tắt ") + device.getName()
        );
    }
}