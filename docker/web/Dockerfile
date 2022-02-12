FROM openjdk:8-jre-alpine

WORKDIR /home

COPY target/json-schema-validator-with-dependencies.jar /json-schema-validator-with-dependencies.jar

EXPOSE 7000

ENTRYPOINT ["java", "-jar", "/json-schema-validator-with-dependencies.jar"]