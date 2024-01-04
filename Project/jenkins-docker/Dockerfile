FROM jenkins/jenkins:lts

USER root

RUN apt-get update && apt-get install curl
RUN curl https://repo1.maven.org/maven2/org/codenarc/CodeNarc/3.3.0 -O

USER jenkins
