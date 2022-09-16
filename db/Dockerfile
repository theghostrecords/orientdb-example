FROM alpine:3.16 as base
RUN apk update && apk upgrade --available
RUN apk add --no-cache tzdata curl bash

ENV PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/lib/jvm/java-11-openjdk/jre/bin:/usr/lib/jvm/java-11-openjdk/bin
ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk
ENV JAVA_VERSION=11u
ENV JAVA_ALPINE_VERSION=11.0.16.1_p1-r0
RUN /bin/sh -c set -x && apk add --no-cache openjdk11="$JAVA_ALPINE_VERSION"


FROM base as orientdb
RUN apk update && apk upgrade --available
RUN apk add --no-cache tar wget bash curl python3 unzip

ENV ORIENTDB_VERSION 3.2.6
ENV ORIENTDB_DOWNLOAD_MD5 08e39080df862d3c5570f834d9d6f92b
ENV ORIENTDB_DOWNLOAD_SHA1 ba07e961cd05dfe2d3bc623e865ae21e533ace68
ENV ORIENTDB_ENTERPRISE_PLUGIN_DOWNLOAD_MD5 b73ddeccab7a020dd00d32462d5c7967
ENV ORIENTDB_ENTERPRISE_PLUGIN_DOWNLOAD_SHA1 ca3e8296d95ae71adfac29c04e6dd35ff293cc0d

ENV ORIENTDB_DOWNLOAD_URL ${ORIENTDB_DOWNLOAD_SERVER:-https://repo1.maven.org/maven2/com/orientechnologies}/orientdb-community/$ORIENTDB_VERSION/orientdb-community-$ORIENTDB_VERSION.tar.gz
ENV ORIENTDB_ENTERPRISE_PLUGIN_DOWNLOAD_URL ${ORIENTDB_DOWNLOAD_SERVER:-https://repo1.maven.org/maven2/com/orientechnologies}/agent/$ORIENTDB_VERSION/agent-$ORIENTDB_VERSION.jar
ENV DEMODB_URL http://orientdb.com/public-databases/3.0.x/demodb.zip

RUN mkdir -p /orientdb && \
  wget $ORIENTDB_DOWNLOAD_URL \
  && echo "$ORIENTDB_DOWNLOAD_MD5 *orientdb-community-$ORIENTDB_VERSION.tar.gz" | md5sum -c - \
  && echo "$ORIENTDB_DOWNLOAD_SHA1 *orientdb-community-$ORIENTDB_VERSION.tar.gz" | sha1sum -c - \
  && tar -xvzf orientdb-community-$ORIENTDB_VERSION.tar.gz -C /orientdb --strip-components=1 \
  && rm orientdb-community-$ORIENTDB_VERSION.tar.gz

FROM orientdb as example

RUN wget ${DEMODB_URL} -P /orientdb/databases
RUN rm /orientdb/databases/demodb.zip

ENV PATH /orientdb/bin:$PATH

WORKDIR /orientdb

#OrientDb binary
EXPOSE 2424

#OrientDb http
EXPOSE 2480

# Default command start the server
CMD ["server.sh"]
