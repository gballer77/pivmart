#### Stage 1: Build the application

FROM openjdk:21
ADD /build/libs/commerce-1.0.0.jar commerce.jar
ENTRYPOINT ["java", "-jar", "/commerce.jar"]
RUN useradd -m myuser
USER myuser
