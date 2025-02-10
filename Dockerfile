# Use a valid Maven image to build the JAR
# FROM maven:3.8.8-eclipse-temurin-17 AS builder
FROM maven:3.8.7-amazoncorretto-17 AS builder


WORKDIR /app
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# Use a lightweight Java image for running the JAR
FROM eclipse-temurin:17-jdk-slim

WORKDIR /app
COPY --from=builder /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
