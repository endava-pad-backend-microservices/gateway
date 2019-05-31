FROM maven:3.6.1-jdk-12 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ /build/src/
RUN mvn package

FROM openjdk:12 as Target
COPY --from=builder /build/target/gateway-1.0.0.jar gateway.jar 
ENV server.url=pad-b-registry \
CONFIG_URL=pad-b-config \
CONFIG_PORT=8086
ENTRYPOINT ["java","-jar","gateway.jar"]

EXPOSE 8080