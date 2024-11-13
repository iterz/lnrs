# LNRS-Company-Search-Service

## Overview
The simplest possible thing that could work for the specified criteria

## Building
This project uses Maven for building. It optionally includes a Swagger UI by specifying the maven profile. To build the project, run:

```
mvn clean install [-PswaggerUI]
```

## Example Usage

To run the app (with the local profile) from the command-line:

```
mvn [-PswaggerUI] spring-boot:run -Dspring-boot.run.profiles=local
```

## Swagger UI

http://localhost:8080/swagger-ui/index.html