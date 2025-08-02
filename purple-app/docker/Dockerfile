FROM docker:28.3.3-cli-alpine3.22

RUN apk update && apk add openjdk21

COPY . /opt/purple-app

WORKDIR /opt/purple-app

RUN ./gradlew test

ENTRYPOINT ["gradle", "run"]
