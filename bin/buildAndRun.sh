#!/bin/bash
set -x
mvn clean package
docker build --tag thiernoamiroud/dropwizarh-hibernate-prometheus .
#docker push thiernoamiroud/dropwizarh-hibernate-prometheus:latest
docker container stop java-webapp ; docker container rm java-webapp
docker container run -it --rm --name java-webapp --network docker-compose_prometheus-network -p 8080:8080 -p 8081:8081 -p 8090:8090 --env-file ./docker-compose/database.env -e POSTGRES_DB=dropwizard -e POSTGRES_HOST=postgres -e EHCACHE_CONFIG=ehcache.xml thiernoamiroud/dropwizarh-hibernate-prometheus