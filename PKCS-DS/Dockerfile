# Smaller OS footprint using JRE
FROM eclipse-temurin:17.0.6_10-jre-alpine
WORKDIR /app

ADD build/libs/*.jar /app/app.jar

CMD ["java", "-jar", "app.jar"]