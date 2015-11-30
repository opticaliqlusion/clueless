package edu.jhu.epioneers.clueless.view;

import edu.jhu.epioneers.clueless.Constants;
import edu.jhu.epioneers.clueless.communication.RequestHandler;
import edu.jhu.epioneers.clueless.model.RoomModel;
import edu.jhu.epioneers.clueless.viewmodel.BoardViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import javafx.scene.input.MouseEvent;
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

    @FXML
    Button btnBeginMove;

    @FXML
    Button btnCancelAction;

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

        btnBeginMove.visibleProperty().bind(getModel().canMoveProperty());

        btnBeginMove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getModel().beginMove();
            }
        });

        btnCancelAction.visibleProperty().bind(getModel().canCancelProperty());

        createBoard();
        scheduleSync();
    }

    private void createBoard() {
        for (RoomModel room : getModel().getAllRooms()) {
            ImageView roomImage = new ImageView();

            roomImage.setFitHeight(100);
            roomImage.setFitWidth(100);

            //TODO Only use each resource once
            roomImage.setImage(new Image(getClass().getResource(room.getImageResourceName()).toExternalForm()));
            grdBoard.add(roomImage, room.getY(), room.getX());
            Label textOverlay = new Label();
            textOverlay.textProperty().bind(room.textOverlayProperty());
            grdBoard.add(textOverlay, room.getY(), room.getX());

            EventHandler<MouseEvent> moveHandler = new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    getModel().moveToRoom(room.getId());
                }
            };

            roomImage.addEventHandler(MouseEvent.MOUSE_CLICKED, moveHandler);
            textOverlay.addEventHandler(MouseEvent.MOUSE_CLICKED, moveHandler);
        }
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