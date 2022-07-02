#
# Set a variable that can be used in all stages.
#
ARG BUILD_HOME=/project-group-10

#
# Gradle image for the build stage.
#
FROM gradle:7.1.0-jdk11 as build-image

ENV JAVA_HOME /opt/java/openjdk

#
# Set the working directory.
#
ARG BUILD_HOME
ENV APP_HOME=$BUILD_HOME
WORKDIR $APP_HOME

#
# Copy the Gradle config, source code, and static analysis config
# into the build container.
#
COPY --chown=gradle:gradle build.gradle settings.gradle $APP_HOME/
COPY --chown=gradle:gradle ARMS-Backend $APP_HOME/src
#COPY --chown=gradle:gradle config $APP_HOME/config

#
# Build the application.
#
USER root 
RUN chown -R gradle /project-group-10 # This changes ownership of folder
USER gradle 
RUN whereis java

RUN gradle --no-daemon build  && java -jar build/libs/ARMS-Backend-0.0.1-SNAPSHOT.jar


#
# Java image for the application to run in.
#
FROM openjdk:12-alpine

#
# Copy the jar file in and name it app.jar.
#
ARG BUILD_HOME
ENV APP_HOME=$BUILD_HOME
COPY --from=build-image $APP_HOME/ARMS-Backend/build/libs/ARMS-Backend-0.0.1-SNAPSHOT.jar app.jar

#
# The command to run when the container starts.
#
ENTRYPOINT java -jar app.jar

FROM node:alpine

WORKDIR $APP_HOME/ARMS-Frontend
COPY package.json /app
RUN npm install
COPY . $APP_HOME/ARMS-Frontend
CMD ["npm", "start"]
