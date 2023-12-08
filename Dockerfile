FROM ghcr.io/navikt/fp-baseimages/java:21

LABEL org.opencontainers.image.source=https://github.com/navikt/fpsoknad-mottak

COPY target/*.jar app.jar
