import sounddevice as sd
from scipy.io.wavfile import write

fs = 16000  
seconds = 5  

print(" Bắt đầu nói...")
audio = sd.rec(int(seconds * fs), samplerate=fs, channels=1)
sd.wait()

write("test.wav", fs, audio)
print(" Đã lưu test.wav")