FROM eclipse-temurin:25-jre-alpine-3.23
LABEL authors="fonfalleh"

RUN apk add --no-cache musescore

RUN addgroup -S javalin; adduser -S javalin; mkdir -p /opt/javalin/jte-classes; chown -R javalin:javalin /opt/javalin

COPY target/app.jar /opt/javalin/
COPY target/jte-classes /opt/javalin/jte-classes

EXPOSE 7070

USER javalin:javalin
WORKDIR /opt/javalin
ENTRYPOINT ["java", "-Dsolr.host=playin-solr", "-Dprecompiled.templates=true", "-jar", "app.jar"]
