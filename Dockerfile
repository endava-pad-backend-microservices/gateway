FROM openjdk:12
ADD ./target/gateway-1.0.0.jar gateway.jar
ENV server.url=registry
ENTRYPOINT ["java","-jar","/gateway.jar"]

EXPOSE 8080