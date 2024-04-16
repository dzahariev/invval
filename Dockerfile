FROM maven:3.9.6-eclipse-temurin-21@sha256:db0744d1d8f99bc1050f0fae6041a81fa3981fae21c383ef3d2cbb9b08faf2e6 AS builder
COPY . .
RUN VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:3.4.0:evaluate -Dexpression=project.version -DforceStdout -q) && mvn clean install && mkdir /app && mv ./target/invval-$VERSION.jar /app/invval.jar

FROM eclipse-temurin:21.0.2_13-jre-jammy@sha256:23014d82f9c1e55cfa594364851ad47e02165b68222d3062494a375d3b71decf
WORKDIR /app
COPY --from=builder /app/invval.jar /app/
CMD ["java", "-jar", "invval.jar"]