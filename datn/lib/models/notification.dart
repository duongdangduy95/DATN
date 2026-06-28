class AppNotification {
  final int? deviceId;
  final String? deviceName;
  final String actorName;
  final String recipientName;
  final String title;
  final String content;
  final String createdAt;

  AppNotification({
    this.deviceId,
    this.deviceName,
    required this.actorName,
    required this.recipientName,
    required this.title,
    required this.content,
    required this.createdAt,
  });

  factory AppNotification.fromJson(Map<String, dynamic> json) {
    return AppNotification(
      deviceId: json['deviceId'],
      deviceName: json['deviceName'],
      actorName: json['actorName'] ?? '',
      recipientName: json['recipientName'] ?? '',
      title: json['title'] ?? '',
      content: json['content'] ?? '',
      createdAt: json['createdAt'] ?? '',
    );
  }
}