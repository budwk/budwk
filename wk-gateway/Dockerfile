FROM openjdk:11.0.13-jdk
COPY ./target/wk-gateway.jar /app/
ENV TZ Asia/Shanghai

WORKDIR /app
EXPOSE 9900
CMD ["java","-jar","-Dnutz.boot.configure.yaml.dir=/conf","/app/wk-gateway.jar"]
