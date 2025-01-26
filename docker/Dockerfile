## Build stage
FROM openjdk:17-alpine AS build
WORKDIR /home/app
COPY ../ff-tm-back/.mvn/ .mvn/
COPY ../ff-tm-back/mvnw ../ff-tm-back/pom.xml ../ff-tm-back/lombok.config ./

COPY ../ff-tm-back/src ./src/
RUN ../ff-tm-back/mvnw clean install -Dmaven.test.skip

## Package stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /home/app

COPY --from=build /home/app/target/*.jar /home/app/*.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/home/app/*.jar"]
