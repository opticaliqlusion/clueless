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
                && castObject.getName() == getName()
                && castObject.getCurrentPlayers() == getCurrentPlayers();
    }

    private final int maximumPlayers = 6;

    private GameState gameState;
    private ArrayList<Integer> inUseCharacters;

    private int getCurrentPlayers() {
        return inUseCharacters.size();
    }

    public final StringProperty gameStatusProperty() {
        StringProperty prop = new SimpleStringProperty();

        if(gameState ==GameState.IN_PROCESS) {
            prop.setValue("IN PROCESS");
        } else {
            prop.setValue(getCurrentPlayers() +"/"+ maximumPlayers);
        }

        return prop;
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
    }
}