# syntax=docker/dockerfile:1
FROM jhipster/jhipster:v7.4.0
MAINTAINER Lukas Retschmeier


WORKDIR home/jhipster/app
COPY ../../app /home/jhipster/app

CMD ["./gradlew", "bootRun --args='spring.profiles.active=dev'"]
EXPOSE 8080