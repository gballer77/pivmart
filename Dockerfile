#### Stage 1: Build the application

FROM openjdk:21
ADD /build/libs/monomart-1.0.0.jar monomart.jar
ENTRYPOINT ["java", "-jar", "/monomart.jar"]
RUN useradd -m myuser
USER myuser
