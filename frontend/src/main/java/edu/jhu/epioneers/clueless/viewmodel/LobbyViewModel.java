package edu.jhu.epioneers.clueless.viewmodel;

import edu.jhu.epioneers.clueless.Constants;
import edu.jhu.epioneers.clueless.communication.RequestHandler;
import edu.jhu.epioneers.clueless.model.GameSummaryModel;
import edu.jhu.epioneers.clueless.model.GameState;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

/**
 * ViewModel for the lobby screen
 */
public class LobbyViewModel extends ViewModelBase {
    /**
     * The current list of games in progress or waiting for players
     */
    private ObservableList<GameSummaryModel> _games;

    /**
     * Represents if the Join Game button is disabled
     */
    private SimpleBooleanProperty _joinGameDisabled = new SimpleBooleanProperty(true);

    public LobbyViewModel(RequestHandler requestHandler) {
        super(requestHandler);
    }

    @Override
    protected void Sync() {
        //TEMP DATA
        GameSummaryModel game1 = new GameSummaryModel();

        game1.setCurrentPlayers(6);
        game1.setName("Game 1");
        game1.setGameState(GameState.IN_PROCESS);

        GameSummaryModel game2 = new GameSummaryModel();

        game2.setCurrentPlayers(0);
        game2.setGameState(GameState.WAITING_FOR_PLAYERS);
        game2.setName("Game 2");

        _games = FXCollections.observableArrayList();
        _games.add(game1);
        _games.add(game2);
        //END TEMP DATA
    }

    public ObservableList<GameSummaryModel> get_games() {
        return _games;
    }

    public ObservableValue<Boolean> joinGameDisabled() {
        return _joinGameDisabled;
    }


    /**
     * Begins create game flow.
     * @param stage Current stage
     */
    public void newGame(Stage stage) {
        changeScene(stage, Constants.ChooseCharacterLayout);
    }

    public void selectedGameChanged(GameSummaryModel selectedModel) {
        _joinGameDisabled.setValue(selectedModel == null || !selectedModel.canJoin());
    }

    /**
     * Begins the join game flow.  The selected game is stored in the ViewModelContext.
     * @param stage Current stage
     * @param selectedItem Selected game
     */
    public void joinGame(Stage stage, GameSummaryModel selectedItem) {
        getContext().setSelectedGame(selectedItem);

        changeScene(stage, Constants.ChooseCharacterLayout);
    }
}