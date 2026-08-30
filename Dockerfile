# Multi-stage build: сначала собираем JAR, потом — минимальный runtime-образ.
# Stage 1: сборка приложения через Gradle wrapper.
FROM gradle:8.14.3-jdk17 AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew && ./gradlew build -x test

# Stage 2: runtime — только JRE и JAR, без исходников и Gradle.
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
