FROM navikt/java:14 
COPY target/*.jar app.jar
ENV JAVA_OPTS --enable-preview

RUN java -Djarmode=layertools -jar app.jar extract
 
FROM navikt/java:14 

COPY  target/dependencies/ ./
COPY  target/snapshot-dependencies/ ./
COPY  target/spring-boot-loader/ ./
COPY  target/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

