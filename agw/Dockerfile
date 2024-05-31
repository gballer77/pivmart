#### Stage 1: Build the application

FROM openjdk:21
ADD /build/libs/gateway-1.0.0.jar gateway.jar
ENTRYPOINT ["java", "-jar", "/gateway.jar"]
RUN useradd -m myuser
USER myuser
