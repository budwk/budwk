FROM openjdk:11.0.13-jdk
COPY ./target/wk-mini.jar /app/
ENV TZ Asia/Shanghai

WORKDIR /app
EXPOSE 9900
CMD ["java","-jar","-Dnutz.boot.configure.yaml.dir=/conf","/app/wk-mini.jar"]
