package edu.jhu.epioneers.clueless.viewmodel;

import edu.jhu.epioneers.clueless.model.GameSummaryModel;
import edu.jhu.epioneers.clueless.model.GameState;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by Phillip on 10/31/2015.
 */
public class LobbyViewModel {
    private ObservableList<GameSummaryModel> _games;
    private SimpleBooleanProperty _joinGameDisabled = new SimpleBooleanProperty(true);

    public ChangeListener<GameSummaryModel> onSelectedGameChanged = new ChangeListener<GameSummaryModel>() {
        @Override
        public void changed(ObservableValue<? extends GameSummaryModel> observable, GameSummaryModel oldValue, GameSummaryModel newValue) {
            _joinGameDisabled.setValue(newValue == null || !newValue.canJoin());
        }
    };

    public LobbyViewModel() {
        SyncGameData();
    }

    public ObservableList<GameSummaryModel> get_games() {
        return _games;
    }

    public ObservableValue<Boolean> joinGameDisabled() {
        return _joinGameDisabled;
    }

    public void SyncGameData() {
        //TEMP DATA
        GameSummaryModel game1 = new GameSummaryModel();

        game1.set_currentPlayers(6);
        game1.set_gameName("Game 1");
        game1.set_gameState(GameState.IN_PROCESS);

        GameSummaryModel game2 = new GameSummaryModel();

        game2.set_currentPlayers(0);
        game2.set_gameState(GameState.WAITING_FOR_PLAYERS);
        game2.set_gameName("Game 2");

        _games = FXCollections.observableArrayList();
        _games.add(game1);
        _games.add(game2);
        //END TEMP DATA
    }
}