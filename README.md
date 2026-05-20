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
