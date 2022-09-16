FROM alpine:3.16 as base
RUN apk update && apk upgrade --available
RUN apk add --no-cache tzdata curl bash

ENV PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/lib/jvm/java-11-openjdk/jre/bin:/usr/lib/jvm/java-11-openjdk/bin
ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk
ENV JAVA_VERSION=11u
ENV JAVA_ALPINE_VERSION=11.0.16.1_p1-r0
RUN /bin/sh -c set -x && apk add --no-cache openjdk11="$JAVA_ALPINE_VERSION"

RUN apk add maven

FROM base as packaged-app
WORKDIR /usr/src/app
COPY ./ ./
RUN mvn clean install -q

FROM packaged-app as app