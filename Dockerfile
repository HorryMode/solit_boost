FROM amazoncorretto:17-alpine

LABEL maintainer = "dkoshkin@solit-clouds.ru"

VOLUME /tmp

VOLUME /home/dkoshkin/storage:/storage

EXPOSE 8089

ENV SPRING_PROFILES_ACTIVE=test

ARG JAR_FILE=target/skill.over.mobile-1.0.0.jar

ADD ${JAR_FILE} skill.over.mobile.jar

ENTRYPOINT "java" "-jar" "/skill.over.mobile.jar"