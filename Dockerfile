FROM openjdk:17-alpine
ARG JAR_FILE=target/*.jar

RUN mkdir -p /apps

WORKDIR /apps

COPY ${JAR_FILE} application.jar
COPY ./entrypoint.sh entrypoint.sh

RUN adduser -D myuser && chown -R myuser ./
USER myuser

CMD ["/apps/entrypoint.sh"]