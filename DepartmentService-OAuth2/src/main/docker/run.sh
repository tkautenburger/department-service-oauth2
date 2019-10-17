#!/bin/sh

echo "******************************************************************"
echo "Reference Architecture - Starting Service"
echo "Application Name: $APP_NAME"
echo "HTTPS Port:       $HTTPS_SERVER_PORT"
echo "HTTP  Port:       $HTTP_SERVER_PORT"
echo "******************************************************************"

java -Dspring.profiles.active=$PROFILE \
	 -Dspring.application.name=$APP_NAME \
     -Dserver.port=$HTTP_SERVER_PORT \
     -Dlegendlime.https-port=$HTTPS_SERVER_PORT \
     -Djavax.net.ssl.trustStore=$TRUST_STORE_PATH \
     -Djavax.net.ssl.trustStorePassword=$TRUST_STORE_PASSWORD \
	 -jar /usr/local/@docker.image.folder@/@project.build.finalName@.jar
