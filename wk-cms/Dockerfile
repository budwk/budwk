FROM openjdk:11.0.13-jdk
COPY ./wk-cms-server/target/wk-cms-server.jar /app/
COPY ./wk-cms-server/target/classes/application.yaml /app/
ENV TZ Asia/Shanghai

WORKDIR /app
CMD ["java","-jar","-Dnutz.boot.configure.yaml.dir=/app","wk-cms-server.jar"]
