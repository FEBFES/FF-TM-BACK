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

## Deploy

1. run `.\mvnw clean package`
