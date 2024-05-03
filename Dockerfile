FROM maven:3.9.6-eclipse-temurin-21@sha256:a56564e1101ee96bf451f847e74c67b55c104638f20e12aa72965e85626256b5 AS builder
COPY . .
RUN VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:3.4.0:evaluate -Dexpression=project.version -DforceStdout -q) && mvn clean install && mkdir /app && mv ./target/invval-$VERSION.jar /app/invval.jar

FROM eclipse-temurin:21.0.3_9-jre-jammy@sha256:0f8bc645fb0c9ab40c913602c9f5f12c32d9ae6bef3e34fa0469c98e7341333c
WORKDIR /app
COPY --from=builder /app/invval.jar /app/
CMD ["java", "-jar", "invval.jar"]