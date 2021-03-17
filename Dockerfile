FROM navikt/java:16
COPY target/*.jar app.jar
ENV JAVA_OPTS --enable-preview

