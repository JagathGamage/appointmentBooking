FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]
EXPOSE 8080
