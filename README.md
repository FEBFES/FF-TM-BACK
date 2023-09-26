# FEBFES TASK MANAGER BACK

Spring Boot 3 + Java 17. Monolith app

## Running locally (IntelliJ IDEA):

1. Select Spring Boot -> Application
2. Expand "Modify options" -> select "Shorten command line"  -> expand "Shorten command line" -> select "classpath file"
3. Copy the following Java VM options at Applications VM options field
4. "Active profiles" -> dev

### Required VM options

db.url = your created postgres db url

db.username = your postgres username

db.password = your postgres password

jwt.secret = jwt secret key

```
-Dspring.datasource.url=db.url
-Dspring.datasource.username=db.username
-Dspring.datasource.password=db.password
-Djwt.secret=jwt.secret
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

Create new .yaml file in [changes](src%2Fmain%2Fresources%2Fdb%2Fchangelog%2Fchanges)

#### New migration naming 

The migration name should be like this: `<year>.<month>.<sequence_number>-<short_description>.yaml`

Example: 2023.03.01-create-task-table.yaml. Year and month of 
creation are 2023 and 3 (March). Sequence number is 1, because 
it's the first migration in March. Short description: created 
new table for tasks.

#### View changes

If changes are made in the view, or a new view is created, a 
new file must be created in the folder [view](src%2Fmain%2Fresources%2Fdb%2Fchangelog%2Fview) for this

### Rollback migrations

Not currently supported
