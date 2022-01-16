FROM openjdk:11.0.13-jdk
COPY ./wk-cms-server/target/wk-cms-server.jar /app/
ENV TZ Asia/Shanghai

WORKDIR /app
CMD ["java","-jar","-Dnutz.boot.configure.yaml.dir=/conf","/app/wk-cms-server.jar"]
