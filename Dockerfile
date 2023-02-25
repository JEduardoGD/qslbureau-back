FROM ibmjava:11-jdk
LABEL maintainer="eduardo_gd@hotmail.com"
VOLUME /tmp
#EXPOSE 8080
ARG JAR_FILE=target/capture-0.0.1-SNAPSHOT.jar

RUN apt-get update  -y
RUN apt-get install -y jq
RUN apt-get install -y curl

ADD ${JAR_FILE} qslbureau-back.jar

CMD java -v

ENTRYPOINT ["java", "-jar", "/qslbureau-back.jar"]
