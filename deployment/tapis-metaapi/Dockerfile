##########################################################################
# tapis image for metaapi services
#
# usage:
#   docker build -f $BUILD_FILE --build-arg SRVC_WAR=meta.war --build-arg VER=0.1 --build-arg GIT_COMMIT=1234 -t $TAG .
#
#   ${pwd}          the root of the git repository working directory.
#   $SRVC_WAR       the aloe service api being installed as a war file from target
#                     directory   ex. target/jobs.war
#   $CATALINA_HOME  set as installation convention from tomcat image
#   $TAG            the tag for image identification
#
#   Tested with Docker version 18.06.0-ce
##########################################################################
#FROM tomcat:9.0.41-jdk15-openjdk-oracle
FROM tomcat:9.0.58-jdk17-openjdk-bullseye

ARG VER
ARG GIT_COMMIT
ARG BUILD_DATE

MAINTAINER CIC Support <cicsupport@tacc.utexas.edu>

#RUN apt-get update
#RUN apt-get install -y yum

# add nano for logback edits
#RUN yum -y install nano

# add
COPY meta /usr/local/tomcat/webapps/v3/
COPY logback.xml /usr/local/tomcat/etc/

# Set environment and turn on remote debug
ENV CATALINA_HOME /usr/local/tomcat

# Set most Java options here.
ENV CATALINA_OPTS -Xms8g -Xmx16g -Dlogback.configurationFile=/usr/local/tomcat/etc/logback.xml --add-opens java.base/java.time=ALL-UNNAMED

# Debug port and http port
EXPOSE 8000
EXPOSE 8080

LABEL "VER"=$VER "GIT_COMMIT"=$GIT_COMMIT "BUILD_DATE"=$BUILD_DATE

# Launch Tomcat on startup
CMD ${CATALINA_HOME}/bin/catalina.sh run


