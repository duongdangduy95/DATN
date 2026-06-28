# HỆ THỐNG CÔNG TẮC ĐIỀU KHIỂN TỪ XA TÍCH HỢP ĐIỀU KHIỂN BẰNG GIỌNG NÓI

##  Giới thiệu

Đây là đồ án tốt nghiệp nghiên cứu và xây dựng hệ thống nhà thông minh cho phép người dùng điều khiển thiết bị điện từ xa thông qua ứng dụng di động và giọng nói.

Hệ thống hỗ trợ:

* Điều khiển thiết bị điện theo thời gian thực.
* Giám sát trạng thái thiết bị từ xa.
* Điều khiển bằng giọng nói tiếng Việt.
* Đồng bộ dữ liệu giữa thiết bị phần cứng và ứng dụng di động thông qua giao thức MQTT.
* Quản lý người dùng và thiết bị thông qua RESTful API.

---

## Kiến trúc hệ thống

Hệ thống được chia thành 4 thành phần chính:

| Thành phần       | Mô tả                                                    |
| ---------------- | -------------------------------------------------------- |
| **iot_hardware** | Chương trình điều khiển chạy trên vi điều khiển ESP32-C3 |
| **iotbackend**   | Backend Spring Boot xử lý nghiệp vụ và quản lý dữ liệu   |
| **voice_model**  | Dịch vụ AI nhận diện giọng nói tiếng Việt                |
| **datn**         | Ứng dụng Flutter dành cho người dùng                     |

---

## Kiến trúc triển khai

| Thành phần       | Công nghệ        | Nền tảng triển khai |
| ---------------- | ---------------- | ------------------- |
| Backend          | Spring Boot      | Render              |
| Voice AI Service | Python + FastAPI | Railway             |
| Database         | PostgreSQL       | Supabase            |
| MQTT Broker      | Mosquitto        | HiveMQ Cloud   |
| Mobile App       | Flutter          | Android             |

---

## Cấu trúc Repository

```text
DATN/
│
├── iot_hardware/      # Mã nguồn ESP32-C3
├── iotbackend/        # Backend Spring Boot
├── voice_model/       # Mô hình AI nhận diện giọng nói
├── datn/              # Ứng dụng Flutter
└── README.md
```

---

#  HƯỚNG DẪN CÀI ĐẶT

## Cài đặt phần cứng (`iot_hardware`)

### Yêu cầu

* Arduino IDE 2.x hoặc VS Code + PlatformIO.
* ESP32 Board Package.
* Các thư viện:

```text
WiFi
PubSubClient
ArduinoJson
```

### Cấu hình

Mở file:

```cpp
DATN_hardware.ino
```

Cập nhật thông tin WiFi và MQTT Broker:

```cpp
const char* ssid = "YOUR_WIFI";
const char* password = "YOUR_PASSWORD";

const char* mqtt_server = "YOUR_MQTT_BROKER";
```

Sau đó:

* Chọn đúng board ESP32C3.
* Chọn đúng cổng COM.
* Nhấn **Upload** để nạp chương trình.

---

##  Cài đặt Backend (`iotbackend`)

Backend được phát triển bằng **Spring Boot 3**.

### Yêu cầu môi trường

* Java JDK 17.
* Maven 3.8+.

### Cấu hình

Mở file:

```text
src/main/resources/application.yml
```

Cập nhật thông tin cơ sở dữ liệu và MQTT:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://host:5432/database
    username: username
    password: password
```

Cài đặt và chạy:

```bash
cd iotbackend

mvn clean install

mvn spring-boot:run
```

Backend sẽ chạy tại:

```text
http://localhost:8080
```

### Production

Backend được triển khai trên:

```text
Render
```

---

##  Cài đặt dịch vụ nhận diện giọng nói (`voice_model`)

Dịch vụ AI được xây dựng bằng Python.

### Yêu cầu

* Python 3.10+
* FFmpeg

### Tạo môi trường ảo

```bash
cd voice_model

python -m venv venv
```

### Kích hoạt môi trường ảo (Windows PowerShell)

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy RemoteSigned

.\venv\Scripts\Activate.ps1
```

Khi kích hoạt thành công:

```powershell
(venv) PS C:\...\voice_model>
```

### Cài đặt thư viện

```bash
pip install -r requirements.txt
```

### Khởi động dịch vụ

```bash
python main.py
```

Dịch vụ AI sẽ chạy tại:

```text
http://localhost:5000
```

### Production

Voice AI Service được triển khai trên:

```text
Railway
```

---

## Cài đặt ứng dụng Flutter (`datn`)

### Yêu cầu

* Flutter SDK 3.x.
* Android Studio.
* Android SDK.

### Cài đặt thư viện

```bash
cd datn

flutter pub get
```

### Kiểm tra môi trường

```bash
flutter doctor
```

### Chạy ứng dụng

```bash
flutter run
```

### Build APK

```bash
flutter build apk --release
```

File APK:

```text
build/app/outputs/flutter-apk/app-release.apk
```

---

# Luồng xử lý dữ liệu

## Điều khiển thiết bị

```text
Flutter App
      │
      │ REST API
      ▼
Spring Boot Backend
      │
      │ MQTT
      ▼
MQTT Broker
      │
      ▼
ESP32-C3
```

## Điều khiển bằng giọng nói
```
                    +-------------------+
                    |   Flutter App     |
                    +-------------------+
                            │
                            │ REST API+Audio File 
                            │
                            ▼
              +-------------------------+
              | Spring Boot Backend     |
              +-------------------------+
                    ▲             │
                    │             │ MQTT
     Command Text   │             ▼
                    │      +--------------+
                    │      | MQTT Broker  |
                    │      +--------------+
                    │             │
                    │             ▼
        +----------------+   +-----------+
        | Voice AI       |   | ESP32-C3 |
        | Service        |   +-----------+
        | (Railway)      |
        +----------------+
```

#  Công nghệ sử dụng

* **Flutter**
* **Spring Boot**
* **Spring Security + JWT**
* **PostgreSQL (Supabase)**
* **Redis**
* **MQTT (Mosquitto)**
* **ESP32-C3**
* **Python**
* **FastAPI**
* **Whisper AI**
* **Render**
* **Railway**

---


