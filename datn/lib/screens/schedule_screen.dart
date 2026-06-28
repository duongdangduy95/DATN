import 'package:flutter/material.dart';
import '../services/device_service.dart';
import '../services/device_service.dart';
class ScheduleScreen extends StatefulWidget {
  final int deviceId;

  const ScheduleScreen({super.key, required this.deviceId});

  @override
  State<ScheduleScreen> createState() => _ScheduleScreenState();
}

class _ScheduleScreenState extends State<ScheduleScreen> {
  TimeOfDay? startTime;
  TimeOfDay? endTime;
  DateTime? executeDate;

  String type = "DAILY";
  bool enabled = true;

  List<String> days = [];

  final service = DeviceService();

  Future<void> pickDate() async {

    final date = await showDatePicker(
      context: context,
      initialDate: DateTime.now(),
      firstDate: DateTime.now(),
      lastDate: DateTime(2100),
    );

    if (date != null) {
      setState(() {
        executeDate = date;
      });
    }
  }
  Future<void> pickTime(bool isStart) async {
    final t = await showTimePicker(
      context: context,
      initialTime: TimeOfDay.now(),
    );

    if (t != null) {
      setState(() {
        if (isStart) {
          startTime = t;
        } else {
          endTime = t;
        }
      });
    }
  }

  void toggleDay(String day) {
    setState(() {
      if (days.contains(day)) {
        days.remove(day);
      } else {
        days.add(day);
      }
    });
  }

  Future<void> save() async {

    // Không cho phép cả 2 đều null
    if (startTime == null && endTime == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text(
            "Vui lòng chọn ít nhất giờ bật hoặc giờ tắt",
          ),
        ),
      );
      if (type == "ONCE" && executeDate == null) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text("Vui lòng chọn ngày thực hiện"),
          ),
        );
        return;
      }
      return;
    }

    final body = {
      "deviceId": widget.deviceId,

      "startTime": startTime == null
          ? null
          : "${startTime!.hour.toString().padLeft(2, '0')}:"
          "${startTime!.minute.toString().padLeft(2, '0')}:00",

      "endTime": endTime == null
          ? null
          : "${endTime!.hour.toString().padLeft(2, '0')}:"
          "${endTime!.minute.toString().padLeft(2, '0')}:00",

      "type": type,

      "executeDate": type == "ONCE"
          ? executeDate!.toIso8601String().split("T")[0]
          : null,

      "daysOfWeek":
      type == "WEEKLY"
          ? days.join(",")
          : null,

      "enabled": enabled
    };

    try {

      await service.createSchedule(body);

      if (!mounted) return;

      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text("Tạo lịch thành công"),
        ),
      );

      Navigator.pop(context);

    } catch (e) {

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(e.toString())),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Tạo lịch thiết bị")),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            ListTile(
              title: const Text("Start Time"),
              trailing: Text(startTime?.format(context) ?? "Chọn"),
              onTap: () => pickTime(true),
            ),

            ListTile(
              title: const Text("End Time"),
              trailing: Text(endTime?.format(context) ?? "Optional"),
              onTap: () => pickTime(false),
            ),

            SwitchListTile(
              title: const Text("Enable"),
              value: enabled,
              onChanged: (v) => setState(() => enabled = v),
            ),

            Wrap(
              spacing: 10,
              children: [

                ChoiceChip(
                  label: const Text("ONCE"),
                  selected: type == "ONCE",
                  onSelected: (_) {
                    setState(() {
                      type = "ONCE";
                    });
                  },
                ),

                ChoiceChip(
                  label: const Text("DAILY"),
                  selected: type == "DAILY",
                  onSelected: (_) {
                    setState(() {
                      type = "DAILY";
                    });
                  },
                ),

                ChoiceChip(
                  label: const Text("WEEKLY"),
                  selected: type == "WEEKLY",
                  onSelected: (_) {
                    setState(() {
                      type = "WEEKLY";
                    });
                  },
                ),
              ],
            ),

            if (type == "WEEKLY")
              Wrap(
                spacing: 5,
                children: ["MON","TUE","WED","THU","FRI","SAT","SUN"]
                    .map((d) => FilterChip(
                  label: Text(d),
                  selected: days.contains(d),
                  onSelected: (_) => toggleDay(d),
                ))
                    .toList(),
              ),
            if (type == "ONCE")
              ListTile(
                leading: const Icon(Icons.calendar_today),
                title: const Text("Ngày thực hiện"),
                subtitle: Text(
                  executeDate == null
                      ? "Chưa chọn ngày"
                      : "${executeDate!.day}/"
                      "${executeDate!.month}/"
                      "${executeDate!.year}",
                ),
                onTap: pickDate,
              ),
            const Spacer(),

            ElevatedButton(
              onPressed: save,
              child: const Text("SAVE SCHEDULE"),
            )
          ],
        ),
      ),
    );
  }
}