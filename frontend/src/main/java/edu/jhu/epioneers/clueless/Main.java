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

        primaryStage.setWidth(Constants.StageSizeX);
        primaryStage.setHeight(Constants.StageSizeY);
        primaryStage.setResizable(false);

        URL resource = getClass().getResource(Constants.LobbyLayout);
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root, Constants.StageSizeX, Constants.StageSizeY);

        String css = this.getClass().getResource(Constants.STYLESHEET).toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.setTitle("ClueLess");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}