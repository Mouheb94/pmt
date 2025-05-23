FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Install netcat instead of mysql-client
RUN apk add --no-cache netcat-openbsd

COPY --from=build /app/target/*.jar app.jar
COPY wait-for.sh /wait-for.sh
RUN chmod +x /wait-for.sh

EXPOSE 8080

ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["/bin/sh", "-c", "/wait-for.sh db -- java ${JAVA_OPTS} -jar app.jar"]