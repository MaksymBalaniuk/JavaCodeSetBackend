# JavaCodeSetBackend

This project is a server part of an application that stores small blocks 
of Java code, allows you to save them, search by name, description, tags, 
content. It was created for the ease and convenience of personal storage 
and management of short code demos that you don't want to lose.

## Used modules

The application uses the following submodules:
- Spring Boot
- Spring Security
- PostgreSQL
- Liquibase
- Lombok
- H2 database
- Model mapper
- Json Web Token
- Springfox
- Junit
- Http client

The app runs on Spring Framework. Database created with PostgreSQL and
Liquibase migrations. Lombok used to reduce the amount of boilerplate code.
Model mapper used to transform objects for their further transportation.
Json Web Token together with Spring Security ensures the security of 
requests coming to the server. Junit, H2 database and Http client used for 
unit and integration tests. Springfox helps provide API documentation 
along with Swagger2. The project is built on Maven, see `pom.xml`.

## How to run

To run the application, you need a working PostgreSQL database server on 
a local port `http://localhost:5432`. You can set it up in the file 
`resources/application.yaml`. There you can also configure credentials 
and profile (set value `localhost` for property `profiles.active`).
You can run the application from the main class `JavaCodeSetApplication`
or build jar file with Maven.

## API documentation

The API documentation was made using Swagger2 and is available after 
running the application at `http://localhost:8090/swagger-ui/`. It 
contains information about available rest controller endpoints and models.

## Additional Information

Custom database queries in JPA repositories are written in JPQL. Migration 
files are in XML format. Spring sessions in the application are replaced 
by the period of validity of the JWT. Exceptions in the application are 
caught by two classes: `ApplicationExceptionHandler` (for response errors)
and `RestAuthenticationEntryPoint` (for authentication errors).




