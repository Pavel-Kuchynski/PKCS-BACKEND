# Smaller OS footprint using JRE
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY build/libs/PKCS-DS-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]