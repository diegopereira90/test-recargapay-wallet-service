# Stage 1: build
FROM gradle:8.14.3-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle clean bootJar -x test

# Stage 2: runtime
FROM openjdk:21-jdk-slim
WORKDIR /app

# Copia o JAR fat gerado no build
COPY --from=build /app/build/libs/*.jar app.jar

# Expõe porta
EXPOSE 8080

# Executa a aplicação
ENTRYPOINT ["java","-jar","app.jar"]
