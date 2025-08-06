FROM gradle:8.14.3-jdk21-jammy

ENV SKIP_TEST_CONTAINERS=true

COPY . /opt/purple-app

WORKDIR /opt/purple-app

RUN gradle build --parallel

ENTRYPOINT ["gradle", "run"]
