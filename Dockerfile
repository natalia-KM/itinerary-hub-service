# Use an official JDK image as the base image
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy Gradle wrapper and project files
COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
COPY src ./src

# Give execute permission to Gradle wrapper
RUN chmod +x gradlew

# Build the application
RUN ./gradlew clean bootJar --no-daemon

# Second stage: Use a smaller runtime image
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the built jar from the previous stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080

