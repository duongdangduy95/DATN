import 'package:flutter/material.dart';
import 'package:dio/dio.dart';

import '../core/api_client.dart';

class AddDeviceScreen extends StatefulWidget {
  const AddDeviceScreen({
    super.key,
  });

  @override
  State<AddDeviceScreen> createState() =>
      _AddDeviceScreenState();
}

class _AddDeviceScreenState
    extends State<AddDeviceScreen> {

  final ssidController =
  TextEditingController();

  final passwordController =
  TextEditingController();

  String pairToken = "";
  bool loading = false;
  bool loadingToken = true;

  @override
  void initState() {
    super.initState();
    loadPairToken();
  }

  Future<void> loadPairToken() async {

    try {

      print("CREATE PAIR TOKEN");

      final response =
      await ApiClient.dio.post(
        "/api/devices/pair-token",
        options: Options(
          responseType:
          ResponseType.plain,
        ),
      );

      print("TOKEN RESPONSE = ${response.data}",);

      setState(() {
        pairToken = response.data.toString();
        loadingToken = false;
      });

      print("PAIR TOKEN = $pairToken",);

    } catch (e) {
      print("PAIR TOKEN ERROR = $e",);

      setState(() {loadingToken = false;});

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(e.toString(),),
          ),
        );
      }
    }
  }

  Future<void> setupDevice() async {

    if (pairToken.isEmpty) {

      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text(
            "Chưa tạo được Pair Token",
          ),
        ),
      );

      return;
    }

    try {

      setState(() {loading = true;});

      final dio = Dio();

      final formData = FormData.fromMap({"ssid": ssidController.text, "pass": passwordController.text, "token": pairToken,});

      print("SEND TOKEN = $pairToken",);


      await dio.post(
        "http://192.168.4.1/save",
        data: {
          "ssid": ssidController.text,
          "pass": passwordController.text,
          "token": pairToken,
        },
        options: Options(
          contentType: Headers.formUrlEncodedContentType,
        ),
      );

      if (mounted) {

        ScaffoldMessenger.of(context)
            .showSnackBar(
          const SnackBar(
            content: Text(
              "Đã gửi cấu hình tới thiết bị",
            ),
          ),
        );

        Navigator.pop(context, true,);
      }

    } catch (e) {

      if (mounted) {

        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(
              e.toString(),
            ),
          ),
        );
      }

    } finally {

      if (mounted) {
        setState(() {loading = false;});
      }
    }
  }

  @override
  void dispose() {

    ssidController.dispose();
    passwordController.dispose();

    super.dispose();
  }

  @override
  Widget build(
      BuildContext context) {

    print("BUILD TOKEN = $pairToken",);

    return Scaffold(
      appBar: AppBar(
        title: const Text("Thêm thiết bị",),
      ),

      body: Padding(
        padding:
        const EdgeInsets.all(16),

        child: Column(

          crossAxisAlignment:
          CrossAxisAlignment.start,

          children: [

            const Text(
              "Pair Token",
              style: TextStyle(
                fontWeight:
                FontWeight.bold,
              ),
            ),

            const SizedBox(height: 8,),

            Container(
              width:
              double.infinity,

              padding:
              const EdgeInsets.all(
                12,
              ),

              decoration:
              BoxDecoration(
                border:
                Border.all(),
                borderRadius:
                BorderRadius.circular(
                  8,
                ),
              ),

              child: loadingToken ? const Text("Đang tạo...",) : SelectableText(pairToken,),),

            const SizedBox(height: 20,),

            TextField(
              controller:
              ssidController,

              decoration:
              const InputDecoration(
                labelText:
                "WiFi SSID",
                border:
                OutlineInputBorder(),
              ),
            ),

            const SizedBox(height: 12,),

            TextField(
              controller:
              passwordController,

              obscureText: true,

              decoration:
              const InputDecoration(
                labelText:
                "WiFi Password",
                border:
                OutlineInputBorder(),
              ),
            ),

            const SizedBox(height: 20,),

            SizedBox(
              width:
              double.infinity,
              child:
              ElevatedButton(

                onPressed:
                loading ? null : setupDevice,

                child: loading ? const SizedBox(
                  height: 20,
                  width: 20,
                  child:
                  CircularProgressIndicator(),
                )
                    : const Text("Gửi cấu hình",),
              ),
            ),

            const SizedBox(height: 20,),

            const Text(
              "Lưu ý:\n"
                  "1. Kết nối điện thoại vào WiFi của thiết bị ESP32\n"
                  "2. Nhập tên WiFi nhà bạn\n"
                  "3. Nhập mật khẩu WiFi\n"
                  "4. Bấm Gửi cấu hình\n"
                  "5. Thiết bị sẽ tự đăng ký vào hệ thống",
            ),
          ],
        ),
      ),
    );
  }
}