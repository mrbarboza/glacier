FROM amazoncorretto:latest

ENV AWS_ACCESS_KEY_ID='<chave>'
ENV AWS_SECRET_ACCESS_KEY='<chave>'
ENV AWS_DEFAULT_REGION='eu-west-2'

WORKDIR /myapp

COPY target/glacier-0.2.0_lib /myapp/glacier-0.2.0_lib

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /myapp/app.jar
ENTRYPOINT [ "java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar" ]
