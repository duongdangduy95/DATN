import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

import '../core/api_client.dart';
import '../models/device.dart';
import '../models/notification.dart';
import '../services/device_service.dart';
import 'add_device_screen.dart';
import 'device_detail_screen.dart';
import 'login_screen.dart';
import '../services/audio_recorder_service.dart';
import '../services/voice_service.dart';
import 'package:dio/dio.dart';
import '../services/permission_service.dart';
import '../models/voice_control_response.dart';
import '../services/ notification_service.dart';

class DeviceScreen extends StatefulWidget {
  const DeviceScreen({super.key});

  @override
  State<DeviceScreen> createState() => _DeviceScreenState();
}

class _DeviceScreenState extends State<DeviceScreen> {
  List<Device> devices = [];
  bool loading = true;

  // List<dynamic> notifications = [];

  final AudioRecorderService audioRecorderService = AudioRecorderService();

  final VoiceService voiceService = VoiceService(ApiClient.dio);

  final NotificationService notificationService = NotificationService();


  @override
  void initState() {
    super.initState();
    loadData();
  }
  Future<void> showSchedules(Device device) async {

    List<dynamic> schedules = [];

    try {
      schedules =
      await DeviceService().getSchedules(device.id);
    } catch (e) {
      if (!mounted) return;

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(e.toString())),
      );
      return;
    }

    if (!mounted) return;

    showDialog(
      context: context,
      builder: (_) => StatefulBuilder(
        builder: (context, setDialogState) {

          return AlertDialog(
            title: Text("Lịch của ${device.name}",
            ),

            content: SizedBox(
              width: double.maxFinite,
              height: 400,

              child: schedules.isEmpty ? const Center(child: Text("Chưa có lịch"),
              )

                  : ListView.builder(
                itemCount: schedules.length,

                itemBuilder: (_, index) {

                  final s = schedules[index];

                  return Card(
                    child: ListTile(
                      title: Text(
                        "${s['startTime']} → ${s['endTime']}",
                      ),

                      subtitle: Text(
                        "Kiểu: ${s['type']}\n"
                            "Enabled: ${s['enabled']}",
                      ),

                      trailing: IconButton(
                        icon: const Icon(
                          Icons.delete,
                          color: Colors.red,
                        ),

                        onPressed: () async {

                          await DeviceService().deleteSchedule(s['id'],);

                          schedules.removeAt(index);

                          setDialogState(() {});

                          if (!context.mounted) return;

                          ScaffoldMessenger.of(context)
                              .showSnackBar(
                            const SnackBar(
                              content:
                              Text("Đã xóa lịch"),
                            ),
                          );
                        },
                      ),
                    ),
                  );
                },
              ),
            ),

            actions: [
              TextButton(
                onPressed: () {
                  Navigator.pop(context);
                },
                child: const Text("Đóng"),
              )
            ],
          );
        },
      ),
    );
  }
  List<AppNotification> notifications = [];

  Future<void> loadNotifications() async {
    try {
      final res = await notificationService.getMyNotifications();

      notifications = res;

      setState(() {});
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(e.toString())),
      );
    }
  }
  Future<void> voiceControl() async {

    final granted = await PermissionService.requestMicPermission();

    if (!granted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text("Bạn chưa cấp quyền micro"),
        ),
      );
      return;
    }

    try {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Đang ghi âm 5 giây...")),
      );

      final audioPath = await audioRecorderService.record5Seconds();

      if (audioPath == null) return;

      final result = await voiceService.controlByVoice(audioPath);

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(result.message)),
      );

      if (result.success) {
        await loadData();
      }

    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(e.toString())),
      );
    }
  }
  Future<void> loadData() async {
    setState(() {
      loading = true;
    });

    try {
      devices = await DeviceService().getDevices();

      for (final d in devices) {
        print("DEVICE=${d.name} STATUS=${d.status}");
      }

    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(e.toString())),
      );
    }

    if (!mounted) return;

    setState(() {
      loading = false;
    });
  }

  Future<void> logout() async {
    const storage = FlutterSecureStorage();

    await storage.delete(key: "token");

    if (!mounted) return;

    Navigator.pushAndRemoveUntil(
      context,
      MaterialPageRoute(
        builder: (_) => const LoginScreen(),
      ),
          (route) => false,
    );
  }

  Future<void> toggleDevice(
      Device device,
      bool value,
      ) async {
    try {

      await DeviceService().controlDevice(
        device.id,
        value,
      );

      if (!mounted) return;

      setState(() {
        device.status = value.toString();
      });

    } catch (e) {

      if (!mounted) return;

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(e.toString()),
        ),
      );
    }
  }
  Widget buildNotificationList() {
    return ListView.builder(
      itemCount: notifications.length,
      itemBuilder: (context, index) {


        final n = notifications[index];

        return ListTile(
          leading: const Icon(Icons.notifications),
          title: Text(n.title),
          subtitle: Text(n.content),
        );
      },
    );
  }
  Widget buildDeviceCard(Device device) {
    final isOn =
        device.status.toUpperCase() == "ON" ||
            device.status == "true";

    return Card(
      elevation: 3,
      margin: const EdgeInsets.symmetric(
        horizontal: 12,
        vertical: 6,
      ),
      child: ListTile(
        contentPadding: const EdgeInsets.all(12),

        leading: CircleAvatar(
          child: Icon(
            device.online
                ? Icons.wifi
                : Icons.wifi_off,
          ),
        ),

        title: Text(
          device.name,
          style: const TextStyle(
            fontWeight: FontWeight.bold,
          ),
        ),

        subtitle: Column(
          crossAxisAlignment:
          CrossAxisAlignment.start,
          children: [
            const SizedBox(height: 4),

            // Text(
            //   "Code: ${device.deviceCode}",
            // ),

            Text("Type: ${device.type}",
            ),

            // Text(
            //   "Role: ${device.role}",
            // ),

            Text(device.online? "Online" : "Offline",
            ),
          ],
        ),

        trailing: Row(
          mainAxisSize: MainAxisSize.min,
          children: [

            PopupMenuButton<String>(
              onSelected: (value) {
                if (value == "schedule") {
                  showSchedules(device);
                }
              },
              itemBuilder: (_) => [
                const PopupMenuItem(
                  value: "schedule",
                  child: Row(
                    children: [
                      Icon(Icons.schedule),
                      SizedBox(width: 8),
                      Text("Lịch tự động"),
                    ],
                  ),
                ),
              ],
            ),

            Switch(
              value: isOn,
              onChanged: (value) {
                toggleDevice(device, value);
              },
            ),
          ],
        ),

        onTap: () async {
          await Navigator.push(
            context,
            MaterialPageRoute(
              builder: (_) =>
                  DeviceDetailScreen(
                    device: device,
                  ),
            ),
          );

          loadData();
        },
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          "Thiết bị của tôi",
        ),
        actions: [

          IconButton(
            icon: const Icon(Icons.notifications),
            onPressed: () async {
              await loadNotifications();

              if (!mounted) return;

              showDialog(
                context: context,
                builder: (_) {
                  return AlertDialog(
                    title: const Text("Thông báo"),

                    content: SizedBox(
                      width: double.maxFinite,
                      height: 400,
                      child: notifications.isEmpty
                          ? const Center(child: Text("Không có thông báo"))
                          : ListView.builder(
                        itemCount: notifications.length,
                        itemBuilder: (context, index) {
                          final n = notifications[index];

                          return ListTile(
                            leading: const Icon(Icons.notifications),
                            title: Text(n.title),
                            subtitle: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(n.content),
                                const SizedBox(height: 4),
                                Text(
                                  n.createdAt,
                                  style: const TextStyle(fontSize: 11, color: Colors.grey),
                                ),
                              ],
                            ),
                          );
                        },
                      ),
                    ),

                    actions: [
                      TextButton(
                        onPressed: () => Navigator.pop(context),
                        child: const Text("Đóng"),
                      )
                    ],
                  );
                },
              );
            },
          ),

          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: logout,
          ),
        ],
      ),

      floatingActionButton: Column(
      mainAxisSize: MainAxisSize.min,
      children: [

        FloatingActionButton(
          heroTag: "voice",
          onPressed: voiceControl,
          child: const Icon(
            Icons.mic,
          ),
        ),

        const SizedBox(
          height: 12,
        ),

        FloatingActionButton(
          heroTag: "add",
          child: const Icon(
            Icons.add,
          ),
          onPressed: () async {

            final result =
            await Navigator.push(
              context,
              MaterialPageRoute(
                builder: (_) =>
                const AddDeviceScreen(),
              ),
            );

            if (result == true) {
              loadData();
            }
          },
        ),
      ],
    ),

      body: loading
          ? const Center(
        child:
        CircularProgressIndicator(),
      )
          : devices.isEmpty ? const Center(
        child: Text("Chưa có thiết bị",),
      )
          : RefreshIndicator(
        onRefresh: loadData,
        child: ListView.builder(
          itemCount:
          devices.length,
          itemBuilder:
              (_, index) {
            return buildDeviceCard(
              devices[index],
            );
          },
        ),
      ),
    );
  }
}