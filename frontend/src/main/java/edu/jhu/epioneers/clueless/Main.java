package edu.jhu.epioneers.clueless;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Entry point of the program.  Initializes FXML components and LobbyLayout.
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        URL resource = getClass().getResource(Constants.LobbyLayout);
        Parent root = FXMLLoader.load(resource);
        primaryStage.setTitle("ClueLess");
        primaryStage.setScene(new Scene(root, Constants.StageSizeX, Constants.StageSizeY));
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}