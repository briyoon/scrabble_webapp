FROM amazoncorretto:17-alpine3.17-jdk

COPY . /app

RUN apk add maven

WORKDIR /app

CMD mvn spring-boot:run

EXPOSE 8080