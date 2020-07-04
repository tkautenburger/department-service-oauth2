FROM openjdk:12-jdk-alpine as builder

# OpenJDK image is about 50MB larger than alpine image
# FROM openjdk:11-slim as builder
WORKDIR application
ARG JAR_FILE=target/@project.build.finalName@.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract
 
FROM openjdk:12-jdk-alpine

# OpenJDK image is about 50MB larger than alpine image
# FROM openjdk:11-slim
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

ENTRYPOINT ["java",  \
"-Dspring.profiles.active=$PROFILE", \
"-Dspring.application.name=$APP_NAME", \
"-Dserver.port=$HTTP_SERVER_PORT", \
"-Dlegendlime.https-port=$HTTPS_SERVER_PORT", \
"-Djavax.net.ssl.trustStore=$TRUST_STORE_PATH", \
"-Djavax.net.ssl.trustStorePassword=$TRUST_STORE_PASSWORD", \
"-Djavax.net.ssl.keyStore=$KEY_STORE_PATH", \
"-Djavax.net.ssl.keyStorePassword=$KEY_STORE_PASSWORD", \
"-Djavax.net.ssl.keyStorePassword=$KEY_STORE_PASSWORD", \
"$HEAP_OPTIONS", \
"org.springframework.boot.loader.JarLauncher"]

