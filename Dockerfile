FROM maven:3.9.6-eclipse-temurin-21@sha256:b194dc4512295335cd429a58447b932da6fd89a88015ae3329c946e3905f46f5 AS builder
COPY . .
RUN VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:3.4.0:evaluate -Dexpression=project.version -DforceStdout -q) && mvn clean install && mkdir /app && mv ./target/invval-$VERSION.jar /app/invval.jar

FROM eclipse-temurin:21.0.3_9-jre-jammy@sha256:a56ee1f79cf57b2b31152cd471a4c85b6deb3057e4a1fbe8e50b57e7d2a1d7c9
WORKDIR /app
COPY --from=builder /app/invval.jar /app/
CMD ["java", "-jar", "invval.jar"]