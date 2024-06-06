FROM maven:3.9.7-eclipse-temurin-21@sha256:7bc6b603a7f9a646e7a31990374fb21119a8a5eb2e713771ee8338b56fbccdcd AS builder
COPY . .
RUN VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:3.4.0:evaluate -Dexpression=project.version -DforceStdout -q) && mvn clean install && mkdir /app && mv ./target/invval-$VERSION.jar /app/invval.jar

FROM eclipse-temurin:21.0.3_9-jre-jammy@sha256:78a82edcacc6cef9fd8c8a276fbd5e08f72fcdfbaf9d28df8b2d9207a7450cb6
WORKDIR /app
COPY --from=builder /app/invval.jar /app/
CMD ["java", "-jar", "invval.jar"]