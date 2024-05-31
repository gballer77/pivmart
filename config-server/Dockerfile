#### Stage 1: Build the application

FROM openjdk:21
ADD /build/libs/config-server-1.0.0.jar config-server.jar
ENTRYPOINT ["java", "-jar", "/config-server.jar"]
RUN useradd -m myuser
USER myuser
