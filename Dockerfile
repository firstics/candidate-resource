FROM openjdk:8

USER root
ARG PORT

RUN set -x
ENV JAVA_OPTS="-Dconfig.file=/src/candidate-resource/stage/conf/application.conf"

RUN mkdir -p /src/candidate-resource/stage/conf/
RUN mkdir -p /src/candidate-resource/stage/bin/
RUN mkdir -p /src/candidate-resource/stage/lib/

WORKDIR /src/candidate-resource/stage/

COPY ./src/main/resources/* /src/candidate-resource/stage/conf/
COPY ./target/universal/stage/bin/* /src/candidate-resource/stage/bin/
COPY ./target/universal/stage/lib/* /src/candidate-resource/stage/lib/

EXPOSE ${PORT}

CMD ["/src/candidate-resource/stage/bin/candidate-resource"]