#include <WiFi.h>
#include <WiFiClientSecure.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>
#include <Preferences.h>
#include <WebServer.h>

#define LOG(x) Serial.println(x)
#define LOGF(fmt, ...) Serial.printf(fmt "\n", ##__VA_ARGS__)


#define RELAY_PIN   4
#define LED_PIN     6
#define BUTTON_PIN  9


const char* mqtt_server = "1c96de2709a740a68e5b6d81365811b2.s1.eu.hivemq.cloud";
const int mqtt_port = 8883;
const char* mqtt_user = "duyduong";
const char* mqtt_pass = "Duy10012004/";


WiFiClientSecure espClient;
PubSubClient client(espClient);
Preferences preferences;
WebServer server(80);


String deviceId;
String clientId;


String topicSet, topicState, topicOnline, topicRegister, topicInfo;


bool relayState = false;
bool pairingMode = false;

unsigned long lastInfo = 0;

unsigned long lastMqttRetry = 0;

wl_status_t lastWifiStatus = WL_IDLE_STATUS;

bool lastMqttStatus = false;


bool buttonPressed = false;
unsigned long pressStart = 0;


// Relay
void applyRelay() {
  digitalWrite(RELAY_PIN, relayState);
  digitalWrite(LED_PIN, relayState);
}

void saveState() {
  preferences.begin("dev", false);
  preferences.putBool("relay", relayState);
  preferences.end();
}

void loadState() {
  preferences.begin("dev", true);
  relayState = preferences.getBool("relay", false);
  preferences.end();
  applyRelay();
}

// wifi
void resetWiFi() {

  LOG("RESET WIFI...");

  WiFi.disconnect(true, true);
  delay(500);

  WiFi.mode(WIFI_OFF);
  delay(500);
}


//pairing

String pairToken = ""; // Biến toàn cục lưu token

void saveToken() {
  preferences.begin("dev", false);
  preferences.putString("pair_token", pairToken); // Lưu string vào flash
  preferences.end();
}

void loadToken() {
  preferences.begin("dev", true);
  pairToken = preferences.getString("pair_token", ""); // Nếu chưa có thì mặc định rỗng
  preferences.end();
  LOGF("LOADED TOKEN FROM FLASH: %s", pairToken.c_str());
}

void startPairingMode() {
  LOG("PAIRING MODE START");

  pairingMode = true;

  resetWiFi();

  WiFi.mode(WIFI_AP);

  bool ok = WiFi.softAP("SONOFF-SETUP");

  if (ok) {
    LOG("AP STARTED OK");
    LOG(WiFi.softAPIP().toString());
  } else {
    LOG("AP FAILED");
  }

  // ROUTE TRANG CHỦ 
  server.on("/", []() {
    server.send(200, "text/html",
      "<h2>ESP32 SETUP</h2>"
      "<form action='/save' method='POST'>"
      "SSID:<input name='ssid'><br>"
      "PASS:<input name='pass'><br>"
      "Pair Token:<input name='token'><br>"
      "<button type='submit'>SAVE</button>"
      "</form>"
    );
  });

  // ROUTE XỬ LÝ LƯU (Đã dọn dẹp trùng lặp và sửa ngoặc) 
  server.on("/save", HTTP_POST, []() {
    String ssid = server.arg("ssid");
    String pass = server.arg("pass");
    
    // HỨNG TOKEN TỪ FLUTTER GỬI SANG
    pairToken = server.arg("token"); 

    LOG("NEW CONFIG RECEIVED FROM FLUTTER");
    LOGF("SSID: %s", ssid.c_str());
    LOGF("TOKEN: %s", pairToken.c_str());

    // Lưu ngay vào Flash để tránh mất điện mất cấu hình
    saveToken(); 

    server.send(200, "text/plain", "Connecting...");

    // Quét mạng WiFi (Optional - để debug)
    int n = WiFi.scanNetworks();
    Serial.println("SCAN RESULT");
    for (int i = 0; i < n; i++) {
      Serial.println(WiFi.SSID(i));
    }

    // Bắt đầu kết nối WiFi (Chỉ cần chạy 1 lần duy nhất)
    WiFi.begin(ssid.c_str(), pass.c_str());

    int retry = 0;
    while (WiFi.status() != WL_CONNECTED && retry < 40) {
      delay(500);
      Serial.print(".");
      retry++;
    }
    Serial.println();

    Serial.print("WIFI STATUS = ");
    Serial.println(WiFi.status());

    // Kiểm tra nếu kết nối thành công
    if (WiFi.status() == WL_CONNECTED) {
      LOG("\nWIFI CONNECTED SUCCESS");
      pairingMode = false;
      
      server.stop();
      WiFi.softAPdisconnect(true);

      espClient.setInsecure();
      client.setServer(mqtt_server, mqtt_port);
    }
  }); // <-- Đóng hàm server.on("/save") cực kỳ chuẩn xác ở đây

  server.begin();
}

// MQTT PUBLISH

void publishState() {

  StaticJsonDocument<128> doc;
  doc["deviceId"] = deviceId;
  doc["state"] = relayState;
  doc["ts"] = millis();

  char buf[128];
  serializeJson(doc, buf);

  client.publish(topicState.c_str(), buf, true);
  LOGF("STATE => %s", buf);
}

// void publishRegister() {

//   StaticJsonDocument<256> doc;

//   doc["deviceId"] = deviceId;
//   doc["type"] = "esp32-switch";
//   doc["ip"] = WiFi.localIP().toString();
//   doc["rssi"] = WiFi.RSSI();
//   doc["pairingMode"] = pairingMode;

//   uint8_t mac[6];
//   WiFi.macAddress(mac);

//   char macStr[18];
//   sprintf(macStr, "%02X:%02X:%02X:%02X:%02X:%02X",
//           mac[0], mac[1], mac[2],
//           mac[3], mac[4], mac[5]);

//   doc["mac"] = macStr;

//   char buf[256];
//   serializeJson(doc, buf);

//   client.publish(topicRegister.c_str(), buf, true);
//   LOGF("REGISTER => %s", buf);
// }

void publishOnline(bool online) {

  StaticJsonDocument<64> doc;
  doc["online"] = online;

  char buf[64];
  serializeJson(doc, buf);

  client.publish(topicOnline.c_str(), buf, true);

  LOGF("ONLINE => %s", buf);
}


// BUTTON HANDLER (FIXED)

void handleButton() {

  bool state = digitalRead(BUTTON_PIN);

  if (state == LOW && !buttonPressed) {
    buttonPressed = true;
    pressStart = millis();
    LOG("BUTTON DOWN");
  }

  if (state == HIGH && buttonPressed) {

    buttonPressed = false;

    unsigned long t = millis() - pressStart;

    LOGF("BUTTON PRESS: %lu ms", t);

    if (t > 3000) {
      LOG("FACTORY RESET WIFI");

      resetWiFi();
      startPairingMode();
    } else {
      relayState = !relayState;
      applyRelay();
      saveState();
      publishState();
    }
  }
}


// MQTT CONNECT 

void reconnectMQTT() {

  if (pairingMode) return;

  if (client.connected()) return;

  if (WiFi.status() != WL_CONNECTED) {
    LOG("MQTT WAIT WIFI");
    return;
  }

  if (millis() - lastMqttRetry < 5000) {
    return;
  }

  lastMqttRetry = millis();

  LOG("CONNECT MQTT...");

  String offline = "{\"online\":false}";

  bool ok = client.connect(
      clientId.c_str(),
      mqtt_user,
      mqtt_pass,
      topicOnline.c_str(),
      1,
      true,
      offline.c_str()
    );

  if (ok) {

    LOG("MQTT OK");

    Serial.print("FREE HEAP = ");
    Serial.println(ESP.getFreeHeap());

    client.subscribe(topicSet.c_str());

    publishOnline(true);
    publishRegister();
    publishState();

  } else {

    LOGF("MQTT FAIL %d", client.state());
  }
}

void publishRegister() {
  // Tăng dung lượng JSON doc để chứa token dài
  StaticJsonDocument<384> doc;

  doc["deviceId"] = deviceId;
  doc["type"] = "esp32-switch";
  doc["ip"] = WiFi.localIP().toString();
  doc["rssi"] = WiFi.RSSI();
  doc["pairingMode"] = pairingMode;
  
  // ĐƯA TOKEN VÀO PAYLOAD GỬI VỀ BACKEND
  doc["pairToken"] = pairToken; 

  uint8_t mac[6];
  WiFi.macAddress(mac);
  char macStr[18];
  sprintf(macStr, "%02X:%02X:%02X:%02X:%02X:%02X",
          mac[0], mac[1], mac[2],
          mac[3], mac[4], mac[5]);
  doc["mac"] = macStr;

  char buf[384]; 
  serializeJson(doc, buf);

  client.publish(topicRegister.c_str(), buf, true);
  LOGF("REGISTER => %s", buf);
}

// CALLBACK

void callback(char* topic, byte* payload, unsigned int len) {

  if (pairingMode) return;

  String msg;
  for (int i = 0; i < len; i++) msg += (char)payload[i];

  LOGF("MQTT IN => %s", msg.c_str());

  StaticJsonDocument<128> doc;
  if (deserializeJson(doc, msg)) return;

  if (doc.containsKey("state")) {
    relayState = doc["state"];
    applyRelay();
    saveState();
    publishState();
  }
}


void setup() {

  Serial.begin(115200);
  delay(1500);

  LOG("BOOT DEVICE");

  pinMode(RELAY_PIN, OUTPUT);
  pinMode(LED_PIN, OUTPUT);
  pinMode(BUTTON_PIN, INPUT_PULLUP);

  uint64_t chip = ESP.getEfuseMac();
  char id[20];
  sprintf(id, "esp32-%04X", (uint16_t)chip);

  deviceId = id;
  clientId = "client-" + deviceId;

  LOGF("DEVICE ID: %s", deviceId.c_str());

  topicSet      = "devices/" + deviceId + "/set";
  topicState    = "devices/" + deviceId + "/state";
  topicOnline   = "devices/" + deviceId + "/online";
  topicRegister = "devices/" + deviceId + "/register";
  topicInfo     = "devices/" + deviceId + "/info";

  loadState();

  //
  // AUTO START PAIRING MODE IF NO WIFI
  //
  startPairingMode();

  espClient.setInsecure();
  client.setServer(mqtt_server, mqtt_port);
client.setCallback(callback);

client.setKeepAlive(60);
client.setSocketTimeout(30);

espClient.setInsecure();

WiFi.onEvent([](WiFiEvent_t event, WiFiEventInfo_t info){

  if(event == ARDUINO_EVENT_WIFI_STA_DISCONNECTED){

      Serial.print("DISCONNECT REASON = ");
      Serial.println(info.wifi_sta_disconnected.reason);
  }

});
}


void loop() {

  if (WiFi.status() != lastWifiStatus) {

  Serial.print("WIFI STATUS CHANGED => ");
  Serial.println(WiFi.status());

  lastWifiStatus = WiFi.status();
}

if (client.connected() != lastMqttStatus) {

  Serial.print("MQTT CONNECTED => ");
  Serial.println(client.connected());

  Serial.print("MQTT STATE => ");
  Serial.println(client.state());

  lastMqttStatus = client.connected();
}

if (!pairingMode &&
    WiFi.status() != WL_CONNECTED) {

  static unsigned long lastWifiRetry = 0;

  if (millis() - lastWifiRetry > 10000) {

    lastWifiRetry = millis();

    LOG("RECONNECT WIFI");

    WiFi.reconnect();
  }
}

static wl_status_t oldStatus = WL_IDLE_STATUS;

if (WiFi.status() != oldStatus) {

  Serial.print("WIFI STATUS CHANGED => ");
  Serial.println(WiFi.status());

  oldStatus = WiFi.status();
}

  if (pairingMode) {
    server.handleClient();
    delay(10);
    return;
  }

  handleButton();

  reconnectMQTT();
  client.loop();

  if (millis() - lastInfo > 30000) {
    lastInfo = millis();
  }
}