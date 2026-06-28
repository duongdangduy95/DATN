import 'package:dio/dio.dart';

class ProvisionService {

  final Dio dio = Dio(
    BaseOptions(
      connectTimeout:
      const Duration(seconds: 10),
    ),
  );

  Future<void> setupDevice({
    required String ssid,
    required String password,
    required String pairToken,
  }) async {

    final formData = FormData.fromMap({
      "ssid": ssid,
      "pass": password,
      "token": pairToken,
    });

    await dio.post(
      "http://192.168.4.1/save",
      data: formData,
    );
  }
}