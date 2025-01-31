## Build stage
FROM openjdk:17-alpine AS build
WORKDIR /home/app
COPY .mvn/ .mvn/
COPY mvnw pom.xml lombok.config ./

COPY ./src ./src/
RUN ./mvnw clean install -Dmaven.test.skip

## Package stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /home/app

COPY --from=build /home/app/target/*.jar /home/app/*.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/home/app/*.jar"]
