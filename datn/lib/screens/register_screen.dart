import 'package:flutter/material.dart';

import '../services/auth_service.dart';
import 'verify_screen.dart';

class RegisterScreen extends StatefulWidget {
  const RegisterScreen({super.key});

  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final usernameController = TextEditingController();
  final emailController = TextEditingController();
  final passwordController = TextEditingController();

  Future<void> register() async {
    // Nên check validate dữ liệu trống trước khi gửi
    if (emailController.text.trim().isEmpty || passwordController.text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Vui lòng điền đầy đủ thông tin")),
      );
      return;
    }

    try {
      print("[FLUTTER] Đang gửi yêu cầu đăng ký lên Render...");

      await AuthService().register(
        usernameController.text.trim(),
        emailController.text.trim(),
        passwordController.text,
      );

      print("🟢 [FLUTTER] Backend đã xử lý OK và lưu DB!");

      // Kiểm tra xem widget còn tồn tại trên màn hình không trước khi chuyển trang
      if (!mounted) return;

      print("🔀 [FLUTTER] Đang chuyển hướng sang VerifyScreen với email: ${emailController.text.trim()}");

      Navigator.pushReplacement(
        context,
        MaterialPageRoute(
          builder: (_) => VerifyScreen(
            email: emailController.text.trim(),
          ),
        ),
      );
    } catch (e) {
      // 🌟 ĐÂY LÀ CHÌA KHÓA: In trực tiếp lỗi ra Console để xem Dio đang bị gãy ở đâu!
      print(" LỖI HOÀN TOÀN TRONG KHỐI CATCH: $e");

      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text("Lỗi đăng ký: ${e.toString()}"),
          backgroundColor: Colors.red,
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Register"),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            TextField(
              controller: usernameController,
              decoration: const InputDecoration(
                labelText: "Username",
              ),
            ),
            TextField(
              controller: emailController,
              decoration: const InputDecoration(
                labelText: "Email",
              ),
            ),
            TextField(
              controller: passwordController,
              obscureText: true,
              decoration: const InputDecoration(
                labelText: "Password",
              ),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: register,
              child: const Text("Register"),
            ),
          ],
        ),
      ),
    );
  }
}