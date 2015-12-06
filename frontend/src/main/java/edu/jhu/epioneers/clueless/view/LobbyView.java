package edu.jhu.epioneers.clueless.view;

import edu.jhu.epioneers.clueless.Constants;
import edu.jhu.epioneers.clueless.communication.RequestHandler;
import edu.jhu.epioneers.clueless.model.GameSummaryModel;
import edu.jhu.epioneers.clueless.model.ModelBase;
import edu.jhu.epioneers.clueless.viewmodel.LobbyViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Used to bind data from the LobbyViewModel to the FXML layout
 */
public class LobbyView extends ViewBase<LobbyViewModel> {

    @FXML
    public ListView<GameSummaryModel> lvGames;

    @FXML
    public Button btnJoinGame;

    @FXML
    public Button btnNewGame;

    @Override
    protected LobbyViewModel createModel() {
        return new LobbyViewModel(new RequestHandler());
    }

    @Override
    protected void initialize() {
        final LobbyViewModel model = getModel();
        lvGames.setItems(model.get_games());

        btnJoinGame.disableProperty().bind(model.joinGameDisabled());

        lvGames.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<GameSummaryModel>() {
            @Override
            public void changed(ObservableValue<? extends GameSummaryModel> observable, GameSummaryModel oldValue, GameSummaryModel newValue) {
                model.selectedGameChanged(newValue);
            }
        });

        lvGames.setCellFactory(ChoiceBoxListCell.forListView(new StringConverter<GameSummaryModel>() {
            @Override
            public String toString(GameSummaryModel object) {
                return object.getName()+" ("+object.gameStatusProperty().getValue() +" players)";
            }

            @Override
            public GameSummaryModel fromString(String string) {
                return null;
            }
        }));

        btnNewGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                model.newGame((Stage)btnNewGame.getScene().getWindow());
            }
        });

        btnJoinGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                model.joinGame((Stage) btnNewGame.getScene().getWindow(), lvGames.getSelectionModel().getSelectedItem());
            }
        });

        scheduleSync();
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