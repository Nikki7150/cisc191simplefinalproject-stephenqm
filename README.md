[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=23971349)
# Simple REST JPA Game Server

This is the simplified replacement for the JavaFX + gRPC game client/server example.
It uses one Spring Boot web application:

```text
Browser page
    ↓ fetch()
REST Controller
    ↓
GameService
    ↓
Spring Data JPA Repository
    ↓
H2 embedded database saved to ./data/game-db
```

## Run the app

```bash
mvn spring-boot:run
```

Open:

```text
http://localhost:9090
```

The browser page lets students create matches and view the leaderboard.

## REST endpoints

| Method | Path | Purpose |
|---|---|---|
| `POST` | `/api/matches` | Create and save a new match |
| `GET` | `/api/matches` | List saved matches |
| `GET` | `/api/matches/{id}` | Get one saved match |
| `DELETE` | `/api/matches/{id}` | Delete one match |
| `GET` | `/api/leaderboard` | Show ranked wins by player |

## Example POST request

```bash
curl -X POST http://localhost:8080/api/matches \
  -H "Content-Type: application/json" \
  -d '{"playerOneName":"Ada","playerTwoName":"Grace","ranked":true}'
```

## H2 database

The database is file-backed and saved under:

```text
./data/game-db.mv.db
```

The `data/` folder is intentionally ignored by Git so students do not commit local database files.

H2 console:

```text
http://localhost:9090/h2-console
```

Use these settings:

| Field | Value |
|---|---|
| JDBC URL | `jdbc:h2:file:./data/game-db` |
| User Name | `sa` |
| Password | leave blank |

## Project structure

```text
src/main/java/edu/sdccd/cisc191/game
├── GameApplication.java
├── controller
│   ├── ApiExceptionHandler.java
│   └── GameController.java
├── dto
│   ├── CreateMatchRequest.java
│   ├── GameMatchResponse.java
│   └── LeaderboardEntry.java
├── model
│   └── GameMatch.java
├── repository
│   └── GameMatchRepository.java
└── service
    └── GameService.java
```

## Example additions

1. Add validation so blank player names return a `400 Bad Request`.
2. Add a `gameMode` field such as `DUEL`, `ARENA`, or `TOURNAMENT`.
3. Add a repository method that finds matches by winner name.
4. Add a REST endpoint for `/api/matches/winner/{name}`.
5. Add tests for the new endpoint.
6. Update the browser page to display the new field.

## Run tests

```bash
mvn test
```

Included tests:

| Test class | What it checks |
|---|---|
| `GameServiceTest` | Service-layer game logic and persistence behavior |
| `GameControllerTest` | REST endpoint responses |
| `WebInterfaceTest` | The browser page loads and points to the REST API |

Project Summary:
EpicGameLeaderboard is a match tracking and leaderboard application that is made for friend groups and gamers that want to keep scores across many sessions of play. Users are able to log matches between two players, have scores generated automatically and instantly see a ranked leaderboard showing who has the most wins overall. The problem it solves is that many friend groups have no way to track competitive results over a long period of time, so wins are forgotten. EpicGameLeaderboard Stores every match in a persisted database so the leaderboard keeps growing the more you play. It runs as a SpringBoot server with both a browser interface and a JavaFX desktop client, making it accessible from multiple platforms.


Module Topic Locations
Module 1: Arrays + OO Refresh

What: A 2D array int[][] scoreHistory stores the scores of the last 10 matches. Each row holds [playerOneScore, playerTwoScore]. A method iterates the array in order to find the highest score
Code: src/main/java/edu/sdccd/cisc191/game/service/GameService.java lines 26-83
Test: src/test/java/edu/sdccd/cisc191/game/ModuleIntegrationTest.java lines 28-41

Module 2: OO Design + Functional Interfaces

What: A Predicate<String> named isValidName validates player names before a match is created. A custom @FunctionalInterface called BackgroundTask is used in the JavaFX controller to abstract background HTTP work.
Code: src/main/java/edu/sdccd/cisc191/game/service/GameService.java, lines 29-30; 
Test: src/test/java/edu/sdccd/cisc191/game/ModuleIntegrationTest.java lines 43-56

Module 3: Inheritance + Polymorphism

Module 4: Exceptions + File I/O + Database Persistence

What: InvalidMatchException is a custom unchecked exception thrown when a player name is blank or null. Match data is persisted to a file-backed H2 database via Spring Data JPA.
Code: src/main/java/edu/sdccd/cisc191/game/service/GameService.java lines 39-44
Test: src/test/java/edu/sdccd/cisc191/game/ModuleIntegrationTest.java lines 68-82

Module 5: Recursion + Algorithms

What: findTopWinner() recursive scans a list of leaderboard entries to find the player with the most wins, base case returns the last entry. Recursive case compares the current entry to the best of the rest
Code: src/main/java/edu/sdccd/cisc191/game/service/GameService.java lines 114-123
Test: src/test/java/edu/sdccd/cisc191/game/ModuleIntegrationTest.java lines 84-101

Module 6: Collections + Generics + Advanced Streams

What: getRankedLeaderboard() uses Collectors.groupingBy() and Collectors.counting() to group matches by winner and count wins. Results are returned as a sorted List<LeaderboardEntry> using stream chaining
Code: src/main/java/edu/sdccd/cisc191/game/service/GameService.java lines 103-112
Test: src/test/java/edu/sdccd/cisc191/game/ModuleIntegrationTest.java lines 103-121

Module 7: JavaFX + Events + Lambdas
