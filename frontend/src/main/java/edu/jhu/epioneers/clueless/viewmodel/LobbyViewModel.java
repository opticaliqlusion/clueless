package edu.jhu.epioneers.clueless.viewmodel;

import com.google.gson.reflect.TypeToken;
import edu.jhu.epioneers.clueless.Constants;
import edu.jhu.epioneers.clueless.communication.GetPendingGamesReponse;
import edu.jhu.epioneers.clueless.communication.RequestHandler;
import edu.jhu.epioneers.clueless.communication.Response;
import edu.jhu.epioneers.clueless.model.GameSummaryModel;
import edu.jhu.epioneers.clueless.model.GameState;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

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
        _games = FXCollections.observableArrayList();

        Response<GetPendingGamesReponse> games = requestHandler.makeGETRequest(Constants.GET_PENDING_GAMES_PATH,
                new TypeToken<Response<GetPendingGamesReponse>>(){}.getType());

        if(games.getHttpStatusCode()==games.HTTP_OK) {
            GetPendingGamesReponse gameData = games.getData();

            for(Map.Entry<Integer,ArrayList<Integer>> game : gameData.getGames().entrySet()) {
                GameSummaryModel gameSummaryModel = new GameSummaryModel();
                gameSummaryModel.setId(game.getKey());
                gameSummaryModel.setName("Game "+game.getKey());
                gameSummaryModel.setInUseCharacters(game.getValue());
            }
        } else {
            //TODO Trigger error scenario
        }
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
        getContext().setSelectedGame(null);
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