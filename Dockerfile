# Use Maven to build the JAR inside the container
FROM maven:3.8.7-openjdk-17 AS builder

WORKDIR /app

# Copy source code
COPY . .

# Build the application (creates JAR inside /app/target/)
RUN mvn clean package -DskipTests

# Use a smaller JDK image for running the built JAR
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=builder /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
