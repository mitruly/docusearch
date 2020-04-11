#!/usr/bin/env bash

# build artifact
mvn clean install

# prepare to build image
mkdir -p target/docker

cp -a docker/. target/docker
cp target/docusearch-0.0.1-SNAPSHOT.jar target/docker/docusearch.jar

cd target/docker

# build image
docker build . -t docusearch
