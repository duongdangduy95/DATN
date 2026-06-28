import 'dart:io';
import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

import '../models/voice_control_response.dart';

class VoiceService {
  final Dio dio;
  final storage = const FlutterSecureStorage();

  VoiceService(this.dio);

  Future<VoiceControlResponse> controlByVoice(String audioPath) async {

    final token = await storage.read(key: "token");

    final file = File(audioPath);

    if (!await file.exists()) {
      throw Exception("File không tồn tại");
    }

    final formData = FormData.fromMap({
      "file": await MultipartFile.fromFile(
        file.path,
        filename: "voice.m4a",
      ),
    });

    final response = await dio.post(
      "/api/voice/control",
      data: formData,
      options: Options(
        headers: {
          "Authorization": "Bearer $token",
        },
        contentType: "multipart/form-data",
      ),
    );

    return VoiceControlResponse.fromJson(response.data);
  }
}