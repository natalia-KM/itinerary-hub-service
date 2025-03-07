FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy Gradle files and download dependencies (use caching)
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle
RUN ./gradlew build --no-daemon || exit 0

COPY ./build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]