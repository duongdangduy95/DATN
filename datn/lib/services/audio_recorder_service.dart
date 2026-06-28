import 'dart:io';
import 'package:record/record.dart';

class AudioRecorderService {
  final AudioRecorder recorder = AudioRecorder();

  Future<String?> record5Seconds() async {

    final hasPermission = await recorder.hasPermission();
    if (!hasPermission) {
      throw Exception("Chưa cấp quyền micro");
    }

    final path = "${Directory.systemTemp.path}/voice.m4a";

    await recorder.start(
      const RecordConfig(
        encoder: AudioEncoder.aacLc,
        sampleRate: 44100,
        numChannels: 1,
      ),
      path: path,
    );

    await Future.delayed(const Duration(seconds: 5));

    return await recorder.stop();
  }
}