class Device {

  final int id;

  final String deviceCode;

  String name;

  final String type;

  String status;

  final bool online;

  final String role;

  Device({
    required this.id,
    required this.deviceCode,
    required this.name,
    required this.type,
    required this.status,
    required this.online,
    required this.role,
  });

  factory Device.fromJson(
      Map<String, dynamic> json) {

    return Device(
      id: json["id"] ?? 0,

      deviceCode:
      json["deviceCode"] ?? "",

      name:
      json["name"] ?? "",

      type:
      json["type"] ?? "",

      status:
      json["status"] ?? "OFF",

      online:
      json["online"] ?? false,

      role:
      json["role"] ?? "",
    );
  }
}