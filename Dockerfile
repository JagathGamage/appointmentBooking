# Use Maven to build JAR inside the container
FROM maven:3.8.7-openjdk-17 AS build

WORKDIR /app

# Copy source code
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# Use lightweight JDK for final image
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
