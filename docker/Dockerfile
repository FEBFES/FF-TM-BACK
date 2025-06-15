## Build stage
FROM amazoncorretto:17-alpine AS build
WORKDIR /home/app

COPY ./ff-tm-back/.mvn/ .mvn/
COPY ./ff-tm-back/mvnw ./mvnw
COPY ./ff-tm-back/pom.xml ./pom.xml
COPY ./ff-tm-back/lombok.config ./lombok.config
COPY ./ff-tm-back/src ./src/

# build commons module first
COPY ./febfes-commons ./febfes-commons

RUN chmod +x mvnw
RUN sed -i 's/\r$//' mvnw
RUN ./mvnw -f febfes-commons/pom.xml clean install -DskipTests

RUN ./mvnw clean install -DskipTests

## Package stage
FROM amazoncorretto:17-alpine
WORKDIR /home/app

COPY --from=build /home/app/target/*.jar /home/app/*.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/home/app/*.jar"]
