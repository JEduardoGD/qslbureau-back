FROM ibmjava:11-jdk
LABEL maintainer="eduardo_gd@hotmail.com"
VOLUME /tmp
#EXPOSE 8080
ARG JAR_FILE=target/capture-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} qslbureau-back.jar

CMD java -v

ENTRYPOINT ["java", "-jar", "/qslbureau-back.jar"]
