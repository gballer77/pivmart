#!/bin/bash
set -e

## Build Monomart docker image
echo "************** Building Monomart jar file **************"
./gradlew clean bootJar

echo "************** Building Monomart docker image **************"
docker build -t monomart-final .

## Build AGW docker image
cd agw
./buildScript.sh
cd ..

## Build Inventory docker image
cd inventory
./buildScript.sh
cd ..

## Build Commerce docker image
cd commerce
./buildScript.sh
cd ..

## Build Config Server docker image
cd config-server
./buildScript.sh
cd ..
