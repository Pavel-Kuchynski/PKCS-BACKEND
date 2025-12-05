# Smaller OS footprint using JRE
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY build/libs/PKCS-CORE-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]