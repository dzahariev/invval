FROM maven:3.9.8-eclipse-temurin-21 AS builder
COPY . .
RUN VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:3.4.0:evaluate -Dexpression=project.version -DforceStdout -q) && mvn clean install && mkdir /app && mv ./target/invval-$VERSION.jar /app/invval.jar

FROM eclipse-temurin:21.0.3_9-jre-jammy
WORKDIR /app
COPY --from=builder /app/invval.jar /app/
CMD ["java", "-jar", "invval.jar"]