# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk AS builder

# Set the working directory
WORKDIR /app

# Copy the project files to the container
COPY . .

# Build the application using Gradle
RUN ./gradlew clean build --no-daemon

# Use a minimal JDK runtime for running the app
FROM eclipse-temurin:17-jre

# Set the working directory
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port (Fly.io automatically sets this in `fly.toml`)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
