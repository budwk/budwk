FROM openjdk:11.0.13-jdk
COPY ./target/wk-ucenter.jar /app/
ENV TZ Asia/Shanghai

WORKDIR /app
CMD ["java","-jar","-Dnutz.boot.configure.yaml.dir=/conf","/app/wk-ucenter.jar"]
