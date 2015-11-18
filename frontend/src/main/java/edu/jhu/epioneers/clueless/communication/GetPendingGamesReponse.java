package edu.jhu.epioneers.clueless.communication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The response of the get_pending_games service operation
 */
public class GetPendingGamesReponse {
    /**
     * Maps game ids to a list of characters already in use
     */
    private HashMap<Integer,ArrayList<Integer>> games;

    public HashMap<Integer, ArrayList<Integer>> getGames() {
        return games;
    }

    public void setGames(HashMap<Integer, ArrayList<Integer>> games) {
        this.games = games;
    }
}
