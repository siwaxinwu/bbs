FROM openjdk:11
MAINTAINER bert <491705331@qq.com>
COPY ./*.jar /jar/app.jar
CMD java -jar /jar/app.jar