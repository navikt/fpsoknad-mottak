FROM gcr.io/distroless/java21-debian12:nonroot
LABEL org.opencontainers.image.source=https://github.com/navikt/fpsoknad-mottak
# Healtcheck lokalt/test
COPY --from=busybox:stable-musl /bin/wget /usr/bin/wget

# Working dir for RUN, CMD, ENTRYPOINT, COPY and ADD (required because of nonroot user cannot run commands in root)
WORKDIR /app

COPY target/*.jar app.jar

CMD ["app.jar"]
