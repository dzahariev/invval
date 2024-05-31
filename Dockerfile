FROM maven:3.9.7-eclipse-temurin-21@sha256:7bc6b603a7f9a646e7a31990374fb21119a8a5eb2e713771ee8338b56fbccdcd AS builder
COPY . .
RUN VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:3.4.0:evaluate -Dexpression=project.version -DforceStdout -q) && mvn clean install && mkdir /app && mv ./target/invval-$VERSION.jar /app/invval.jar

FROM eclipse-temurin:21.0.3_9-jre-jammy@sha256:0f8bc645fb0c9ab40c913602c9f5f12c32d9ae6bef3e34fa0469c98e7341333c
WORKDIR /app
COPY --from=builder /app/invval.jar /app/
CMD ["java", "-jar", "invval.jar"]