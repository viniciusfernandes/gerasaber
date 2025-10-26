FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Make gradlew executable
RUN chmod +x gradlew

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src src

# Build the application
RUN ./gradlew build --no-daemon -x test

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "build/libs/gerasaber-middleware-api-0.0.1-SNAPSHOT.jar"]
