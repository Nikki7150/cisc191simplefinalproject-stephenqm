package edu.sdccd.cisc191.game.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import java.net.URL;


// MODULE 7: JavaFX entry point — run this after starting the Spring Boot server
public class GameApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        URL resource = getClass().getResource("/fxml/game-view.fxml");
        if (resource == null) throw new RuntimeException("Cannot find game-view.fxml — check it is in src/main/resources/fxml/");
        FXMLLoader loader = new FXMLLoader(resource);        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Game Server");
        stage.setScene(scene);

        stage.setOnCloseRequest(e -> {
            System.out.println("Closing Game Server");
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
