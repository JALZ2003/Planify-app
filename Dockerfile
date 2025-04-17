FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/Planify-app-0.0.1.jar Planify-app-0.0.1.jar

ENTRYPOINT ["java", "-jar", "Planify-app-0.0.1.jar"]