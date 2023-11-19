#FROM openjdk:8u322-jre-slim-buster
FROM amazoncorretto:17.0.9-alpine
#FROM openjdk:17-jdk
MAINTAINER Thierno Amirou DIALLO thiernoamiroud@gmail.com
RUN #apt-get update && apt-get install -y iputils-ping
WORKDIR /usr/local/bin
COPY target/dropwizart-getting-started-1.0-SNAPSHOT.jar /usr/local/bin/webapp.jar
COPY keystore/client-identity.p12 src/main/resources/ehcache.xml src/main/resources/introduction-config.yml /usr/local/bin/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "webapp.jar", "server", "introduction-config.yml" ]