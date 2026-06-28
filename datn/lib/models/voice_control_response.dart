class VoiceControlResponse {
  final bool success;
  final String message;

  VoiceControlResponse({
    required this.success,
    required this.message,
  });

  factory VoiceControlResponse.fromJson(Map<String, dynamic> json) {
    return VoiceControlResponse(
      success: json['success'] ?? false,
      message: json['message'] ?? '',
    );
  }
}