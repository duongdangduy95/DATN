import 'package:flutter/material.dart';

import '../models/device.dart';
import '../models/device_guest.dart';
import '../services/device_service.dart';
import 'schedule_screen.dart';

class DeviceDetailScreen extends StatefulWidget {
  final Device device;

  const DeviceDetailScreen({super.key, required this.device,});

  @override
  State<DeviceDetailScreen> createState() => _DeviceDetailScreenState();
}

class _DeviceDetailScreenState extends State<DeviceDetailScreen> {
  List<DeviceGuest> guests = [];

  bool loading = true;

  @override
  void initState() {
    super.initState();
    loadGuests();
  }

  Future<void> loadGuests() async {
    try {
      guests = await DeviceService().getGuests(
        widget.device.id,
      );
    } catch (_) {}

    if (!mounted) return;
    setState(() {loading = false;});
  }

  Future<void> renameDevice() async {
    final controller = TextEditingController(text: widget.device.name,);

    final name = await showDialog<String>(
      context: context,
      builder: (_) => AlertDialog(
        title: const Text("Đổi tên thiết bị",),
        content: TextField(controller: controller,),
        actions: [
          TextButton(
            onPressed: () {Navigator.pop(context);},
            child: const Text("Hủy",),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(context, controller.text,);
            },
            child: const Text("Lưu",),
          ),
        ],
      ),
    );

    if (name == null || name.trim().isEmpty) {
      return;
    }

    await DeviceService().renameDevice(widget.device.id, name,);

    setState(() {widget.device.name = name;});
  }

  Future<void> shareDevice() async {
    final controller = TextEditingController();

    final email = await showDialog<String>(
      context: context,
      builder: (_) => AlertDialog(
        title: const Text(
          "Chia sẻ thiết bị",
        ),
        content: TextField(
          controller: controller,
          decoration:
          const InputDecoration(
            labelText: "Email",
          ),
        ),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.pop(context);
            },
            child: const Text(
              "Hủy",
            ),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(
                context,
                controller.text,
              );
            },
            child: const Text(
              "Share",
            ),
          ),
        ],
      ),
    );

    if (email == null || email.trim().isEmpty) {
      return;
    }

    await DeviceService().shareDevice(widget.device.id, email,);

    loadGuests();
  }

  Future<void> deleteGuest(
      DeviceGuest guest) async {
    await DeviceService()
        .removeGuest(
      widget.device.id,
      guest.userId,
    );

    loadGuests();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
          widget.device.name,
        ),
      ),
      body: loading ? const Center(child: CircularProgressIndicator(),)
          : ListView(
        children: [
          Card(
            margin:
            const EdgeInsets.all(
              12,
            ),
            child: Column(
              children: [
                ListTile(leading: const Icon(Icons.devices,),
                  title: const Text("Tên thiết bị",),
                  subtitle: Text(widget.device.name,),
                ),
                const Divider(),

                ListTile(
                  leading: const Icon(Icons.qr_code,),
                  title: const Text("Device Code",),
                  subtitle: Text(
                    widget.device
                        .deviceCode,
                  ),
                ),
                const Divider(),

                ListTile(
                  leading: const Icon(Icons.category,),
                  title: const Text("Loại",),
                  subtitle: Text(widget.device.type,),
                ),
                const Divider(),

                ListTile(
                  leading: const Icon(
                    Icons.power,
                  ),
                  title: const Text("Trạng thái",),
                  subtitle: Text(
                    widget.device.status,
                  ),
                ),
                const Divider(),

                ListTile(
                  leading: Icon(widget.device.online ? Icons.wifi : Icons.wifi_off,),
                  title: const Text(
                    "Kết nối",
                  ),
                  subtitle: Text(widget.device.online ? "Online" : "Offline",),
                ),
                const Divider(),

                ListTile(
                  leading: const Icon(Icons.person,),
                  title: const Text("Vai trò",),
                  subtitle: Text(
                    widget.device.role,
                  ),
                ),
              ],
            ),
          ),

          ListTile(
            leading: const Icon(
              Icons.edit,
            ),
            title: const Text("Đổi tên thiết bị",
            ),
            onTap: renameDevice,
          ),

          ListTile(
            leading: const Icon(
              Icons.share,
            ),
            title: const Text(
              "Chia sẻ thiết bị",
            ),
            onTap: shareDevice,
          ),

          // const Divider(),
          ListTile(
            leading: const Icon(Icons.schedule),
            title: const Text("Lập lịch tự động"),
            subtitle: const Text("Bật/Tắt thiết bị theo giờ"),
            onTap: () async {
              await Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (_) => ScheduleScreen(
                    deviceId: widget.device.id,
                  ),
                ),
              );
            },
          ),

          const Divider(),
          const Padding(
            padding:
            EdgeInsets.all(16),
            child: Text(
              "Danh sách guest",
              style: TextStyle(
                fontSize: 16,
                fontWeight:
                FontWeight.bold,
              ),
            ),
          ),

          ...guests.map(
                (guest) => ListTile(
              leading:
              const Icon(Icons.person,),
              title: Text(guest.username,),
              subtitle: Text(guest.email,),
              trailing:
              IconButton(
                icon:
                const Icon(Icons.delete,),
                onPressed: () {deleteGuest(guest);},
              ),
            ),
          ),
        ],
      ),
    );
  }
}