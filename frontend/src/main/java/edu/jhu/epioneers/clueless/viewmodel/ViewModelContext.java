package edu.jhu.epioneers.clueless.viewmodel;

import edu.jhu.epioneers.clueless.model.GameSummaryModel;

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
}
