FROM navikt/java:14 
COPY target/*.jar app.jar
ENV JAVA_OPTS --enable-preview
WORKSPACE .
RUN java -Djarmode=layertools -jar app.jar extract
FROM navikt/java:14 
WORKSPACE .
COPY  dependencies/ ./
COPY  snapshot-dependencies/ ./
COPY  spring-boot-loader/ ./
COPY  application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

