FROM docker.neg/base/alpine-java-jre:8
VOLUME /tmp
ENV TZ=America/Los_Angeles \
    BASE_DIR=/opt/app/feedflow/
ADD feedflow-job ${BASE_DIR}
WORKDIR ${BASE_DIR}
RUN sed -i 's/\r//' start.sh
ENTRYPOINT ["sh","start.sh"]