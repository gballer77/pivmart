#!/bin/bash
set -e

## Build Commerce docker image
echo "************** Building Commerce jar file **************"
./gradlew clean bootJar

echo "************** Building Commerce docker image **************"
docker build -t monomart-commerce-final .