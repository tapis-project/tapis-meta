##########################################################################
# tapis image for metaapi services
#
# usage:
#   docker build -t $TAG .
#
##########################################################################
FROM raffis/mongodb-query-exporter:v1.0.0-beta6

MAINTAINER CIC Support <cicsupport@tacc.utexas.edu>

# add nano for logback edits
# RUN yum -y install nano

# add
COPY mqe-config.yml /etc/mongodb-query-exporter/config.yaml



