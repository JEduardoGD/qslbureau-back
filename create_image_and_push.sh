#!/bin/bash

file="./deploy_env.properties"
while IFS='=' read -r key value
do
    key=$(echo $key | tr '.' '_')
    eval ${key}=\${value}
done < "$file"

IMAGE_NAME=${docker_user_name}/qslbureau-back:${environment}

echo ${IMAGE_NAME}

docker build -t ${IMAGE_NAME} .

docker push ${IMAGE_NAME}
