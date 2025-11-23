# Multi-stage build for Spring Boot application

# Stage 1: Build stage
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

# Download dependencies (cached layer)
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src src

# Build the application
RUN ./gradlew clean build -x test --no-daemon

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# Create a non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copy the built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose the application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

