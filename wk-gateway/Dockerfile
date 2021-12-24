FROM openjdk:11.0.13-jdk
COPY ./target/wk-gateway.jar /app/
COPY ./target/classes/application.yaml /app/
ENV TZ Asia/Shanghai

WORKDIR /app
EXPOSE 9900
CMD ["java","-jar","-Dnutz.boot.configure.yaml.dir=/app","wk-gateway.jar"]
