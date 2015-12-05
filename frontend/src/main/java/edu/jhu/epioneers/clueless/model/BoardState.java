package edu.jhu.epioneers.clueless.model;

/**
 * Created by Phillip on 11/4/2015.
 */
public enum BoardState {
    /**
     * When the game is formed but there are not enough players to start the game
     */
    WaitingForPlayers(0),
    /**
     * When the game is formed, it has enough players to start, but it has not yet started
     */
    ReadyToStart(1),
    /**
     * Base state for the user's turn
     */
    BaseTurn(2),
    /**
     * When the user has started the movement flow but hasn't yet picked a movement location
     */
    StartMove(3),
    /**
     * When the user has started the suggestion flow but hasn't yet picked a movement location
     */
    StartSuggestion(4),
    /**
     * When the user is in process of disproving a suggestion
     */
    DisproveSuggestion(5),
    /**
     * When no one has disproven a suggestion and the user can make an accusation
     */
    MakeAccusation(6),
    /**
     * When the game is ended
     */
    GameOver(6),
    /***
     * Waiting for your turn
     */
    WaitingForTurn(7),

    /**
     * Waiting for a disprover of your suggestion
     */
    WaitingForDisprover(8);

    private int value;

    private BoardState(int val){
        value = val;
    }

    public int getValue(){
        return value;
    }
}
