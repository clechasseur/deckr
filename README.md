# deckr
Simple card game wrapped in a REST API

This is the solution I propose to your challenge. It is a web application written in Java using Spring Boot that implements a REST API allowing to deal with a basic card game. No operation besides those that were required have been implemented. The interface tries to be as RESTful as possible, using basic HTTP methods to avoid using operation names in URLs and including links in API responses to facilitate hyperlinking.

## Tests

The project uses Gradle for build scripts, so you can run the tests simply by running

```shell
./gradlew test
```

There are tests for the service and controller layers. Most tests use mocks to only test the appropriate parts. There is one basic integration test that uses many game operations.

The tests are also run when a commit is pushed in GitHub via GitHub actions.

## Running the server

To run the server locally:

```shell
./gradle bootRun
```

This will start the API stack using an in-memory database (H2). Although data is not persisted between runs, all API operations are functional.

## Using PostgreSQL

The project also includes optional support for using a PostgreSQL database to store data. This will make data persist between runs. To use this support, you need a Postgres database running somewhere and use:

```shell
export SPRING_DATASOURCE_URL=jdbc:postgresql://your_postgresql_host/your_database_name
export SPRING_DATASOURCE_USERNAME=your_postgresql_user
export SPRING_DATASOURCE_PASSWORD=your_postgresql_password
export SPRING_PROFILES_ACTIVE=postgres
./gradlew bootRun
```

If your Postgres database is running locally, you can avoid specifying some of those properties. You can look at the default values in

`src/main/resources/application-postgres.properties`

## Postman collection

To facilitate the use of the API operations, a sample Postman collection containing examples of each operation has been provided in

`postman/Deckr.postman_collection.json`

# Future considerations

If this was a real project, here are some considerations for the project's future.

## Security

Currently, the API is unsecured. Not only can anyone call any operation, but data is not filtered from the output (so for example, players see the content of the game's shoe when querying their state). If this was made into a real game, a security layer would need to be added. Most likely, various roles could be defined - dealer (access to the game's shoe, shuffling, dealing, etc.) and player (query hand state, perhaps draw cards from shoe).

In this project, we could use Spring Security to implement such a security layer.

## Concurrency

Currently, the API uses database transactions for multi-query operations. However, this might not be sufficient if the server was to be deployed on multiple instances/in multiple containers. A locking system would probably be required to avoid concurrent modifications of the game's state (for instance, shuffling while drawing cards, drawing by multiple players simultaneously). Such locking would need to be done in the database to be applicable to multiple servers.

## Game operations

Currently, the API does not really allow any game to be played - the basic operations are a good backbone, but more are needed to make an actual card game. There are two options here, I think: either implement the rules of a real card back, like Black Jack, in the system, or simply provide enough basic operations to allow users to implement their own card game on top of it.
