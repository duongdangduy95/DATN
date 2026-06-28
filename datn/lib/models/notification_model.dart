class NotificationModel {
  final String title;
  final String content;

  NotificationModel({
    required this.title,
    required this.content,
  });

  factory NotificationModel.fromJson(Map<String, dynamic> json) {
    return NotificationModel(
      title: json['title'],
      content: json['content'],
    );
  }
}