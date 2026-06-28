import whisper

model = whisper.load_model("small")

result = model.transcribe(
    "test.wav",
    language="vi"
)

print("Text:", result["text"])