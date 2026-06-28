package com.example.iotbackend.config;

import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MqttClientConfig {

    @Value("${mqtt.host}")
    private String host;

    @Value("${mqtt.client-id}")
    private String clientId;

    private final MqttConnectOptions options;

    @Bean
    public MqttClient mqttClient() throws Exception {

        MqttClient client = new MqttClient(host, clientId);

        client.connect(options);

        return client;
    }
}