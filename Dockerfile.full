
### phase 1
FROM maven:3.6.1-jdk-8

ADD . /src/
WORKDIR /src

RUN mvn package -DskipTests=true

### phase 2
FROM openjdk:8u275-jre

COPY --from=0 /src/target/ding-0.0.1-SNAPSHOT.jar /root

CMD ["java", "-jar", "/root/ding-0.0.1-SNAPSHOT.jar"]

#EXPOSE 8080