package edu.jhu.epioneers.clueless.view;

import edu.jhu.epioneers.clueless.Constants;
import edu.jhu.epioneers.clueless.model.GameSummaryModel;
import edu.jhu.epioneers.clueless.viewmodel.LobbyViewModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Phillip on 10/31/2015.
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
        return new LobbyViewModel();
    }

    @Override
    protected void initialize() {
        tableView.setItems(model.get_games());

        btnJoinGame.disableProperty().bind(model.joinGameDisabled());
        tableView.getSelectionModel().selectedItemProperty().addListener(model.onSelectedGameChanged);

        btnNewGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //TODO Figure out error pattern
                try {
                    Stage stage = (Stage)btnNewGame.getScene().getWindow();
                    Parent root = FXMLLoader.load(getClass().getResource(Constants.ChooseCharacterLayout));
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}