FROM openjdk:18.0-jdk
RUN apk update && apk upgrade && apk add netcat-openbsd
RUN mkdir -p /usr/local/@docker.image.folder@
ADD @project.build.finalName@.jar /usr/local/@docker.image.folder@/
ADD run.sh run.sh
RUN chmod +x run.sh
CMD ./run.sh
