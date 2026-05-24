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

    @FXML
    private TextField playerOneField;
    @FXML
    private TextField playerTwoField;
    @FXML
    private CheckBox rankedBox;
    @FXML
    private Label statusLabel;
    @FXML
    private ListView<String> matchList;
    @FXML
    private ListView<String> leaderboardList;

}
