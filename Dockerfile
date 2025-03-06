# Use an official OpenJDK runtime as a base image
FROM eclipse-temurin:17-jdk

# Set working directory inside the container
WORKDIR /app

# Copy Gradle files and download dependencies (use caching)
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle
RUN ./gradlew build --no-daemon || exit 0

# Copy the application JAR file
COPY build/libs/*.jar app.jar

# Expose port 8080 (Spring Boot default)
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "app.jar"]
