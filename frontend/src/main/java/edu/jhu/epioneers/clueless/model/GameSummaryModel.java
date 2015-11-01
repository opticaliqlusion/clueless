package edu.jhu.epioneers.clueless.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Phillip on 10/31/2015.
 */
public class GameSummaryModel {
    private final int _maximumPlayers = 6;

    private String _gameName;
    private int _currentPlayers;
    private int _id;
    private GameState _gameState;

    public void set_gameName(String value)
    {
        _gameName=value;
    }

    public final StringProperty gameNameProperty() { return new SimpleStringProperty(_gameName); }

    public final StringProperty gameStatusProperty() {
        StringProperty prop = new SimpleStringProperty();

        if(_gameState ==GameState.IN_PROCESS) {
            prop.setValue("IN PROCESS");
        } else {
            prop.setValue(_currentPlayers +"/"+ _maximumPlayers);
        }

        return prop;
    }

    public boolean canJoin() {
        return _gameState ==GameState.WAITING_FOR_PLAYERS
                && _currentPlayers != _maximumPlayers;
    }

    public void set_currentPlayers(int _currentPlayers) {
        this._currentPlayers = _currentPlayers;
    }

    public void set_gameState(GameState _gameState) {
        this._gameState = _gameState;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }
}