FROM openjdk:11.0.13-jdk
COPY ./wk-platform-server/target/wk-platform-server.jar /app/
COPY ./wk-platform-server/target/classes/application.yaml /app/

WORKDIR /app
CMD ["java","-jar","-Dnutz.boot.configure.yaml.dir=/app","wk-platform-server.jar"]
