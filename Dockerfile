FROM maven:3.9.6-eclipse-temurin-21@sha256:b194dc4512295335cd429a58447b932da6fd89a88015ae3329c946e3905f46f5 AS builder
COPY . .
RUN VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:3.4.0:evaluate -Dexpression=project.version -DforceStdout -q) && mvn clean install && mkdir /app && mv ./target/invval-$VERSION.jar /app/invval.jar

FROM eclipse-temurin:21.0.3_9-jre-jammy@sha256:f1ceb8a466efc236215a5bf8ef93348f77867ee467f318b8b10f6fdf074de85d
WORKDIR /app
COPY --from=builder /app/invval.jar /app/
CMD ["java", "-jar", "invval.jar"]