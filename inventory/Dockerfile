#### Stage 1: Build the application

FROM openjdk:21
ADD /build/libs/inventory-1.0.0.jar inventory.jar
ENTRYPOINT ["java", "-jar", "/inventory.jar"]
RUN useradd -m myuser
USER myuser
