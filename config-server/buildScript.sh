#!/bin/bash
set -e

## Build Config Server docker image
echo "************** Building Config Server jar file **************"
./gradlew clean bootJar

echo "************** Building Config Server docker image **************"
docker build -t monomart-config:v6.0 .
