FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
ENV ES_URL=""
ADD ${JAR_FILE} app.jar
EXPOSE 8094
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","--elasticsearch.url=${ES_URL}"]