FROM amazoncorretto:latest

ENV AWS_ACCESS_KEY_ID='AKIAUHSJ7ZQM73RQ2B4Z'
ENV AWS_SECRET_ACCESS_KEY='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'
ENV AWS_DEFAULT_REGION='eu-west-2'

WORKDIR /myapp

COPY target/glacier-0.2.0_lib /myapp/glacier-0.2.0_lib

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /myapp/app.jar
ENTRYPOINT [ "java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar" ]
