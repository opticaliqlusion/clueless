package edu.jhu.epioneers.clueless.model;

/**
 * Created by Phillip on 10/31/2015.
 */
public enum GameState {
    WAITING_FOR_PLAYERS(0),
    IN_PROCESS(1),
    COMPLETE(1);

    private int value;

    private GameState(int val){
        value = val;
    }

    public int getValue(){
        return value;
    }
}
