import 'package:dio/dio.dart';

import '../core/api_client.dart';
import '../models/device.dart';
import '../models/device_guest.dart';
import '../models/device_schedule.dart';

class DeviceService {

  Future<List<Device>> getDevices() async {


    final response =
    await ApiClient.dio.get("/api/devices/my",);

    return (response.data as List).map((e) => Device.fromJson(e),).toList();
  }

  Future<void> controlDevice(int id, bool state,) async {

    try {

      final response = await ApiClient.dio.post(
        "/api/devices/control",
        data: {
          "deviceId": id,
          "state": state,
        },
      );
      print("STATUS = ${response.statusCode}");
      print("DATA = ${response.data}");

    } on DioException catch (e) {

      print("ERROR STATUS = ${e.response?.statusCode}");
      print("ERROR DATA = ${e.response?.data}");
      print("ERROR = ${e.message}");

      rethrow;
    }
  }

  Future<void> renameDevice(int deviceId, String name,) async {

    await ApiClient.dio.post(
      "/api/devices/rename",
      data: {
        "deviceId": deviceId,
        "name": name,
      },
    );
  }

  Future<void> shareDevice(int deviceId, String email,) async {

    await ApiClient.dio.post(
      "/api/devices/share",
      data: {
        "deviceId": deviceId,
        "email": email,
      },
    );
  }

  Future<List<DeviceGuest>> getGuests(
      int deviceId) async {

    final response =
    await ApiClient.dio.get("/api/devices/$deviceId/guests",);

    return (response.data as List).map((e) => DeviceGuest.fromJson(e),).toList();
  }

  Future<void> removeGuest(int deviceId, int guestUserId,) async {
    await ApiClient.dio.delete(
      "/api/devices/$deviceId/guest/$guestUserId",
    );
  }

  Future<DeviceSchedule> createSchedule(Map<String, dynamic> body) async {
    final response = await ApiClient.dio.post(
      "/api/schedule",
      data: body,
    );

    return DeviceSchedule.fromJson(response.data);
  }

  Future<List<dynamic>> getSchedules(int deviceId) async {
    final response = await ApiClient.dio.get(
      "/api/schedule/device/$deviceId",
    );

    return response.data;
  }

  Future<void> deleteSchedule(int id) async {
    await ApiClient.dio.delete(
      "/api/schedule/$id",
    );
  }

}