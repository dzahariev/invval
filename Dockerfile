FROM maven:3.9.6-eclipse-temurin-21@sha256:fd5186a92c0432cbc9bd948c00838e7ade0b540765cc0c84e95b472f122cbcd3 AS builder
COPY . .
RUN VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:3.4.0:evaluate -Dexpression=project.version -DforceStdout -q) && mvn clean install && mkdir /app && mv ./target/invval-$VERSION.jar /app/invval.jar

FROM eclipse-temurin:21.0.2_13-jre-jammy@sha256:7ee0d51696b3c314bdb96d7a9931140ef2499cc95dccab4dfddbaf2fe59f51a5
WORKDIR /app
COPY --from=builder /app/invval.jar /app/
CMD ["java", "-jar", "invval.jar"]