FROM openjdk:17-jdk-alpine
# Set locale environment variables
ENV JAVA_OPTS="-Dfile.encoding=UTF-8"
ENV LANG=en_US.UTF-8
ENV LANGUAGE=en_US.UTF-8
ENV LC_ALL=en_US.UTF-8
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
VOLUME /tmp
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY target/*.jar pffc.jar
EXPOSE 8080
ARG BUILD_TIME 'N/A'
ENV BUILD_TIME=${BUILD_TIME}
ARG COMMIT_MESSAGE 'N/A'    
ENV COMMIT_MESSAGE=${COMMIT_MESSAGE}
ARG COMMIT_HASH 'N/A'
ENV COMMIT_HASH=${COMMIT_HASH}

#ENTRYPOINT exec java $JAVA_OPTS -jar pffc.jar
# For Spring-Boot project, use the entrypoint below to reduce Tomcat startup time.
# https://dev.to/onticdani/automatically-build-docker-images-with-github-actions-3n8e
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar pffc.jar

