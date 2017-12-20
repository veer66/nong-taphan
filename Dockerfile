FROM openjdk:8-jre-alpine
ENV PORT 3122
COPY target/uberjar/server.jar /
COPY config.clj /
COPY run.sh /
ADD data /data
EXPOSE 3122
ENTRYPOINT ["./run.sh"]
