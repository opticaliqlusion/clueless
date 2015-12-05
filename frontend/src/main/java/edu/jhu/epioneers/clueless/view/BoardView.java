package edu.jhu.epioneers.clueless.view;

import edu.jhu.epioneers.clueless.Constants;
import edu.jhu.epioneers.clueless.communication.RequestHandler;
import edu.jhu.epioneers.clueless.model.ModelBase;
import edu.jhu.epioneers.clueless.model.RoomModel;
import edu.jhu.epioneers.clueless.viewmodel.BoardViewModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
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
    Button btnBeginAccusation;

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
    Button btnMakeAccusation;

    @FXML
    ComboBox<ModelBase> comboDisproveSelect;

    @FXML
    Button btnDisproveSuggestion;

    @FXML
    GridPane grdDisprove;

    @FXML
    GridPane grdStart;

    @FXML
    ComboBox<ModelBase> comboRoom;

    @FXML
    Button btnCancelAccusation;

    @FXML
    TextArea txtLog;

    @FXML
    TextArea txtCards;

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
        btnBeginAccusation.visibleProperty().bind(getModel().canEndTurnProperty());

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

        EventHandler<ActionEvent> cancelAction = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getModel().cancelAction();
            }
        };

        btnCancelAction.visibleProperty().bind(getModel().canCancelProperty());
        btnCancelAccusation.visibleProperty().bind(getModel().isAccusationProperty());

        btnCancelAction.setOnAction(cancelAction);
        btnCancelAccusation.setOnAction(cancelAction);

        grdSuggestion.visibleProperty().bind(getModel().canMakeSuggestionProperty());
        grdTurn.visibleProperty().bind(getModel().isBaseTurnProperty());

        comboWeapon.setItems(getModel().getWeaponCards());

        comboCharacter.setItems(getModel().getCharacterCards());

        comboRoom.setItems(getModel().getRoomCards());

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
        comboRoom.setCellFactory(modelBaseComboFactory);

        comboWeapon.getSelectionModel().selectFirst();
        comboCharacter.getSelectionModel().selectFirst();
        comboRoom.getSelectionModel().selectFirst();

        comboRoom.visibleProperty().bind(getModel().isAccusationProperty());

        btnMakeSuggestion.visibleProperty().bind(getModel().isAccusationProperty().not());
        btnMakeAccusation.visibleProperty().bind(getModel().isAccusationProperty());

        btnMakeSuggestion.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getModel().submitSuggestion((comboWeapon.getSelectionModel().getSelectedItem()).getId(),
                        (comboCharacter.getSelectionModel().getSelectedItem()).getId());
            }
        });

        btnMakeAccusation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getModel().submitAccusation((comboWeapon.getSelectionModel().getSelectedItem()).getId(),
                        (comboCharacter.getSelectionModel().getSelectedItem()).getId(),
                        (comboRoom.getSelectionModel().getSelectedItem()).getId());
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

        btnBeginAccusation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getModel().beginAccusation();
            }
        });

        grdDisprove.visibleProperty().bind(getModel().canDisproveSuggestionProperty());

        txtLog.disableProperty().setValue(true);
        txtCards.disableProperty().setValue(true);

        txtLog.textProperty().bind(getModel().logTextProperty());
        txtCards.textProperty().bind(getModel().cardsTextProperty());


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
            textOverlay.setStyle("-fx-color: black; -fx-background-color: white;");
            GridPane.setMargin(textOverlay, new Insets(10, 0, 0, 0));
            GridPane.setHalignment(textOverlay, HPos.CENTER );
            GridPane.setValignment(textOverlay, VPos.TOP);

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