FROM openjdk:11.0.13-jdk
COPY ./wk-platform-server/target/wk-platform-server.jar /app/
COPY ./wk-platform-server/target/classes/application.yaml /app/
ENV TZ Asia/Shanghai

WORKDIR /app
CMD ["java","-jar","-Dnutz.boot.configure.yaml.dir=/app","/app/wk-platform-server.jar"]
