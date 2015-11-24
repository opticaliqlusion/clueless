package edu.jhu.epioneers.clueless.view;

import edu.jhu.epioneers.clueless.communication.RequestHandler;
import edu.jhu.epioneers.clueless.viewmodel.BoardViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Used to bind data from the BoardViewModel to the FXML layout
 */
public class BoardView extends ViewBase<BoardViewModel> {

    @FXML
    Label lblStatus;

    @FXML
    Button btnStartGame;
    @Override
    protected BoardViewModel createModel() {
        return new BoardViewModel(new RequestHandler());
    }

    @Override
    protected void initialize() {
        lblStatus.textProperty().bind(getModel().statusTextProperty());
        btnStartGame.visibleProperty().bind(getModel().canStartGameProperty());

        /* TODO Sync will be something like this but not completely figured out
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                getModel().Sync();
            }
        }, 10, 10, TimeUnit.SECONDS);
        */

        btnStartGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getModel().startGame();
            }
        });
    }
}