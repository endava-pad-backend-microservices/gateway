FROM maven:3.6.1-jdk-12 AS builder
COPY ./ .
RUN mvn clean package

FROM openjdk:12 as Target
COPY --from=builder target/gateway-1.0.0.jar gateway.jar 
ENV server.url=pad-b-registry
ENTRYPOINT ["java","-jar","gateway.jar"]

EXPOSE 8080