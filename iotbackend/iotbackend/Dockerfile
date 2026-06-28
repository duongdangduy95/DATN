# Bước 1: Dùng Maven để build ra file .jar
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Bước 2: Dùng Java gọn nhẹ để chạy file .jar
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render sẽ tự cấp cổng ngẫu nhiên qua biến $PORT, dòng này giúp Spring Boot tự nhận cổng đó
ENTRYPOINT ["java", "-jar", "-Dserver.port=${PORT:8080}", "app.jar"]