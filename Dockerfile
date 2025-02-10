# Use Maven image for building the JAR
FROM maven:3.8.7-openjdk-17 AS builder

WORKDIR /app
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# Use a lightweight Java image to run the JAR
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=builder /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
