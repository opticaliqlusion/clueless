package edu.jhu.epioneers.clueless.communication;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The response of the get_board_state service operation
 */
public class GetBoardStateResponse {
    /***
     * Maps player ids to rooms
     */
    private HashMap<Integer,Integer> playerGameIdMap;

    /**
     * Id of the player for which the current turn is associated
     */
    private int idCurrentTurn;

    /***
     * Current state of the game
     */
    private int gameState;

    /***
     * A list of logs to output to the console
     */
    private ArrayList<String> logs;

    /***
     * Id of the last log item received
     */
    private int lastLogId;

    /***
     * Id of the current player
     */
    private int playerId;

    /***
     * The list of cards for the current player
     */
    private ArrayList<Integer> cardIds;

    public HashMap<Integer, Integer> getPlayerGameIdMap() {
        return playerGameIdMap;
    }

    public void setPlayerGameIdMap(HashMap<Integer, Integer> playerGameIdMap) {
        this.playerGameIdMap = playerGameIdMap;
    }

    public int getIdCurrentTurn() {
        return idCurrentTurn;
    }

    public void setIdCurrentTurn(int idCurrentTurn) {
        this.idCurrentTurn = idCurrentTurn;
    }

    public int getGameState() {
        return gameState;
    }

    public void setGameState(int gameState) {
        this.gameState = gameState;
    }

    public ArrayList<String> getLogs() {
        return logs;
    }

    public void setLogs(ArrayList<String> logs) {
        this.logs = logs;
    }

    public int getLastLogId() {
        return lastLogId;
    }

    public void setLastLogId(int lastLogId) {
        this.lastLogId = lastLogId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public ArrayList<Integer> getCardIds() {
        return cardIds;
    }

    public void setCardIds(ArrayList<Integer> cardIds) {
        this.cardIds = cardIds;
    }
}