package edu.jhu.epioneers.clueless.model;

/**
 * GameState representation for the lobby view
 */
public enum GameState {
    /**
     * When a game is waiting for players and can be joined
     */
    WAITING_FOR_PLAYERS(0),
    /**
     * When a game is currently in process and cannot be joined
     */
    IN_PROCESS(1),
    /**
     * When a game is currently complete and cannot be joined
     */
    COMPLETE(2);

    private int value;

    private GameState(int val){
        value = val;
    }

    public int getValue(){
        return value;
    }
}
