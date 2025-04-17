FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/Planify-app-0.0.1.jar Planify-app-0.0.1.jar

ENTRYPOINT ["java", "-jar", "Planify-app-0.0.1.jar"]