class DeviceGuest {
  final int userId;
  final String username;
  final String email;
  final String role;

  DeviceGuest({
    required this.userId,
    required this.username,
    required this.email,
    required this.role,
  });

  factory DeviceGuest.fromJson(
      Map<String, dynamic> json,
      ) {
    return DeviceGuest(
      userId: json["userId"],
      username: json["username"] ?? "",
      email: json["email"] ?? "",
      role: json["role"] ?? "",
    );
  }
}