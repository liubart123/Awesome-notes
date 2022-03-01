# Awesome-notes
Simple project, created for testing java Spring features.
It's REST server, which allows user to:
- register an account and login
- manipulate with own notes (CRUD)
- manipulate with own labels (CRUD)

### Main features:
- JWT authoriazation (I've used custom implementation instead of Spring Security)
- 2 roles (user and admin)
- Swagger
- Aspect logging
- Dockerized
- Postgres + Jpa 
- Integration and unit tests

Libraries and technologies were used for learning, so they are used at basic level and some code could be much cleaner.

## How to run
To run project in docker you should create jar file:
> mvn clean package

And run docker compose
> docker-compose up

After that server will be running on 8080 port, whichcan be changed in docker-compose.yaml.

#### Swagger
To see swagger documentation go to http://localhost:8080/swagger-ui/index.html

#### Database
Database properties can be setted in docker-compose.yaml, or in application.properties, if server is started without docker.

#### Properties
Other properties (such as secrets, logging levels) can be also setted in application.properties. 
Properties for interation tests are stored in `src/test/resources/application-test.properties`.

#### Logs
By default logs are stored in `logs/` (or `test-logs/` for testing) folders. It can be changed in `logback.xml` file in resources.

### Test
To run unit tests you shoud run command (Your test classes should begins or ends wih "Test") 
> mvn clean test

or 

> mvn clean verify

to run both unit and integration tests (Integration test classes should beins or ends with "IT")
