class DeviceSchedule {
  final int id;
  final int deviceId;
  final int userId;
  final String? startTime;
  final String? endTime;
  final String type;
  final String? daysOfWeek;
  final bool enabled;
  final String? lastRunStart;
  final String? lastRunEnd;

  DeviceSchedule({
    required this.id,
    required this.deviceId,
    required this.userId,
    this.startTime,
    this.endTime,
    required this.type,
    this.daysOfWeek,
    required this.enabled,
    this.lastRunStart,
    this.lastRunEnd,
  });

  factory DeviceSchedule.fromJson(Map<String, dynamic> json) {
    return DeviceSchedule(
      id: json['id'],
      deviceId: json['deviceId'],
      userId: json['userId'],
      startTime: json['startTime'],
      endTime: json['endTime'],
      type: json['type'],
      daysOfWeek: json['daysOfWeek'],
      enabled: json['enabled'],
      lastRunStart: json['lastRunStart'],
      lastRunEnd: json['lastRunEnd'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'deviceId': deviceId,
      'userId': userId,
      'startTime': startTime,
      'endTime': endTime,
      'type': type,
      'daysOfWeek': daysOfWeek,
      'enabled': enabled,
      'lastRunStart': lastRunStart,
      'lastRunEnd': lastRunEnd,
    };
  }
}