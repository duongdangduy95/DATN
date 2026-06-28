import 'package:flutter/material.dart';
import '../services/auth_service.dart';
import 'login_screen.dart';

class VerifyScreen extends StatefulWidget {
  final String email;

  const VerifyScreen({
    super.key,
    required this.email,
  });

  @override
  State<VerifyScreen> createState() => _VerifyScreenState();
}

class _VerifyScreenState extends State<VerifyScreen> {
  final otpController = TextEditingController();

  //  Biến quản lý trạng thái Loading để tránh User bấm dồn dập
  bool _isLoading = false;

  Future<void> verify() async {
    // Kiểm tra dữ liệu rỗng trước khi gọi API
    if (otpController.text.trim().isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Vui lòng nhập mã OTP")),
      );
      return;
    }

    setState(() {
      _isLoading = true; // Bật vòng xoay loading
    });

    try {
      print("[FLUTTER] Đang gửi OTP lên Render để xác thực...");

      await AuthService().verify(
        widget.email,
        otpController.text.trim(),
      );

      print("🟢 [FLUTTER] Xác thực thành công! Chuẩn bị chuyển màn...");

      if (!mounted) return;

      setState(() {
        _isLoading = false; // Tắt loading
      });

      // Hiển thị thông báo thành công ngắn trước khi chuyển trang
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text("Xác thực tài khoản thành công!"),
          backgroundColor: Colors.green,
        ),
      );

      // Điều hướng về Login và xóa sạch các màn hình trước đó trong Stack
      Navigator.pushAndRemoveUntil(
        context,
        MaterialPageRoute(
          builder: (_) => const LoginScreen(),
        ),
            (route) => false,
      );
    } catch (e) {
      print(" [FLUTTER] Lỗi tại màn hình Verify: $e");

      if (!mounted) return;

      setState(() {
        _isLoading = false; // Tắt loading khi gặp lỗi để User nhập lại
      });

      //  Bọc mounted an toàn cho khối báo lỗi
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text("Xác thực thất bại: ${e.toString()}"),
          backgroundColor: Colors.red,
        ),
      );
    }
  }

  @override
  void dispose() {
    otpController.dispose(); // Giải phóng bộ nhớ của Controller
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Verify OTP"),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center, // Canh giữa màn hình cho đẹp
          children: [
            Icon(Icons.mark_email_read_outlined, size: 80, color: Colors.blue.shade700),
            const SizedBox(height: 16),
            Text(
              "Mã OTP đã được tạo cho email:\n${widget.email}",
              textAlign: TextAlign.center,
              style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w500),
            ),
            const SizedBox(height: 24),
            TextField(
              controller: otpController,
              keyboardType: TextInputType.number, // Ép bàn phím điện thoại hiện số cho dễ gõ OTP
              maxLength: 6, // Giới hạn độ dài OTP thông thường là 6 số
              decoration: const InputDecoration(
                labelText: "Mã OTP (Xác thực)",
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.lock_clock),
              ),
            ),
            const SizedBox(height: 20),

            // Tự động chuyển đổi giữa Nút bấm và Vòng xoay Loading tùy theo trạng thái mạng
            _isLoading
                ? const CircularProgressIndicator()
                : SizedBox(
              width: double.infinity, // Kéo dài nút bằng chiều ngang màn hình
              height: 50,
              child: ElevatedButton(
                style: ElevatedButton.styleFrom(
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                ),
                onPressed: verify,
                child: const Text("Xác thực ngay", style: TextStyle(fontSize: 16)),
              ),
            ),
          ],
        ),
      ),
    );
  }
}