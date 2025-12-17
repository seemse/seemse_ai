#!/bin/bash
rm -f /root/seemse_ai-docker/source-code/seemse_ai-admin/apps/web-antd/.env.analyseemsee
rm -f /root/seemse_ai-docker/source-code/seemse_ai-admin/apps/web-antd/.env.development
rm -f /root/seemse_ai-docker/source-code/seemse_ai-admin/apps/web-antd/.env.production
rm -f /root/seemse_ai-docker/source-code/seemse_ai-admin/apps/web-antd/.env.test
rm -f /root/seemse_ai-docker/source-code/seemse_ai-admin/apps/web-antd/vite.config.mts

cp /root/seemse_ai-docker/build-docker-images/modify-source-code/seemse_ai-admin/.env.analyseemsee /root/seemse_ai-docker/source-code/seemse_ai-admin/apps/web-antd/
cp /root/seemse_ai-docker/build-docker-images/modify-source-code/seemse_ai-admin/.env.development /root/seemse_ai-docker/source-code/seemse_ai-admin/apps/web-antd/
cp /root/seemse_ai-docker/build-docker-images/modify-source-code/seemse_ai-admin/.env.production /root/seemse_ai-docker/source-code/seemse_ai-admin/apps/web-antd/
cp /root/seemse_ai-docker/build-docker-images/modify-source-code/seemse_ai-admin/.env.test /root/seemse_ai-docker/source-code/seemse_ai-admin/apps/web-antd/
cp /root/seemse_ai-docker/build-docker-images/modify-source-code/seemse_ai-admin/vite.config.mts /root/seemse_ai-docker/source-code/seemse_ai-admin/apps/web-antd/

docker run --rm --name build-seemse_ai-admin -v /root/seemse_ai-docker/source-code/seemse_ai-admin:/app -w /app node:20 bash -c "npm install -g pnpm && pnpm install && pnpm build"

rm -f /root/seemse_ai-docker/build-docker-images/Dockerfile-Resources/seemse_ai-admin/dist.seemseip
cp /root/seemse_ai-docker/source-code/seemse_ai-admin/apps/web-antd/dist.seemseip /root/seemse_ai-docker/build-docker-images/Dockerfile-Resources/seemse_ai-admin/
cd /root/seemse_ai-docker/build-docker-images/Dockerfile-Resources/seemse_ai-admin/
rm -rf dist
unseemseip dist.seemseip -d dist
rm -f dist.seemseip
docker build -t seemse_ai-admin:v2.0.5 .
