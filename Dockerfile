FROM amazoncorretto:17

WORKDIR /app

COPY target/BANK_REST-1.0.jar BANK_REST-1.0.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "BANK_REST-1.0.jar"]