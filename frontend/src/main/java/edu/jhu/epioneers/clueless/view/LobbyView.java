package edu.jhu.epioneers.clueless.view;

import edu.jhu.epioneers.clueless.communication.RequestHandler;
import edu.jhu.epioneers.clueless.model.GameSummaryModel;
import edu.jhu.epioneers.clueless.viewmodel.LobbyViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * Used to bind data from the LobbyViewModel to the FXML layout
 */
public class LobbyView extends ViewBase<LobbyViewModel> {

    @FXML
    public TableView<GameSummaryModel> tableView;

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
        tableView.setItems(model.get_games());

        btnJoinGame.disableProperty().bind(model.joinGameDisabled());


        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<GameSummaryModel>() {
            @Override
            public void changed(ObservableValue<? extends GameSummaryModel> observable, GameSummaryModel oldValue, GameSummaryModel newValue) {
                model.selectedGameChanged(newValue);
            }
        });

        btnNewGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                model.newGame((Stage)btnNewGame.getScene().getWindow());
            }
        });

        btnJoinGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                model.joinGame((Stage) btnNewGame.getScene().getWindow(), tableView.getSelectionModel().getSelectedItem());
            }
        });
    }
}