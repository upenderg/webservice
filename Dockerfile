FROM openjdk:11.0.4-jdk

COPY target/webservice-0.2.0.jar /app/webservice-0.1.0.jar

ENTRYPOINT ["java", "-Dspring.data.mongo.client.uri=mongodb://mongo","-Djava.security.egd=file:/dev/./urandom","-jar", "/app/webservice-0.1.0.jar"]