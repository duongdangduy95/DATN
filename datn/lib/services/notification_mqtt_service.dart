import 'dart:convert';
import 'package:mqtt_client/mqtt_client.dart';
import 'package:mqtt_client/mqtt_server_client.dart';

class NotificationMqttService {

  late MqttServerClient client;

  Function(String title, String content)? onMessage;

  Future<void> connect(int userId) async {

    client = MqttServerClient("broker.hivemq.com", "flutter_$userId",);

    client.port = 1883;
    client.keepAlivePeriod = 20;

    client.connectionMessage = MqttConnectMessage().withClientIdentifier("flutter_$userId").startClean();

    await client.connect();

    client.subscribe("users/$userId/notifications", MqttQos.atLeastOnce,);

    client.updates!.listen((messages) {

      final recMess = messages[0].payload as MqttPublishMessage;

      final payload = MqttPublishPayload.bytesToStringAsString(recMess.payload.message);

      final data = jsonDecode(payload);

      if (onMessage != null) {
        onMessage!(data['title'], data['content']);
      }
    });
  }

  void disconnect() {
    client.disconnect();
  }
}