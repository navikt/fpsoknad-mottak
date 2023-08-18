FROM ghcr.io/navikt/fp-baseimages/java:17-appdynamics

LABEL org.opencontainers.image.source=https://github.com/navikt/fpsoknad-mottak

ENV APP_NAME=fpsoknad-mottak
ENV APPD_ENABLED=true
ENV APPDYNAMICS_CONTROLLER_HOST_NAME=appdynamics.adeo.no
ENV APPDYNAMICS_CONTROLLER_PORT=443
ENV APPDYNAMICS_CONTROLLER_SSL_ENABLED=true
COPY target/*.jar app.jar
