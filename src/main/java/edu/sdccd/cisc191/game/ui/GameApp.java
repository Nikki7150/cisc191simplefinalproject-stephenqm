package edu.sdccd.cisc191.game.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

// MODULE 7: JavaFX entry point — run this after starting the Spring Boot server
public class GameApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game-view.fxml"));
        Parent root = loader.load();
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
 