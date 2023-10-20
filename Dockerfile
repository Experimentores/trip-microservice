FROM openjdk:17
VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE=target/trip-microservice-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} trip-microservice-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/trip-microservice-0.0.1-SNAPSHOT.jar"]

