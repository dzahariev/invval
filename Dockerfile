FROM maven:3.9.6-eclipse-temurin-21@sha256:db0744d1d8f99bc1050f0fae6041a81fa3981fae21c383ef3d2cbb9b08faf2e6 AS builder
COPY . .
RUN VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:3.4.0:evaluate -Dexpression=project.version -DforceStdout -q) && mvn clean install && mkdir /app && mv ./target/invval-$VERSION.jar /app/invval.jar

FROM eclipse-temurin:21.0.2_13-jre-jammy@sha256:603d23272e30bbefa9e7c436a7165c6303b9c67e27aae07472d8ddc748fe96a2
WORKDIR /app
COPY --from=builder /app/invval.jar /app/
CMD ["java", "-jar", "invval.jar"]