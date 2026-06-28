package com.example.iotbackend.mqtt;

import com.example.iotbackend.entity.Device;
import com.example.iotbackend.repository.DeviceRepository;
import com.example.iotbackend.service.MqttService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeviceScheduleRunner {

    private final MqttService mqttService;
    private final DeviceRepository deviceRepository;

    public void turnOn(Long deviceId) {
        send(deviceId, true);
    }

    public void turnOff(Long deviceId) {
        send(deviceId, false);
    }

    private void send(Long deviceId, boolean state) {

        Device device = deviceRepository.findById(deviceId).orElseThrow();

        String topic = "devices/" + device.getDeviceCode() + "/set";

        String payload = """
            { "state": %s }
            """.formatted(state);

        mqttService.publish(topic, payload);


        device.setStatus(state ? "ON" : "OFF");

        deviceRepository.save(device);
    }
}