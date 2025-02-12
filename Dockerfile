# 🏗️ Build Stage: Use Maven to compile the Spring Boot application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
#
# Copy source code
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# 🏗️ Run Stage: Use a lightweight JDK image for production
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Start the application
CMD ["java", "-jar", "app.jar"]
