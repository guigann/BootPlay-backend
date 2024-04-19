FROM eclipse-temurin:21-jdk-alpine
ADD target/app-user-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "/app.jar"] 