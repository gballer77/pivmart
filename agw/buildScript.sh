#!/bin/bash
set -e

## Build AGW docker image
echo "************** Building AGW jar file **************"
./gradlew clean bootJar

echo "************** Building AGW docker image **************"
docker build -t monomart-agw-final .