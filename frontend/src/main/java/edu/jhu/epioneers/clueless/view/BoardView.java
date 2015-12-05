package edu.jhu.epioneers.clueless.view;

import edu.jhu.epioneers.clueless.Constants;
import edu.jhu.epioneers.clueless.communication.RequestHandler;
import edu.jhu.epioneers.clueless.model.ModelBase;
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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

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

    @FXML
    GridPane grdSuggestion;

    @FXML
    GridPane grdTurn;

    @FXML
    ComboBox<ModelBase> comboWeapon;

    @FXML
    ComboBox<ModelBase> comboCharacter;

    @FXML
    Button btnMakeSuggestion;

    @FXML
    ComboBox<ModelBase> comboDisproveSelect;

    @FXML
    Button btnDisproveSuggestion;

    @FXML
    GridPane grdDisprove;

    @FXML
    GridPane grdStart;

    @Override
    protected BoardViewModel createModel() {
        return new BoardViewModel(new RequestHandler());
    }

    @Override
    protected void initialize() {
        lblStatus.textProperty().bind(getModel().statusTextProperty());

        grdStart.visibleProperty().bind(getModel().canStartGameProperty());

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

        grdSuggestion.visibleProperty().bind(getModel().canMakeSuggestionProperty());
        grdTurn.visibleProperty().bind(getModel().canMakeSuggestionProperty().not()
                .and(getModel().canDisproveSuggestionProperty().not())
                .and(getModel().canStartGameProperty().not()));

        comboWeapon.setItems(getModel().getWeaponCards());

        comboCharacter.setItems(getModel().getCharacterCards());

        Callback<ListView<ModelBase>, ListCell<ModelBase>> modelBaseComboFactory = new Callback<ListView<ModelBase>, ListCell<ModelBase>>() {

            @Override
            public ListCell call(ListView param) {
                final ListCell<ModelBase> cell = new ListCell<ModelBase>(){

                    @Override
                    protected void updateItem(ModelBase t, boolean bln) {
                        super.updateItem(t, bln);

                        if(t != null){
                            setText(t.getName());
                        }else{
                            setText(null);
                        }
                    }

                };

                return cell;
            }
        };

        comboWeapon.setCellFactory(modelBaseComboFactory);
        comboCharacter.setCellFactory(modelBaseComboFactory);

        comboWeapon.getSelectionModel().selectFirst();
        comboCharacter.getSelectionModel().selectFirst();

        btnMakeSuggestion.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getModel().submitSuggestion((comboWeapon.getSelectionModel().getSelectedItem()).getId(),
                        (comboCharacter.getSelectionModel().getSelectedItem()).getId());
            }
        });

        comboDisproveSelect.setCellFactory(modelBaseComboFactory);

        comboDisproveSelect.setItems(getModel().getDisprovalCards());

        btnDisproveSuggestion.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getModel().disproveSuggestion((comboDisproveSelect.getSelectionModel().getSelectedItem()).getId());
            }
        });

        grdDisprove.visibleProperty().bind(getModel().canDisproveSuggestionProperty());

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