package edu.jhu.epioneers.clueless.viewmodel;

import edu.jhu.epioneers.clueless.communication.IdNameType;
import edu.jhu.epioneers.clueless.model.GameSummaryModel;

import java.util.ArrayList;

/**
 * Singleton class that contains data to be synced between ViewModels
 */
public class ViewModelContext {
    private final static ViewModelContext instance = new ViewModelContext();

    public static ViewModelContext getInstance() {
        return instance;
    }

    /**
     * Currently selected game for the choose character screen
     */
    private GameSummaryModel selectedGame;

    public GameSummaryModel getSelectedGame() {
        return selectedGame;
    }

    public void setSelectedGame(GameSummaryModel selectedGame) {
        this.selectedGame = selectedGame;
    }

    private ArrayList<IdNameType> allCharacters;

    public ArrayList<IdNameType> getAllCharacters() {
        return allCharacters;
    }

    public void setAllCharacters(ArrayList<IdNameType> allCharacters) {
        this.allCharacters = allCharacters;
    }
}
