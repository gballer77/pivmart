#!/bin/bash
set -e

## Build Inventory docker image
echo "************** Building Inventory jar file **************"
./gradlew clean bootJar

echo "************** Building Inventory docker image **************"
docker build -t monomart-inventory-final .