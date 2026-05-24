package edu.sdccd.cisc191.game.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// MODULE 7: JavaFX controller — events, background threads, lambdas
public class FxGameController {

    private static final String BASE = "http://localhost:9090";
    private final HttpClient http = HttpClient.newHttpClient();

    @FXML private TextField playerOneField;
    @FXML private TextField playerTwoField;
    @FXML private CheckBox rankedBox;
    @FXML private Label statusLabel;
    @FXML private ListView<String> matchList;
    @FXML private ListView<String> leaderboardList;
    @FXML private TextField matchIdField;
    // MODULE 7: Called automatically when the FXML loads
    @FXML
    public void initialize() {
        refreshMatches();
        refreshLeaderboard();
    }

    // MODULE 7: Button event — runs HTTP POST on a background thread
    @FXML
    private void createMatch() {
        String json = String.format(
                "{\"playerOneName\":\"%s\",\"playerTwoName\":\"%s\",\"ranked\":%b}",
                playerOneField.getText(), playerTwoField.getText(), rankedBox.isSelected()
        );

        runInBackground(() -> post("/api/matches", json), result -> {
            statusLabel.setText("Match created!");
            refreshMatches();
            refreshLeaderboard();
        });
    }

    // MODULE 7: Button event — loads matches on background thread
    @FXML
    private void refreshMatches() {
        runInBackground(() -> get("/api/matches"), result -> {
            ObservableList<String> items = FXCollections.observableArrayList();
            for (String entry : result.replace("[{","").replace("}]","").split("},\\{")) {
                if (!entry.isBlank()) {
                    items.add(field(entry,"playerOneName") + " vs " +
                            field(entry,"playerTwoName") + " → " +
                            field(entry,"winnerName") + " wins (" +
                            field(entry,"playerOneScore") + "-" +
                            field(entry,"playerTwoScore") + ")");
                }
            }
            matchList.setItems(items);
        });
    }

    // MODULE 7: Button event — loads leaderboard on background thread
    @FXML
    private void refreshLeaderboard() {
        runInBackground(() -> get("/api/leaderboard"), result -> {
            ObservableList<String> items = FXCollections.observableArrayList();
            for (String entry : result.replace("[{","").replace("}]","").split("},\\{")) {
                if (!entry.isBlank()) {
                    items.add(field(entry,"playerName") + " — " + field(entry,"wins") + " wins");
                }
            }
            leaderboardList.setItems(items);
        });
    }

    // MODULE 7: Runs work on background thread, updates UI safely with Platform.runLater
    private void runInBackground(BackgroundTask work, java.util.function.Consumer<String> onDone) {
        Task<String> task = new Task<>() {
            @Override protected String call() throws Exception { return work.run(); }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> onDone.accept(task.getValue())));
        task.setOnFailed(e -> Platform.runLater(() ->
                statusLabel.setText("Error: " + task.getException().getMessage())
        ));
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private String get(String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(BASE + path)).GET().build();
        return http.send(req, HttpResponse.BodyHandlers.ofString()).body();
    }

    private String post(String path, String json) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json)).build();
        return http.send(req, HttpResponse.BodyHandlers.ofString()).body();
    }

    private String field(String json, String key) {
        String search = "\"" + key + "\":";
        int i = json.indexOf(search);
        if (i == -1) return "";
        i += search.length();
        if (json.charAt(i) == '"') {
            int end = json.indexOf('"', i + 1);
            return json.substring(i + 1, end);
        }
        int end = json.indexOf(',', i);
        if (end == -1) end = json.length();
        return json.substring(i, end).replace("}", "").trim();
    }
    // MODULE 7: Button event — get one match by ID
    @FXML
    private void getMatchById() {
        String id = matchIdField.getText().trim();
        if (id.isBlank()) { statusLabel.setText("Enter a match ID"); return; }
        runInBackground(() -> get("/api/matches/" + id), result -> {
            statusLabel.setText("Match: " + result);
        });
    }

    // MODULE 7: Button event — delete a match by ID
    @FXML
    private void deleteMatch() {
        String id = matchIdField.getText().trim();
        if (id.isBlank()) { statusLabel.setText("Enter a match ID"); return; }
        runInBackground(() -> delete("/api/matches/" + id), result -> {
            statusLabel.setText("Deleted match " + id);
            refreshMatches();
        });
    }

    private String delete(String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + path))
                .DELETE().build();
        return http.send(req, HttpResponse.BodyHandlers.ofString()).body();
    }

    // MODULE 2: Custom functional interface for background work
    @FunctionalInterface
    interface BackgroundTask {
        String run() throws Exception;
    }
}