package edu.jhu.epioneers.clueless.view;

import edu.jhu.epioneers.clueless.Constants;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Used to bind data from the BoardViewModel to the FXML layout
 */
public class BoardView extends ViewBase<BoardViewModel> {

    @FXML
    Button btnEndTurn;

    @FXML
    Label lblStatus;

    @FXML
    Button btnStartGame;

    @FXML
    GridPane grdBoard;

    @FXML ImageView roomStudy;
    @FXML ImageView hallStudyHall;
    @FXML ImageView roomHall;
    @FXML ImageView hallHallLounge;
    @FXML ImageView roomLounge;
    @FXML ImageView hallStudyLibrary;
    @FXML ImageView hallHallBilliard;
    @FXML ImageView hallLoungeDining;
    @FXML ImageView roomLibrary;
    @FXML ImageView hallLibraryBilliard;
    @FXML ImageView roomBilliard;
    @FXML ImageView hallBilliardDining;
    @FXML ImageView roomDining;
    @FXML ImageView hallLibraryConservatory;
    @FXML ImageView hallBilliardBallroom;
    @FXML ImageView hallDiningKitchen;
    @FXML ImageView roomConservatory;
    @FXML ImageView hallConservatoryBallroom;
    @FXML ImageView roomBallroom;
    @FXML ImageView hallBallroomKitchen;
    @FXML ImageView roomKitchen;

    @Override
    protected BoardViewModel createModel() {
        return new BoardViewModel(new RequestHandler());
    }

    @Override
    protected void initialize() {
        lblStatus.textProperty().bind(getModel().statusTextProperty());
        btnStartGame.visibleProperty().bind(getModel().canStartGameProperty());

        btnStartGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getModel().startGame();
            }
        });

        btnEndTurn.visibleProperty().bind(getModel().canEndTurnProperty());
        btnEndTurn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getModel().endTurn();
            }
        });

        setImages();

        scheduleSync();
    }

    private void setImages() {

        Image roomImage = new Image(getClass().getResource("/images/room.jpg").toExternalForm());
        Image hallImage = new Image(getClass().getResource("/images/hall.jpg").toExternalForm());

        roomStudy.setImage(roomImage);
        hallStudyHall.setImage(hallImage);
        roomHall.setImage(roomImage);
        hallHallLounge.setImage(hallImage);
        roomLounge.setImage(roomImage);
        hallStudyLibrary.setImage(hallImage);
        hallHallBilliard.setImage(hallImage);
        hallLoungeDining.setImage(hallImage);
        roomLibrary.setImage(roomImage);
        hallLibraryBilliard.setImage(hallImage);
        roomBilliard.setImage(roomImage);
        hallBilliardDining.setImage(hallImage);
        roomDining.setImage(roomImage);
        hallLibraryConservatory.setImage(hallImage);
        hallBilliardBallroom.setImage(hallImage);
        hallDiningKitchen.setImage(hallImage);
        roomConservatory.setImage(roomImage);
        hallConservatoryBallroom.setImage(hallImage);
        roomBallroom.setImage(roomImage);
        hallBallroomKitchen.setImage(hallImage);
        roomKitchen.setImage(roomImage);
    }

    private void scheduleSync() {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

        ses.schedule(new Runnable() {
            @Override
            public void run() {
                getModel().Sync();

                if(!getModel().isModelDisposed()) {
                    scheduleSync();
                }
            }
        }, Constants.SyncDelay, TimeUnit.SECONDS);
    }
}