## Build stage
FROM amazoncorretto:17-alpine AS build
WORKDIR /home/app

COPY ./ff-tm-back/.mvn/ .mvn/
COPY ./ff-tm-back/mvnw ./mvnw
COPY ./ff-tm-back/pom.xml ./pom.xml
COPY ./ff-tm-back/lombok.config ./lombok.config
COPY ./ff-tm-back/src ./src/

RUN chmod +x mvnw
# clean up the file
RUN sed -i 's/\r$//' mvnw
# run with the SH path
RUN /bin/sh mvnw dependency:resolve
RUN ./mvnw clean install -DskipTests

## Package stage
FROM amazoncorretto:17-alpine
WORKDIR /home/app

COPY --from=build /home/app/target/*.jar /home/app/*.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/home/app/*.jar"]
