FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Ensure the JAR file is correctly referenced
COPY target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
