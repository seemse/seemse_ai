#!/bin/bash
rm -f /root/seemse_ai-docker/source-code/seemse_ai-backend/seemse-admin/src/main/resources/application-prod.yml
cp /root/seemse_ai-docker/build-docker-images/modify-source-code/seemse_ai-backend/application-prod.yml /root/seemse_ai-docker/source-code/seemse_ai-backend/seemse-admin/src/main/resources/application-prod.yml
docker run --rm --name build-seemse_ai-backend -v /root/seemse_ai-docker/source-code/seemse_ai-backend:/app -w /app maven:3.9.9-eclipse-temurin-17-alpine bash -c "mvn clean package -Pprod"
rm -f /root/seemse_ai-docker/build-docker-images/Dockerfile-Resources/seemse_ai-backend/seemse-admin.jar
cp /root/seemse_ai-docker/source-code/seemse_ai-backend/seemse-admin/target/seemse-admin.jar /root/seemse_ai-docker/build-docker-images/Dockerfile-Resources/seemse_ai-backend/
cd /root/seemse_ai-docker/build-docker-images/Dockerfile-Resources/seemse_ai-backend/
docker build -t seemse_ai-backend:v2.0.5 .
