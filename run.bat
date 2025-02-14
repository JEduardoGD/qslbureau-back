@echo off
cls

set JAVA_HOME=C:\sft\java\jdk-21.0.5

set ALLOWED_CORS_ORIGINS=localhost
set FRONT_NAMESERVER=
set JWT_SECRET=rrD61pYECs7jdqRS
set MYSQL_DB_PASSWORD=88LbWiP$K^^CB3H3h
set MYSQL_DB_URL=jdbc:mysql://192.168.0.199:6033/fmremx1_qslbureau_dev_db?useUnicode=true^&useJDBCCompliantTimezoneShift=true^&useLegacyDatetimeCode=false^&serverTimezone=UTC^&characterEncoding=utf-8^&enabledTLSProtocols=TLSv1.2
set MYSQL_DB_USERNAME=fmremx1_qslbureau_dev_usr
set QRZ_PASSWORD=2YsT3qceBnjfw1Q3cLFvCj5kXyL28P
set QRZ_USERNAME=XE1JEG
set ROOT_LOG_LEVEL=INFO

%JAVA_HOME%/bin/java -jar target/capture-0.0.1-SNAPSHOT.jar

pause