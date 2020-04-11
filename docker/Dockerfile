FROM openjdk:8u201-jdk-alpine3.9

# install bash
RUN apk add --update --no-cache bash

# create document folder
RUN mkdir -p /app/files

# copy app dependencies
COPY files/ /app/files/
COPY docker.yml docusearch.jar run.sh /app/

# add execute permissions
RUN chmod a+x /app/run.sh

# document port
EXPOSE 9000

CMD ["/app/run.sh"]
