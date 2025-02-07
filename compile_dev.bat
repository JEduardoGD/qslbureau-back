@echo off
cls

set JAVA_HOME=C:\sft\java\jdk-21.0.5
set MAVEN_HOME=C:\sft\maven\apache-maven-3.9.9

echo --------------------------------------------------------
call %MAVEN_HOME%/bin/mvn -v
echo --------------------------------------------------------

call %MAVEN_HOME%/bin/mvn clean package -Dmaven.test.skip=true
pause