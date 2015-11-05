package edu.jhu.epioneers.clueless.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Phillip on 10/31/2015.
 */
public class GameSummaryModel extends ModelBase {
    private final int maximumPlayers = 6;

    private int currentPlayers;
    private GameState gameState;

    public final StringProperty gameStatusProperty() {
        StringProperty prop = new SimpleStringProperty();

        if(gameState ==GameState.IN_PROCESS) {
            prop.setValue("IN PROCESS");
        } else {
            prop.setValue(currentPlayers +"/"+ maximumPlayers);
        }

        return prop;
    }

    public boolean canJoin() {
        return gameState ==GameState.WAITING_FOR_PLAYERS
                && currentPlayers != maximumPlayers;
    }

    public void setCurrentPlayers(int currentPlayers) {
        this.currentPlayers = currentPlayers;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}