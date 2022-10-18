# COMS W4156 ASE Project

## Team members
Junhao Lin, Ruize Li, Ken Xiong, Jianyang Duan

## How to build and run locally

1. Install [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
2. Create a schema named "4156db" in your local MySQL server
3. Enter the username and the password of your local MySQL server in `src/main/resources/application.properties`
4. By default, the service runs on port 8080. If you wish to use a different port, enter `server.port=<port no.>` in `src/main/resources/application.properties`
5. Run `./mvnw`
6. Run `java -jar ./target/easymoney-0.0.1-SNAPSHOT.war`
7. Use Postman or curl to interact

## How to run tests

1. Create a schema named "4156dbtest" in your local MySQL server
2. Enter the username and the password of your local MySQL server in `src/test/resources/application.properties`
3. By default, the service runs on port 8080. If you wish to use a different port, enter `server.port=<port no.>` in `src/test/resources/application.properties`
4. Install Maven (>=3.8.6 recommended) if you haven't done so
5. Run `mvn clean test`

## API Documentation

First, [run the service locally](#how-to-run-locally). Then open your browser. The API documentation can be viewed in 3 ways (change the port below if needed):
1. UI (recommended): http://localhost:8080/swagger-ui/index.html
2. JSON: http://localhost:8080/api-docs
3. Download the YAML: http://localhost:8080/api-docs.yaml

## Third-party code in use

These are all defined in `pom.xml` and downloaded by Maven.

- Spring: Handles client requests, returns responses to clients, and manages beans. Used everywhere.
- Spring validation: Validates data fields in client requests. Mainly used under `src/main/models/*.java`.
- JPA: Interacts with the database. Mainly used under `src/main/repositories/*.java`.
- lombok: Simplifies the code by avoiding manually writing getters, setters, etc. Mainly used under `src/main/models/*.java` and `src/main/payload/*.java`.

## How to deploy

(TODO)
