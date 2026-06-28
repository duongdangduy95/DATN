import 'package:dio/dio.dart';
import '../core/api_client.dart';
import '../models/notification.dart';


class NotificationService {

  Future<List<AppNotification>> getMyNotifications() async {

    final response =
    await ApiClient.dio.get("/api/notifications");

    return (response.data as List)
        .map((e) => AppNotification.fromJson(e))
        .toList();
  }


}