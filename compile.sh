#!/bin/bash

file="./deploy_env.properties"

while IFS='=' read -r key value
do
    key=$(echo $key | tr '.' '_')
    eval ${key}=\${value}
done < "$file"


export PATH=$PATH:$MAVEN_HOME/bin:$JAVA_HOME/bin

mvn -v

mvn clean package -Dmaven.test.skip=true
