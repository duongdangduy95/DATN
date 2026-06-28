package com.example.iotbackend.mqtt;

import com.example.iotbackend.service.PairingService;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MqttSubscriber implements MqttCallback {

    private final MqttClient mqttClient;
    private final PairingService pairingService;

    @jakarta.annotation.PostConstruct
    public void init() {
        // Cài đặt callback trước để sẵn sàng nhận sự kiện
        mqttClient.setCallback(this);

        // 🌟 Tạo một Thread chạy ngầm xử lý Subscribe bất đồng bộ
        // Giúp bảo vệ Server Spring Boot khởi động thành công 100% kể cả khi Broker MQTT gặp sự cố
        new Thread(() -> {
            int retryCount = 0;
            // Thử đợi tối đa 10 giây để đảm bảo MqttClient đã kết nối thành công (isConnected = true)
            while (!mqttClient.isConnected() && retryCount < 10) {
                try {
                    System.out.println("🔄 Đang đợi MqttClient kết nối thành công... (Thử lại: " + retryCount + ")");
                    Thread.sleep(1000); // Đợi 1 giây mỗi nhịp
                    retryCount++;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Chỉ tiến hành Subscribe khi Client đã kết nối an toàn với Broker
            if (mqttClient.isConnected()) {
                try {
                    mqttClient.subscribe("devices/pair/request");
                    mqttClient.subscribe("devices/+/register");
                    mqttClient.subscribe("devices/+/state");
                    mqttClient.subscribe("devices/+/online");
                    System.out.println("🟢 [MQTT] Đã subscribe thành công tất cả các topic phần cứng!");
                } catch (MqttException e) {
                    System.err.println("❌ [MQTT] Lỗi trong quá trình subscribe các topic: " + e.getMessage());
                }
            } else {
                System.err.println("❌ [MQTT] Không thể subscribe các topic vì kết nối tới Broker quá hạn (Timeout)!");
            }
        }).start();
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("⚠️ MQTT disconnected: " + (cause != null ? cause.getMessage() : "Unknown cause"));
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());

            // PAIR REQUEST
            if (topic.equals("devices/pair/request")) {
                pairingService.handlePairing(payload);
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi xử lý dữ liệu tin nhắn MQTT: " + e.getMessage());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Tự động kích hoạt khi gửi bản tin thành công (nếu có)
    }
}