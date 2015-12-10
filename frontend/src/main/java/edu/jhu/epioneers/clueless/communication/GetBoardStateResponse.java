package edu.jhu.epioneers.clueless.communication;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The response of the get_board_state service operation
 */
public class GetBoardStateResponse {
    private int idGame;
    private boolean wasMoved;

    private Integer winner;

    private ArrayList<Integer> losers;

    /***
     * Maps player ids to rooms
     */
    private HashMap<Integer,Integer> playerGameIdMap;


    private HashMap<Integer,Integer> characterMap;

    /**
     * Id of the player for which the current turn is associated
     */
    private int idCurrentTurn;

    /**
     * Id of current player in disprover flow
     */
    private int idCurrentDisprover;

    /***
     * Current state of the game
     */
    private int gameState;

    private int turnState;

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
    private int idPlayer;

    private ArrayList<Integer> currentSuggestion;

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

    public int getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(int idPlayer) {
        this.idPlayer = idPlayer;
    }

    public ArrayList<Integer> getCardIds() {
        return cardIds;
    }

    public void setCardIds(ArrayList<Integer> cardIds) {
        this.cardIds = cardIds;
    }

    public int getIdGame() {
        return idGame;
    }

    public void setIdGame(int idGame) {
        this.idGame = idGame;
    }

    public int getTurnState() {
        return turnState;
    }

    public void setTurnState(int turnState) {
        this.turnState = turnState;
    }

    public int getIdCurrentDisprover() {
        return idCurrentDisprover;
    }

    public void setIdCurrentDisprover(int idCurrentDisprover) {
        this.idCurrentDisprover = idCurrentDisprover;
    }

    public ArrayList<Integer> getCurrentSuggestion() {
        return currentSuggestion;
    }

    public void setCurrentSuggestion(ArrayList<Integer> currentSuggestion) {
        this.currentSuggestion = currentSuggestion;
    }

    public HashMap<Integer, Integer> getCharacterMap() {
        return characterMap;
    }

    public void setCharacterMap(HashMap<Integer, Integer> characterMap) {
        this.characterMap = characterMap;
    }

    public Integer getWinner() {
        return winner;
    }

    public void setWinner(Integer winner) {
        this.winner = winner;
    }

    public ArrayList<Integer> getLosers() {
        return losers;
    }

    public void setLosers(ArrayList<Integer> losers) {
        this.losers = losers;
    }

    public boolean getWasMoved() {
        return wasMoved;
    }

    public void setWasMoved(boolean wasMoved) {
        this.wasMoved = wasMoved;
    }
}