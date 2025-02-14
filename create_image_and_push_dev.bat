@echo off
cls

set IMAGE_NAME=jeduardogd/qslbureau-back:development

echo %IMAGE_NAME%

docker build -t %IMAGE_NAME% .

docker push %IMAGE_NAME%

pause