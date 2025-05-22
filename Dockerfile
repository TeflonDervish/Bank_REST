FROM ubuntu:latest
LABEL authors="teflondervish"

ENTRYPOINT ["top", "-b"]