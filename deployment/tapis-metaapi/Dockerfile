FROM openjdk:16 AS build
EXPOSE 8080
WORKDIR /app
# Just copy the jars needed
COPY  api/target  ./api/target/
WORKDIR /app/api
CMD ["java", "-cp", "target/meta.jar:target/dependencies/*", "edu.utexas.tacc.tapis.meta.api.MetaApplication"]
