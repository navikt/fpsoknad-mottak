FROM navikt/java:14 
COPY target/*.jar app.jar
ENV JAVA_OPTS --enable-preview
RUN java -Djarmode=layertools -jar app.jar extract
 
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

