package edu.jhu.epioneers.clueless.viewmodel;

import edu.jhu.epioneers.clueless.communication.RequestHandler;
import edu.jhu.epioneers.clueless.model.BoardState;

/**
 * Represents the game board and corresponding logic
 */
public class BoardViewModel extends ViewModelBase {
    /**
     * Indicates if the user can start the game
     */
    private boolean canStartGame;

    /**
     * Indicates if the user can move
     */
    private boolean canMove;

    /**
     * Indicates if the user can make a suggestion
     */
    private boolean canMakeSuggestion;

    /**
     * Indicates the current state of the game board
     */
    private BoardState boardState;

    public BoardViewModel(RequestHandler requestHandler) {
        super(requestHandler);
    }

    @Override
    protected void Sync() {

    }

    /**
     * Starts the game
     */
    public void startGame() {

    }

    /**
     * Begins the movement flow
     */
    public void beginMove() {

    }

    /**
     * Sends the move request to the server
     * @param x Postion X
     * @param y Postion Y
     */
    public void moveToPosition(int x, int y) {

    }

    /**
     * Sends the secret passage request to the server
     */
    public void takeSecretPassage() {

    }

    /**
     * Begins the suggestion flow
     */
    public void beginSuggestion() {

    }

    /**
     * Sends the end turn request to the server
     */
    public void endTurn() {

    }

    /**
     * Returns to the BaseTurn state
     */
    public void cancelAction() {

    }

    /**
     * Sends the suggestion request to the server.  The room id is implicit based on location
     * @param weaponId The id of the weapon being suggested
     * @param characterId The id of the character being suggested
     */
    public void submitSuggestion(int weaponId, int characterId) {

    }

    /**
     * Sends the disprove suggestion request to the server.  Only one of the id fields will contain a value.
     * @param weaponId The id of the weapon being disproven
     * @param characterId The id of the chracter being disproven
     * @param roomId The id of the room being disproven
     */
    public void disproveSuggestion(Integer weaponId, Integer characterId, Integer roomId) {

    }

    /**
     * Sends the make accusation request to the server.
     */
    public void makeAccusation() {

    }
}
