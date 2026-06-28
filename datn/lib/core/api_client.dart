import 'package:dio/dio.dart';

import '../storage/secure_storage.dart';
import 'api_constants.dart';

class ApiClient {

  static final Dio dio = Dio(
    BaseOptions(
      baseUrl: ApiConstants.baseUrl,
      connectTimeout: const Duration(seconds: 30),
      receiveTimeout: const Duration(seconds: 30),

      // BỎ DÒNG NÀY
      // sendTimeout: const Duration(seconds: 30),

      headers: {
        "Content-Type": "application/json",
        "Accept": "application/json",
      },
    ),
  );

  static Future<void> init() async {

    dio.interceptors.add(
      LogInterceptor(
        request: true,
        requestHeader: true,
        requestBody: true,
        responseBody: true,
        responseHeader: false,
        error: true,
      ),
    );

    dio.interceptors.add(
      InterceptorsWrapper(
        onRequest: (options, handler) async {

          String? token =
          await SecureStorage.getToken();

          print(
              "JWT TOKEN = $token");

          if (token != null) {

            options.headers["Authorization"] =
            "Bearer $token";
          }

          return handler.next(options);
        },
      ),
    );
  }
}