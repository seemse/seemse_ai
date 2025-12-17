#!/bin/bash
rm -f /root/seemse_ai-docker/deploy/mysql-init/*.sql
cp /root/seemse_ai-docker/source-code/seemse_ai-backend/script/sql/seemse_ai.sql /root/seemse_ai-docker/deploy/mysql-init/seemse_ai.sql

