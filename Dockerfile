FROM ghcr.io/navikt/fp-baseimages/java:21

LABEL org.opencontainers.image.source=https://github.com/navikt/fpsoknad-mottak

ENV JAVA_OPTS=" \
      -XX:ActiveProcessorCount=2 \
      -XX:MaxRAMPercentage=75 \
      -XX:+PrintCommandLineFlags"

COPY target/*.jar app.jar
