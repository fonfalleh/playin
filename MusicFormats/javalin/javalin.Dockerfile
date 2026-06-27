FROM eclipse-temurin:25-jre-alpine-3.23
LABEL authors="fonfalleh"

RUN apk add --no-cache musescore

RUN addgroup -S javalin; adduser -S javalin; mkdir -p /opt/javalin; chown -R javalin:javalin /opt/javalin

COPY target/app.jar /opt/javalin/

EXPOSE 7070

USER javalin:javalin
WORKDIR /opt/javalin
ENTRYPOINT ["java", "-jar", "app.jar"]
