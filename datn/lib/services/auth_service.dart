import 'package:dio/dio.dart';
import '../core/api_client.dart';
import '../storage/secure_storage.dart';
class AuthService {

  //  LOGIN API
  Future<void> login(String email, String password,) async {

    try {

      print("🚀 [AUTH SERVICE] Đang gọi API Login...");
      final response = await ApiClient.dio.post(
        "/api/auth/login",
        data: {"email": email, "password": password,},
      );

      if (response.statusCode == 200) {

        final token = response.data["token"];

        print("TOKEN FROM SERVER = $token");
        await SecureStorage.saveToken(token,);

        print("TOKEN SAVED = ${await SecureStorage.getToken()}");

        print("🟢 LOGIN SUCCESS");
      }

    } catch (e) {

      print("LOGIN ERROR = $e");
      rethrow;
    }
  }

  // 2. REGISTER API
  Future<void> register(String username, String email, String password) async {
    try {
      print(" [AUTH SERVICE] Đang gọi API Register...");
      final response = await ApiClient.dio.post(
        '/api/auth/register',
        data: {
          'username': username,
          'email': email,
          'password': password,
        },
        options: Options(responseType: ResponseType.plain),
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        print("🟢 [AUTH SERVICE] Đăng ký thành công trên Server (Mã ${response.statusCode})");
        return;
      }
    } on DioException catch (de) {
      print(" [AUTH SERVICE] Lỗi Dio tại Register: ${de.message}");
      if (de.response?.statusCode == 200 || de.response?.statusCode == 201) return;
      throw Exception(de.response?.data ?? de.message);
    } catch (e) {
      print(" [AUTH SERVICE] Lỗi khác tại Register: $e");
      if (e.toString().contains("FormatException")) return;
      rethrow;
    }
  }

  // 3. VERIFY API (Xác thực OTP)
  Future<void> verify(String email, String otp) async {
    try {
      print(" [AUTH SERVICE] Đang gửi mã OTP lên xác thực...");
      final response = await ApiClient.dio.post(
        "/api/auth/verify",
        data: {
          "email": email,
          "otp": otp,
        },
        options: Options(responseType: ResponseType.plain), // Phòng thủ lỗi ép kiểu chuỗi
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        print("🟢 [AUTH SERVICE] Xác thực OTP thành công! (Mã ${response.statusCode})");
        return;
      }
    } on DioException catch (de) {
      print(" [AUTH SERVICE] Lỗi Dio tại Verify: ${de.message}");
      if (de.response?.statusCode == 200 || de.response?.statusCode == 201) {
        print("🟢 [AUTH SERVICE] Phòng thủ Dio thành công tại màn Verify!");
        return;
      }
      throw Exception(de.response?.data ?? de.message);
    } catch (e) {
      print(" [AUTH SERVICE] Lỗi khác tại Verify: $e");
      if (e.toString().contains("FormatException")) {
        print("⚠️ [AUTH SERVICE] Đã nuốt lỗi FormatException thành công tại màn Verify!");
        return; // Cho qua để UI chuyển màn vào Home ngon lành
      }
      rethrow;
    }
  }

  // 4. FORGOT PASSWORD API
  Future<void> forgotPassword(String email) async {
    try {
      print(" [AUTH SERVICE] Đang yêu cầu cấp lại mật khẩu cho: $email");
      final response = await ApiClient.dio.post(
        "/api/auth/forgot-password",
        data: {"email": email},
        options: Options(responseType: ResponseType.plain),
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        print("🟢 [AUTH SERVICE] Yêu cầu OTP quên mật khẩu thành công!");
        return;
      }
    } on DioException catch (de) {
      print(" [AUTH SERVICE] Lỗi Dio tại Forgot Password: ${de.message}");
      if (de.response?.statusCode == 200 || de.response?.statusCode == 201) return;
      throw Exception(de.response?.data ?? de.message);
    } catch (e) {
      print(" [AUTH SERVICE] Lỗi khác tại Forgot Password: $e");
      if (e.toString().contains("FormatException")) return;
      rethrow;
    }
  }

  // 5. RESET PASSWORD API
  Future<void> resetPassword(String email, String otp, String newPassword) async {
    try {
      print("[AUTH SERVICE] Đang gửi yêu cầu đặt lại mật khẩu mới...");
      final response = await ApiClient.dio.post(
        "/api/auth/reset-password",
        data: {
          "email": email,
          "otp": otp,
          "newPassword": newPassword,
        },
        options: Options(responseType: ResponseType.plain),
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        print("🟢 [AUTH SERVICE] Đổi mật khẩu mới thành công!");
        return;
      }
    } on DioException catch (de) {
      print(" Lỗi Dio tại Reset Password: ${de.message}");
      if (de.response?.statusCode == 200 || de.response?.statusCode == 201) return;
      throw Exception(de.response?.data ?? de.message);
    } catch (e) {
      print(" Lỗi khác tại Reset Password: $e");
      if (e.toString().contains("FormatException")) return;
      rethrow;
    }
  }

  //  LOGOUT API
  Future<void> logout() async {

    await SecureStorage.clear();
    try {
      await ApiClient.dio.post("/api/auth/logout",);
    } catch (_) {}
  }
}