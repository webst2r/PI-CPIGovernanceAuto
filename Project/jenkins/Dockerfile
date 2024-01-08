FROM jenkins/jenkins:lts

ENV CLASSPATH /cp

USER root

RUN mkdir "$CLASSPATH"
RUN curl https://repo1.maven.org/maven2/org/codenarc/CodeNarc/3.3.0/CodeNarc-3.3.0-all.jar -o "$CLASSPATH/codenarc.jar"

RUN curl -L https://github.com/mwittrock/cpilint/releases/download/v1.0.4/cpilint-1.0.4.zip -o "$CLASSPATH/cpilint.zip"
RUN unzip "$CLASSPATH/cpilint.zip" -d "$CLASSPATH"
RUN chmod +x "$CLASSPATH/cpilint-1.0.4/bin/cpilint"

RUN curl -L https://github.com/jeremylong/DependencyCheck/releases/download/v9.0.8/dependency-check-9.0.8-release.zip -o "$CLASSPATH/dependency-check.zip"
RUN unzip "$CLASSPATH/dependency-check.zip" -d "$CLASSPATH"
RUN chmod +x "$CLASSPATH/dependency-check/bin/dependency-check.sh"

USER jenkins
