# FEBFES TASK MANAGER BACK

Spring Boot 3 + Java 17

## Running locally (IntelliJ IDEA):

1. Select Spring Boot -> Application
2. Expand "Modify options" -> select "Shorten command line"  -> expand "Shorten command line" -> select "classpath file"
3. Copy the following Java VM options at Applications VM options field
4. "Active profiles" -> dev

### Required VM options

db.url = your created postgres db url

db.username = your postgres username

db.password = your postgres password

```
-Dspring.datasource.url=db.url
-Dspring.datasource.username=db.username
-Dspring.datasource.password=db.password
```

## Style

### Branch naming

New branch: {type of task}/{task number}/{short description}

For example: feature/12/added-new-entity

### Commit naming

New commit: {task number} - {short description}

For example: 12 - added new entity

## Swagger

Swagger is available at: http://localhost:8090/api/swagger-ui/index.html#/

## Liquibase

### Adding new migrations

1. Create new .yaml file in [changelog](src%2Fmain%2Fresources%2Fdb%2Fchangelog).
   File name {version of migration} - {short description}

For example: last migration has num 0.1.2 and our migrations add new table Person
-> 0.1.3-addPersonTable.yaml

2. Add created file in the end
   of [db.changelog-master.yaml](src%2Fmain%2Fresources%2Fdb%2Fchangelog%2Fdb.changelog-master.yaml)

```
databaseChangeLog:
    ...
    - include:
        file: /db/changelog/0.1.2-someAction.yaml
    - include:
        file: /db/changelog/0.1.3-addPersonTable.yaml
```

### Rollback migrations

You need to specify database url, username and password in [liquibase.properties](src%2Fmain%2Fresources%2Fdb%2Fchangelog%2Fliquibase.properties). 

Roll one changeset back:

`./mvnw liquibase:rollback -Dliquibase.rollbackCount=1`
