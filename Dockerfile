FROM navikt/java:16-appdynamics

ENV APP_NAME=fpsoknad-mottak
ENV APPD_ENABLED=true
ENV APPDYNAMICS_CONTROLLER_HOST_NAME=appdynamics.adeo.no
ENV APPDYNAMICS_CONTROLLER_PORT=443
ENV APPDYNAMICS_CONTROLLER_SSL_ENABLED=true
COPY target/*.jar app.jar
ENV JAVA_OPTS --enable-preview --illegal-access=permit

