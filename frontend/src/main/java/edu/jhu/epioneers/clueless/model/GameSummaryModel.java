package edu.jhu.epioneers.clueless.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

/***
 * Contains summary information for games in the lobby view
 */
public class GameSummaryModel extends ModelBase {

    @Override
    public boolean equals(Object obj) {
        if(obj==null || getClass() != obj.getClass()) {
            return false;
        }

        GameSummaryModel castObject = (GameSummaryModel) obj;

        return castObject.getId() == getId()
                && castObject.getName().equals(getName())
                && castObject.getCurrentPlayers() == getCurrentPlayers();
    }

    private final int maximumPlayers = 6;

    private GameState gameState;
    private ArrayList<Integer> inUseCharacters;
    private StringProperty gameStatus = new SimpleStringProperty("");

    private int getCurrentPlayers() {
        return inUseCharacters.size();
    }

    public final StringProperty gameStatusProperty() {
        return gameStatus;
    }

    public boolean canJoin() {
        return getCurrentPlayers() != maximumPlayers;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public ArrayList<Integer> getInUseCharacters() {
        return inUseCharacters;
    }

    public void setInUseCharacters(ArrayList<Integer> inUseCharacters) {
        this.inUseCharacters = inUseCharacters;

        if(gameState ==GameState.IN_PROCESS) {
            gameStatus.setValue("IN PROCESS");
        } else {
            gameStatus.setValue(getName()+" ("+getCurrentPlayers() +"/"+ maximumPlayers +" players)");
        }
    }
}