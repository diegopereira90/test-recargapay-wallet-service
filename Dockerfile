# Stage 1: build
FROM gradle:8.14.3-jdk21 AS build
WORKDIR /app

# Copy source code and build fat JAR
COPY . .
RUN gradle clean bootJar -x test

# Stage 2: runtime
FROM openjdk:21-jdk-slim
WORKDIR /app

# Copy the fat JAR built in the previous stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
