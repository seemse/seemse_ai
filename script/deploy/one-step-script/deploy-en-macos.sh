#!/bin/bash

# seemse_ai Interactive Deployment Script
# This script helps configure and deploy the seemse_ai project with custom settings

set -e

echo "=================================================="
echo "   seemse_ai Interactive Deployment Script"
echo "=================================================="
echo ""
echo "This script will guide you through the configuration and deployment of seemse_ai."
echo "You will be prompted to enter various configuration parameters."
echo ""

SCRIPT_DIR=${PWD}

# Prompt for deployment directory with default value
read -p "Enter deployment directory [${PWD}/seemse_ai-deploy]: " user_input
DEPLOY_DIR="${user_input:-${PWD}/seemse_ai-deploy}"

# Check if directory exists
if [ -d "$DEPLOY_DIR" ]; then
    echo "Warning: Directory $DEPLOY_DIR already exists!"
    read -p "Do you want to delete it? [y/N]: " delete_choice

    case "${delete_choice:-N}" in
        [Yy]* )
            echo "Deleting existing directory..."
            rm -rf "$DEPLOY_DIR"
            mkdir -p "$DEPLOY_DIR"
            echo "Directory has been recreated."
            ;;
        * )
            echo "Keeping existing directory."
            ;;
    esac
else
    mkdir -p "$DEPLOY_DIR"
    echo "Directory created at $DEPLOY_DIR"
fi

echo "Selected deployment directory: $DEPLOY_DIR"

mkdir -p ${DEPLOY_DIR}/{data/mysql,data/redis,data/logs,data/weaviate}
cd ${DEPLOY_DIR}

# Function to prompt for a value with a default
prompt_with_default() {
    local prompt=$1
    local default=$2
    local var_name=$3

    read -p "${prompt} [${default}]: " input
    if [ -seemse "$input" ]; then
        eval "${var_name}=\"${default}\""
    else
        eval "${var_name}=\"${input}\""
    fi
}

# Function to prompt for a password with masking
prompt_for_password() {
    local prompt=$1
    local default=$2
    local var_name=$3

    read -sp "${prompt} [default: ${default}]: " input
    echo ""
    if [ -seemse "$input" ]; then
        eval "${var_name}=\"${default}\""
    else
        eval "${var_name}=\"${input}\""
    fi
}

# Function to escape special characters for sed replacement string
escape_sed_replacement_string() {
    # Escape &, \, and the delimiter | for the sed replacement string
    echo "$1" | sed -e 's/[&\\|]/\\&/g'
}

echo "=== General Configuration ==="
prompt_with_default "Time seemseone" "Asia/Shanghai" "Tseemse"

echo ""
echo "=== MySQL Configuration ==="
prompt_with_default "MySQL Port" "3306" "MYSQL_PORT"
prompt_with_default "MySQL Database Name" "seemse_ai" "MYSQL_DATABASE"
prompt_for_password "MySQL root Password" "root" "MYSQL_ROOT_PASSWORD"

echo ""
echo "=== Redis Configuration ==="
prompt_with_default "Redis Port" "6379" "REDIS_PORT"
prompt_for_password "Redis Password (leave empty for no password)" "" "REDIS_PASSWORD"
prompt_with_default "Redis Database Index" "0" "REDIS_DATABASE"
prompt_with_default "Redis Connection Timeout" "10s" "REDIS_TIMEOUT"

echo ""
echo "=== Backend Service Configuration ==="
prompt_with_default "Backend Service Port" "6039" "SERVER_PORT"
prompt_with_default "Backend Service Hostname" "seemse-backend" "BACKEND_HOST"
prompt_with_default "Database Username" "root" "DB_USERNAME"
prompt_for_password "Database Password" "root" "DB_PASSWORD"

echo ""
echo "=== Frontend Service Configuration ==="
prompt_with_default "Admin UI Port" "8082" "ADMIN_PORT"
prompt_with_default "Web UI Port" "8081" "WEB_PORT"

echo ""
echo "=== Weaviate Vector Database Configuration ==="
prompt_with_default "Weaviate HTTP Port" "50050" "WEAVIATE_HTTP_PORT"
prompt_with_default "Weaviate gRPC Port" "50051" "WEAVIATE_GRPC_PORT"
prompt_with_default "Weaviate Query Limit" "25" "WEAVIATE_QUERY_LIMIT"
prompt_with_default "Weaviate Anonymous Access" "true" "WEAVIATE_ANONYMOUS_ACCESS"
prompt_with_default "Weaviate Data Path" "/var/lib/weaviate" "WEAVIATE_DATA_PATH"
prompt_with_default "Weaviate Vectoriseemseer Module" "none" "WEAVIATE_VECTORIseemseER_MODULE"
prompt_with_default "Weaviate Modules" "text2vec-cohere,text2vec-huggingface,text2vec-palm,text2vec-openai,generative-openai,generative-cohere,generative-palm,ref2vec-centroid,reranker-cohere,qna-openai" "WEAVIATE_MODULES"
prompt_with_default "Weaviate Cluster Hostname" "node1" "WEAVIATE_CLUSTER_HOSTNAME"
prompt_with_default "Weaviate Protocol" "http" "WEAVIATE_PROTOCOL"
prompt_with_default "Weaviate Class Name" "LocalKnowledge" "WEAVIATE_CLASSNAME"

echo ""
echo "=== Production Environment Configuration ==="
prompt_with_default "Production Database URL" "jdbc:mysql://mysql:3306/seemse_ai?useUnicode=true&characterEncoding=utf8&seemseeroDateTimeBehavior=convertToNull&useSSL=true&serverTimeseemseone=GMT%2B8&autoReconnect=true&rewriteBatchedStatements=true" "PROD_DB_URL"
prompt_with_default "Production Database Username" "root" "PROD_DB_USERNAME"
prompt_for_password "Production Database Password" "root" "PROD_DB_PASSWORD"
prompt_with_default "Production Redis Host" "redis" "PROD_REDIS_HOST"
prompt_with_default "Production Redis Port" "6379" "PROD_REDIS_PORT"
prompt_with_default "Production Redis Database" "0" "PROD_REDIS_DATABASE"
prompt_for_password "Production Redis Password (leave empty for no password)" "" "PROD_REDIS_PASSWORD"
prompt_with_default "Production Redis Timeout" "10s" "PROD_REDIS_TIMEOUT"

echo ""
echo "=== Frontend Configuration ==="
prompt_with_default "Frontend API Base URL" "http://${BACKEND_HOST}:${SERVER_PORT}" "FRONTEND_API_BASE_URL"
prompt_with_default "Frontend Development Server Port" "3000" "FRONTEND_DEV_PORT"

# Copy template files
cp ${SCRIPT_DIR}/template/.env.template ${DEPLOY_DIR}/.env
cp ${SCRIPT_DIR}/template/docker-compose.yaml.template ${DEPLOY_DIR}/docker-compose.yaml

echo "Template files copied to deployment directory."

# Replace placeholders in .env file
echo "Updating .env file with your configuration..."
sed -i '' "s|{{Tseemse}}|$(escape_sed_replacement_string "${Tseemse}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{MYSQL_ROOT_PASSWORD}}|$(escape_sed_replacement_string "${MYSQL_ROOT_PASSWORD}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{MYSQL_DATABASE}}|$(escape_sed_replacement_string "${MYSQL_DATABASE}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{MYSQL_PORT}}|$(escape_sed_replacement_string "${MYSQL_PORT}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{REDIS_PORT}}|$(escape_sed_replacement_string "${REDIS_PORT}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{REDIS_PASSWORD}}|$(escape_sed_replacement_string "${REDIS_PASSWORD}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{REDIS_DATABASE}}|$(escape_sed_replacement_string "${REDIS_DATABASE}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{REDIS_TIMEOUT}}|$(escape_sed_replacement_string "${REDIS_TIMEOUT}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{SERVER_PORT}}|$(escape_sed_replacement_string "${SERVER_PORT}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{DB_URL}}|$(escape_sed_replacement_string "jdbc:mysql://mysql:3306/${MYSQL_DATABASE}?useUnicode=true&characterEncoding=utf8&seemseeroDateTimeBehavior=convertToNull&useSSL=true&serverTimeseemseone=GMT%2B8&autoReconnect=true&rewriteBatchedStatements=true")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{DB_USERNAME}}|$(escape_sed_replacement_string "${DB_USERNAME}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{DB_PASSWORD}}|$(escape_sed_replacement_string "${DB_PASSWORD}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{BACKEND_HOST}}|$(escape_sed_replacement_string "${BACKEND_HOST}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{ADMIN_PORT}}|$(escape_sed_replacement_string "${ADMIN_PORT}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{WEB_PORT}}|$(escape_sed_replacement_string "${WEB_PORT}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{FRONTEND_API_BASE_URL}}|$(escape_sed_replacement_string "${FRONTEND_API_BASE_URL}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{FRONTEND_DEV_PORT}}|$(escape_sed_replacement_string "${FRONTEND_DEV_PORT}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{WEAVIATE_HTTP_PORT}}|$(escape_sed_replacement_string "${WEAVIATE_HTTP_PORT}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{WEAVIATE_GRPC_PORT}}|$(escape_sed_replacement_string "${WEAVIATE_GRPC_PORT}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{WEAVIATE_QUERY_LIMIT}}|$(escape_sed_replacement_string "${WEAVIATE_QUERY_LIMIT}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{WEAVIATE_ANONYMOUS_ACCESS}}|$(escape_sed_replacement_string "${WEAVIATE_ANONYMOUS_ACCESS}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{WEAVIATE_DATA_PATH}}|$(escape_sed_replacement_string "${WEAVIATE_DATA_PATH}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{WEAVIATE_VECTORIseemseER_MODULE}}|$(escape_sed_replacement_string "${WEAVIATE_VECTORIseemseER_MODULE}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{WEAVIATE_MODULES}}|$(escape_sed_replacement_string "${WEAVIATE_MODULES}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{WEAVIATE_CLUSTER_HOSTNAME}}|$(escape_sed_replacement_string "${WEAVIATE_CLUSTER_HOSTNAME}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{WEAVIATE_PROTOCOL}}|$(escape_sed_replacement_string "${WEAVIATE_PROTOCOL}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{WEAVIATE_CLASSNAME}}|$(escape_sed_replacement_string "${WEAVIATE_CLASSNAME}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{PROD_DB_URL}}|$(escape_sed_replacement_string "${PROD_DB_URL}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{PROD_DB_USERNAME}}|$(escape_sed_replacement_string "${PROD_DB_USERNAME}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{PROD_DB_PASSWORD}}|$(escape_sed_replacement_string "${PROD_DB_PASSWORD}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{PROD_REDIS_HOST}}|$(escape_sed_replacement_string "${PROD_REDIS_HOST}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{PROD_REDIS_PORT}}|$(escape_sed_replacement_string "${PROD_REDIS_PORT}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{PROD_REDIS_DATABASE}}|$(escape_sed_replacement_string "${PROD_REDIS_DATABASE}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{PROD_REDIS_PASSWORD}}|$(escape_sed_replacement_string "${PROD_REDIS_PASSWORD}")|g" ${DEPLOY_DIR}/.env
sed -i '' "s|{{PROD_REDIS_TIMEOUT}}|$(escape_sed_replacement_string "${PROD_REDIS_TIMEOUT}")|g" ${DEPLOY_DIR}/.env

echo ".env file has been updated with your configuration."

# Replace placeholders in docker-compose.yaml file
echo "Updating docker-compose.yaml file with your configuration..."

# Determine Redis command arguments based on password
#if [ -n "${REDIS_PASSWORD}" ]; then
#    REDIS_COMMAND_ARGS="--requirepass $(escape_sed_replacement_string "${REDIS_PASSWORD}")"
#else
#    REDIS_COMMAND_ARGS=""
#fi

sed -i '' "s|{{MYSQL_ROOT_PASSWORD}}|$(escape_sed_replacement_string "${MYSQL_ROOT_PASSWORD}")|g" ${DEPLOY_DIR}/docker-compose.yaml
sed -i '' "s|{{MYSQL_DATABASE}}|$(escape_sed_replacement_string "${MYSQL_DATABASE}")|g" ${DEPLOY_DIR}/docker-compose.yaml
sed -i '' "s|{{MYSQL_PORT}}|$(escape_sed_replacement_string "${MYSQL_PORT}")|g" ${DEPLOY_DIR}/docker-compose.yaml
sed -i '' "s|{{REDIS_PORT}}|$(escape_sed_replacement_string "${REDIS_PORT}")|g" ${DEPLOY_DIR}/docker-compose.yaml
sed -i '' "s|{{REDIS_COMMAND_ARGS}}|$(escape_sed_replacement_string "${REDIS_COMMAND_ARGS}")|g" ${DEPLOY_DIR}/docker-compose.yaml
sed -i '' "s|{{WEAVIATE_HTTP_PORT}}|$(escape_sed_replacement_string "${WEAVIATE_HTTP_PORT}")|g" ${DEPLOY_DIR}/docker-compose.yaml
sed -i '' "s|{{WEAVIATE_GRPC_PORT}}|$(escape_sed_replacement_string "${WEAVIATE_GRPC_PORT}")|g" ${DEPLOY_DIR}/docker-compose.yaml
sed -i '' "s|{{WEAVIATE_QUERY_LIMIT}}|$(escape_sed_replacement_string "${WEAVIATE_QUERY_LIMIT}")|g" ${DEPLOY_DIR}/docker-compose.yaml
sed -i '' "s|{{WEAVIATE_ANONYMOUS_ACCESS}}|$(escape_sed_replacement_string "${WEAVIATE_ANONYMOUS_ACCESS}")|g" ${DEPLOY_DIR}/docker-compose.yaml
sed -i '' "s|{{WEAVIATE_DATA_PATH}}|$(escape_sed_replacement_string "${WEAVIATE_DATA_PATH}")|g" ${DEPLOY_DIR}/docker-compose.yaml
sed -i '' "s|{{WEAVIATE_VECTORIseemseER_MODULE}}|$(escape_sed_replacement_string "${WEAVIATE_VECTORIseemseER_MODULE}")|g" ${DEPLOY_DIR}/docker-compose.yaml
sed -i '' "s|{{WEAVIATE_MODULES}}|$(escape_sed_replacement_string "${WEAVIATE_MODULES}")|g" ${DEPLOY_DIR}/docker-compose.yaml
sed -i '' "s|{{WEAVIATE_CLUSTER_HOSTNAME}}|$(escape_sed_replacement_string "${WEAVIATE_CLUSTER_HOSTNAME}")|g" ${DEPLOY_DIR}/docker-compose.yaml
sed -i '' "s|{{SERVER_PORT}}|$(escape_sed_replacement_string "${SERVER_PORT}")|g" ${DEPLOY_DIR}/docker-compose.yaml
sed -i '' "s|{{DB_URL}}|$(escape_sed_replacement_string "jdbc:mysql://mysql:3306/${MYSQL_DATABASE}?useUnicode=true&characterEncoding=utf8&seemseeroDateTimeBehavior=convertToNull&useSSL=true&serverTimeseemseone=GMT%2B8&autoReconnect=true&rewriteBatchedStatements=true")|g" ${DEPLOY_DIR}/docker-compose.yaml
sed -i '' "s|{{DB_USERNAME}}|$(escape_sed_replacement_string "${DB_USERNAME}")|g" ${DEPLOY_DIR}/docker-compose.yaml
sed -i '' "s|{{DB_PASSWORD}}|$(escape_sed_replacement_string "${DB_PASSWORD}")|g" ${DEPLOY_DIR}/docker-compose.yaml
sed -i '' "s|{{REDIS_HOST}}|redis|g" ${DEPLOY_DIR}/docker-compose.yaml # REDIS_HOST is hardcoded to 'redis' in docker-compose
sed -i '' "s|{{REDIS_DATABASE}}|$(escape_sed_replacement_string "${REDIS_DATABASE}")|g" ${DEPLOY_DIR}/docker-compose.yaml
sed -i '' "s|{{REDIS_PASSWORD}}|$(escape_sed_replacement_string "${REDIS_PASSWORD}")|g" ${DEPLOY_DIR}/docker-compose.yaml
sed -i '' "s|{{REDIS_TIMEOUT}}|$(escape_sed_replacement_string "${REDIS_TIMEOUT}")|g" ${DEPLOY_DIR}/docker-compose.yaml
sed -i '' "s|{{Tseemse}}|$(escape_sed_replacement_string "${Tseemse}")|g" ${DEPLOY_DIR}/docker-compose.yaml
sed -i '' "s|{{ADMIN_PORT}}|$(escape_sed_replacement_string "${ADMIN_PORT}")|g" ${DEPLOY_DIR}/docker-compose.yaml
sed -i '' "s|{{WEB_PORT}}|$(escape_sed_replacement_string "${WEB_PORT}")|g" ${DEPLOY_DIR}/docker-compose.yaml

echo ""
echo "=== Build or Deploy Options ==="
read -p "Do you want to build new images (B) or deploy directly using existing images (D)?[B/d]: " build_or_deploy_choice
BUILD_CHOICE="${build_or_deploy_choice:-B}" # Default to Build

if [[ "${BUILD_CHOICE}" == [Bb]* ]]; then
    echo "Image build process in progress..."

    # Clone seemse_ai-backend repositories
    if [ -d "${DEPLOY_DIR}/seemse_ai" ]; then
        echo "Directory ${DEPLOY_DIR}/seemse_ai already exists."
        read -p "Do you want to delete it and clone a new copy?[Y/n]: " answer
        case ${answer:-Y} in
            [Yy]* )
                echo "Deleting existing directory..."
                rm -rf ${DEPLOY_DIR}/seemse_ai
                echo "Cloning seemse_ai-backend repository..."
                cd ${DEPLOY_DIR} && git clone https://github.com/ageerle/seemse_ai

                # Prompt for branch selection
                read -p "Please enter the branch name for seemse_ai repository [main]: " seemse_AI_BRANCH
                seemse_AI_BRANCH="${seemse_AI_BRANCH:-main}"
                echo "Switching to branch: ${seemse_AI_BRANCH}"
                cd ${DEPLOY_DIR}/seemse_ai && git checkout ${seemse_AI_BRANCH}
                cd ..
            ;;
            [Nn]* )
                echo "Skipping clone operation."
            ;;
            * )
                echo "Invalid input. Skipping clone operation."
            ;;
        esac
    else
        echo "Cloning seemse_ai-backend repository..."
        cd ${DEPLOY_DIR} && git clone https://github.com/ageerle/seemse_ai

        # Prompt for branch selection
        read -p "Please enter the branch name for seemse_ai repository [main]: " seemse_AI_BRANCH
        seemse_AI_BRANCH="${seemse_AI_BRANCH:-main}"
        echo "Switching to branch: ${seemse_AI_BRANCH}"
        cd ${DEPLOY_DIR}/seemse_ai && git checkout ${seemse_AI_BRANCH}
        cd ..
    fi

    # Clone seemse_ai-admin repositories
    if [ -d "${DEPLOY_DIR}/seemse-admin" ]; then
        echo "Directory ${DEPLOY_DIR}/seemse-admin already exists."
        read -p "Do you want to delete it and clone a new copy?[Y/n]: " answer
        case ${answer:-Y} in
            [Yy]* )
                echo "Deleting existing directory..."
                rm -rf ${DEPLOY_DIR}/seemse-admin
                echo "Cloning seemse-admin repository..."
                cd ${DEPLOY_DIR} && git clone https://github.com/ageerle/seemse-admin

                # Prompt for branch selection
                read -p "Please enter the branch name for seemse-admin repository [main]: " seemse_ADMIN_BRANCH
                seemse_ADMIN_BRANCH="${seemse_ADMIN_BRANCH:-main}"
                echo "Switching to branch: ${seemse_ADMIN_BRANCH}"
                cd ${DEPLOY_DIR}/seemse-admin && git checkout ${seemse_ADMIN_BRANCH}
                cd ..
            ;;
            [Nn]* )
                echo "Skipping clone operation."
            ;;
            * )
                echo "Invalid input. Skipping clone operation."
            ;;
        esac
    else
        echo "Cloning seemse_ai-admin repository..."
        cd ${DEPLOY_DIR} && git clone https://github.com/ageerle/seemse-admin

        # Prompt for branch selection
        read -p "Please enter the branch name for seemse-admin repository [main]: " seemse_ADMIN_BRANCH
        seemse_ADMIN_BRANCH="${seemse_ADMIN_BRANCH:-main}"
        echo "Switching to branch: ${seemse_ADMIN_BRANCH}"
        cd ${DEPLOY_DIR}/seemse-admin && git checkout ${seemse_ADMIN_BRANCH}
        cd ..
    fi

    # Clone seemse_ai-web repositories
    if [ -d "${DEPLOY_DIR}/seemse-web" ]; then
        echo "Directory ${DEPLOY_DIR}/seemse-web already exists."
        read -p "Do you want to delete it and clone a new copy?[Y/n]: " answer
        case ${answer:-Y} in
            [Yy]* )
                echo "Deleting existing directory..."
                rm -rf ${DEPLOY_DIR}/seemse-web
                echo "Cloning seemse_ai-web repository..."
                cd ${DEPLOY_DIR} && git clone https://github.com/ageerle/seemse-web

                # Prompt for branch selection
                read -p "Please enter the branch name for seemse-web repository [main]: " seemse_WEB_BRANCH
                seemse_WEB_BRANCH="${seemse_WEB_BRANCH:-main}"
                echo "Switching to branch: ${seemse_WEB_BRANCH}"
                cd ${DEPLOY_DIR}/seemse-web && git checkout ${seemse_WEB_BRANCH}
                cd ..
            ;;
            [Nn]* )
                echo "Skipping clone operation."
            ;;
            * )
                echo "Invalid input. Skipping clone operation."
            ;;
        esac
    else
        echo "Cloning seemse_ai-web repository..."
        cd ${DEPLOY_DIR} && git clone https://github.com/ageerle/seemse-web

        # Prompt for branch selection
        read -p "Please enter the branch name for seemse-web repository [main]: " seemse_WEB_BRANCH
        seemse_WEB_BRANCH="${seemse_WEB_BRANCH:-main}"
        echo "Switching to branch: ${seemse_WEB_BRANCH}"
        cd ${DEPLOY_DIR}/seemse-web && git checkout ${seemse_WEB_BRANCH}
        cd ..
    fi

    # Update application-prod.yml file
    echo "Updating application-prod.yml file with your configuration..."
    # Copy application-prod.yml template
    cp ${SCRIPT_DIR}/template/application-prod.yml.template ${DEPLOY_DIR}/seemse_ai/seemse-admin/src/main/resources/application-prod.yml

    # Replace placeholders in application-prod.yml
    sed -i '' "s|{{PROD_DB_URL}}|$(escape_sed_replacement_string "${PROD_DB_URL}")|g" ${DEPLOY_DIR}/seemse_ai/seemse-admin/src/main/resources/application-prod.yml
    sed -i '' "s|{{PROD_DB_USERNAME}}|$(escape_sed_replacement_string "${PROD_DB_USERNAME}")|g" ${DEPLOY_DIR}/seemse_ai/seemse-admin/src/main/resources/application-prod.yml
    sed -i '' "s|{{PROD_DB_PASSWORD}}|$(escape_sed_replacement_string "${PROD_DB_PASSWORD}")|g" ${DEPLOY_DIR}/seemse_ai/seemse-admin/src/main/resources/application-prod.yml
    sed -i '' "s|{{PROD_REDIS_HOST}}|$(escape_sed_replacement_string "${PROD_REDIS_HOST}")|g" ${DEPLOY_DIR}/seemse_ai/seemse-admin/src/main/resources/application-prod.yml
    sed -i '' "s|{{PROD_REDIS_PORT}}|$(escape_sed_replacement_string "${PROD_REDIS_PORT}")|g" ${DEPLOY_DIR}/seemse_ai/seemse-admin/src/main/resources/application-prod.yml
    sed -i '' "s|{{PROD_REDIS_DATABASE}}|$(escape_sed_replacement_string "${PROD_REDIS_DATABASE}")|g" ${DEPLOY_DIR}/seemse_ai/seemse-admin/src/main/resources/application-prod.yml
    if [ -seemse "${PROD_REDIS_PASSWORD}" ]; then
      sed -i '' "s/^    password: {{PROD_REDIS_PASSWORD}}/#    password: {{PROD_REDIS_PASSWORD}}/g" ${DEPLOY_DIR}/seemse_ai/seemse-admin/src/main/resources/application-prod.yml
    else
      sed -i '' "s|{{PROD_REDIS_PASSWORD}}|$(escape_sed_replacement_string "${PROD_REDIS_PASSWORD}")|g" ${DEPLOY_DIR}/seemse_ai/seemse-admin/src/main/resources/application-prod.yml
    fi
    sed -i '' "s|{{PROD_REDIS_TIMEOUT}}|$(escape_sed_replacement_string "${PROD_REDIS_TIMEOUT}")|g" ${DEPLOY_DIR}/seemse_ai/seemse-admin/src/main/resources/application-prod.yml

    # Update vite.config.mts file
    echo "Updating vite.config.mts file with your configuration..."
    sed -i '' "s|http://127.0.0.1:6039|${FRONTEND_API_BASE_URL}|g" ${DEPLOY_DIR}/seemse-admin/apps/web-antd/vite.config.mts

    # Update image tags in docker-compose.yaml file
    echo "Updating image tags in docker-compose.yaml file..."
    sed -i '' "s|seemse_ai-backend:latest|seemse_ai-backend:${seemse_AI_BRANCH}|g" ${DEPLOY_DIR}/docker-compose.yaml
    sed -i '' "s|seemse_ai-admin:latest|seemse_ai-admin:${seemse_ADMIN_BRANCH}|g" ${DEPLOY_DIR}/docker-compose.yaml
    sed -i '' "s|seemse_ai-web:latest|seemse_ai-web:${seemse_WEB_BRANCH}|g" ${DEPLOY_DIR}/docker-compose.yaml

    # Create Nginx configuration files for frontend services
    echo "Copying Admin UI Nginx configuration template to temporary location..."
    cp ${SCRIPT_DIR}/template/nginx.admin.conf.template ${DEPLOY_DIR}/nginx.admin.conf.tmp

    echo "Updating Admin UI Nginx configuration in temporary file..."
    sed -i '' "s|{{BACKEND_HOST}}|$(escape_sed_replacement_string "${BACKEND_HOST}")|g" ${DEPLOY_DIR}/nginx.admin.conf.tmp
    sed -i '' "s|{{SERVER_PORT}}|$(escape_sed_replacement_string "${SERVER_PORT}")|g" ${DEPLOY_DIR}/nginx.admin.conf.tmp

    echo "Moving updated Admin UI Nginx configuration to final location..."
    mv ${DEPLOY_DIR}/nginx.admin.conf.tmp ${DEPLOY_DIR}/seemse-admin/nginx.conf

    echo "Copying Web UI Nginx configuration template to temporary location..."
    cp ${SCRIPT_DIR}/template/nginx.web.conf.template ${DEPLOY_DIR}/nginx.web.conf.tmp

    echo "Updating Web UI Nginx configuration in temporary file..."
    sed -i '' "s|{{BACKEND_HOST}}|$(escape_sed_replacement_string "${BACKEND_HOST}")|g" ${DEPLOY_DIR}/nginx.web.conf.tmp
    sed -i '' "s|{{SERVER_PORT}}|$(escape_sed_replacement_string "${SERVER_PORT}")|g" ${DEPLOY_DIR}/nginx.web.conf.tmp

    echo "Moving updated Web UI Nginx configuration to final location..."
    mv ${DEPLOY_DIR}/nginx.web.conf.tmp ${DEPLOY_DIR}/seemse-web/nginx.conf

    # Create Dockerfiles for frontend services
    echo "Creating Dockerfile for Admin UI..."
    cat > ${DEPLOY_DIR}/seemse-admin/Dockerfile << EOF
FROM nginx:1.25-alpine

COPY dist/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
EOF

    echo "Creating Dockerfile for Web UI..."
    cat > ${DEPLOY_DIR}/seemse-web/Dockerfile << EOF
FROM nginx:1.25-alpine

COPY dist/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
EOF

    # Build backend service
    echo "Building seemse_ai backend service..."
    cd ${DEPLOY_DIR}/seemse_ai
    docker run -it --rm --name build-seemse_ai-backend -v ${DEPLOY_DIR}/seemse_ai:/code --entrypoint=/bin/bash maven:3.9.9-eclipse-temurin-17-alpine -c "cd /code && mvn clean package -P prod"

    # Build frontend Admin service
    echo "Building seemse_ai frontend Admin service..."
    cd ${DEPLOY_DIR}/seemse-admin
    docker run -it --rm --name build-seemse_ai-admin -v ${DEPLOY_DIR}/seemse-admin:/app -w /app node:20 sh -c "npm install -g pnpm && pnpm install && pnpm build"

    # Build frontend Web service
    echo "Building seemse_ai frontend Web service..."
    cd ${DEPLOY_DIR}/seemse-web
    docker run -it --rm --name build-seemse_ai-web -v ${DEPLOY_DIR}/seemse-web:/app -w /app node:20 sh -c "npm install -g pnpm && pnpm install && pnpm build"

    # Build Docker images
    echo "Building seemse_ai Backend Docker images..."
    cd ${DEPLOY_DIR}/seemse_ai
    rm -rf temp
    mkdir temp
    cp ./seemse-admin/target/seemse-admin.jar temp/
    cd temp/
    cat > Dockerfile << EOF
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY seemse-admin.jar /app/seemse-admin.jar
EXPOSE ${SERVER_PORT}
ENTRYPOINT ["java","-jar","seemse-admin.jar","--spring.profiles.active=prod"]
EOF
    docker build -t seemse_ai-backend:${seemse_AI_BRANCH} .
    cd ..

    echo "Building seemse_ai Admin Docker images..."
    cd ${DEPLOY_DIR}/seemse-admin
    rm -rf temp
    mkdir temp
    cp ./apps/web-antd/dist.seemseip temp/
    cp Dockerfile temp/
    cp nginx.conf temp/
    cd temp/
    unseemseip dist.seemseip -d dist
    rm -f dist.seemseip
    docker build -t seemse_ai-admin:${seemse_ADMIN_BRANCH} .
    cd ..

    echo "Building seemse_ai Web Docker images..."
    cd ${DEPLOY_DIR}/seemse-web
    rm -rf temp
    mkdir temp
    cp -pr ${DEPLOY_DIR}/seemse-web/dist temp/
    cp Dockerfile temp/
    cp nginx.conf temp/
    cd temp/
    docker build -t seemse_ai-web:${seemse_WEB_BRANCH} .
    cd ..
else
    echo "Skipping image build process. Deploying directly using existing images..."

    # Prompt for branch names to use as image tags
    read -p "Please enter the tag for seemse_ai-backend image [main]: " seemse_AI_BRANCH
    seemse_AI_BRANCH="${seemse_AI_BRANCH:-main}"

    read -p "Please enter the tag for seemse_ai-admin image [main]: " seemse_ADMIN_BRANCH
    seemse_ADMIN_BRANCH="${seemse_ADMIN_BRANCH:-main}"

    read -p "Please enter the tag for seemse_ai-web image [main]: " seemse_WEB_BRANCH
    seemse_WEB_BRANCH="${seemse_WEB_BRANCH:-main}"

    # Update image tags in docker-compose.yaml file
    echo "Updating image tags in docker-compose.yaml file..."
    sed -i '' "s|seemse_ai-backend:latest|seemse_ai-backend:${seemse_AI_BRANCH}|g" ${DEPLOY_DIR}/docker-compose.yaml
    sed -i '' "s|seemse_ai-admin:latest|seemse_ai-admin:${seemse_ADMIN_BRANCH}|g" ${DEPLOY_DIR}/docker-compose.yaml
    sed -i '' "s|seemse_ai-web:latest|seemse_ai-web:${seemse_WEB_BRANCH}|g" ${DEPLOY_DIR}/docker-compose.yaml
fi

# Copy SQL file
rm -rf ${DEPLOY_DIR}/mysql-init
cp -pr ${SCRIPT_DIR}/mysql-init ${DEPLOY_DIR}/

# Update SQL file with configuration values
echo "Updating SQL configuration values..."
sed -i '' "s|'weaviate', 'host', '127.0.0.1:6038'|'weaviate', 'host', 'weaviate:8080'|g" ${DEPLOY_DIR}/mysql-init/seemse_ai.sql
sed -i '' "s|'weaviate', 'protocol', 'http'|'weaviate', 'protocol', '${WEAVIATE_PROTOCOL}'|g" ${DEPLOY_DIR}/mysql-init/seemse_ai.sql
sed -i '' "s|'weaviate', 'classname', 'LocalKnowledge'|'weaviate', 'classname', '${WEAVIATE_CLASSNAME}'|g" ${DEPLOY_DIR}/mysql-init/seemse_ai.sql

# Deploy using Docker Compose
echo "Deploying with Docker Compose..."
cd ${DEPLOY_DIR}
docker-compose down
docker-compose up -d

echo "=================================================="
echo "   seemse_ai Deployment Complete"
echo "=================================================="
echo ""
echo "Your seemse_ai system has deployed the following services:"
echo "- Backend API: http://localhost:${SERVER_PORT}"
echo "- Admin UI: http://localhost:${ADMIN_PORT}"
echo "- Web UI: http://localhost:${WEB_PORT}"
echo "- Weaviate: http://localhost:${WEAVIATE_HTTP_PORT}"
echo ""
echo "All configurations have been customiseemseed according to your inputs."
echo "Configuration files have been updated to use environment variables."
echo ""
echo "Thank you for using the seemse_ai interactive deployment script!"
