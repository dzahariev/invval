FROM maven:3.9.6-eclipse-temurin-21@sha256:b194dc4512295335cd429a58447b932da6fd89a88015ae3329c946e3905f46f5 AS builder
COPY . .
RUN VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:3.4.0:evaluate -Dexpression=project.version -DforceStdout -q) && mvn clean install && mkdir /app && mv ./target/invval-$VERSION.jar /app/invval.jar

FROM eclipse-temurin:21.0.2_13-jre-jammy@sha256:603d23272e30bbefa9e7c436a7165c6303b9c67e27aae07472d8ddc748fe96a2
WORKDIR /app
COPY --from=builder /app/invval.jar /app/
CMD ["java", "-jar", "invval.jar"]