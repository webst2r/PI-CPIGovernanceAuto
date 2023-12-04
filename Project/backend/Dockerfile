# First stage: build the application
FROM maven:3.8.5-openjdk-17 AS build
COPY . /backend
WORKDIR /backend
RUN mvn package -DskipTests

# Second stage: create a slim image
FROM openjdk:17-jdk-slim
COPY --from=build /backend/target/backend-0.0.1-SNAPSHOT.jar /backend.jar
ENTRYPOINT ["java", "-jar", "/backend.jar"]