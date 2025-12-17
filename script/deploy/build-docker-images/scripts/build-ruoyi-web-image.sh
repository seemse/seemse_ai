#!/bin/bash
docker run --rm --name build-seemse_ai-web -v /root/seemse_ai-docker/source-code/seemse_ai-web:/app -w /app node:20 bash -c "npm install -g pnpm && pnpm install && pnpm approve-builds && pnpm build"
rm -rf /root/seemse_ai-docker/build-docker-images/Dockerfile-Resources/seemse_ai-web/dist
cp -pr /root/seemse_ai-docker/source-code/seemse_ai-web/dist /root/seemse_ai-docker/build-docker-images/Dockerfile-Resources/seemse_ai-web/
cd /root/seemse_ai-docker/build-docker-images/Dockerfile-Resources/seemse_ai-web/
docker build -t seemse_ai-web:v2.0.5 .
