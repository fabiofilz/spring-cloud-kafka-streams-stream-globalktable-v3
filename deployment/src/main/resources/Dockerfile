# Pull base image
FROM eclipse-temurin:17-alpine
LABEL maintainer="https://github.com/fabiofilz"

# RUN apk add --no-cache openssl curl
RUN apk upgrade --no-cache && apk add --no-cache libgcc libstdc++ ncurses-libs

# Create a user for the service
RUN addgroup -S service-user && adduser -S service-user -G service-user

# Add JAR
COPY --chown=service-user:service-user application/target/spring-cloud-kafka-streams-stream-globalktable-application-*.jar /service.jar

# Add configuration.
COPY --chown=service-user:service-user deployment/target/classes/config /config

# Expose management ports
EXPOSE 8080
EXPOSE 8081

WORKDIR /

USER service-user

# Run the application.
# -Djavax.net.debug=all
ENTRYPOINT java $JAVA_OPTS -Djavax.net.ssl.trustStorePassword=$SPRING_KAFKA_PROPERTIES_SSL_TRUSTSTORE_PASSWORD -Djavax.net.ssl.trustStore=$SPRING_KAFKA_PROPERTIES_SSL_TRUSTSTORE_LOCATION -Dspring.profiles.active=$ENVIRONMENT,k8s -jar /service.jar
