import 'package:flutter/material.dart';

import '../storage/secure_storage.dart';
import 'device_screen.dart';
import 'login_screen.dart';

class SplashScreen
    extends StatefulWidget {

  const SplashScreen({
    super.key,
  });

  @override
  State<SplashScreen> createState() =>
      _SplashScreenState();
}

class _SplashScreenState
    extends State<SplashScreen> {

  @override
  void initState() {
    super.initState();

    checkLogin();
  }

  Future<void> checkLogin() async {

    String? token =
    await SecureStorage.getToken();

    if (!mounted) return;

    Navigator.pushReplacement(
      context,
      MaterialPageRoute(
        builder: (_) => token == null
            ? const LoginScreen()
            : const DeviceScreen(),
      ),
    );
  }

  @override
  Widget build(
      BuildContext context) {

    return const Scaffold(
      body: Center(
        child:
        CircularProgressIndicator(),
      ),
    );
  }
}