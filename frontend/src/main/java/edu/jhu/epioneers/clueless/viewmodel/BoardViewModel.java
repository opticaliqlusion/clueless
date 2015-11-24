package edu.jhu.epioneers.clueless.viewmodel;

import com.google.gson.reflect.TypeToken;
import edu.jhu.epioneers.clueless.Constants;
import edu.jhu.epioneers.clueless.communication.*;
import edu.jhu.epioneers.clueless.model.BoardState;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents the game board and corresponding logic
 */
public class BoardViewModel extends ViewModelBase {

    private BooleanProperty canStartGame;
    private StringProperty statusText;
    private BooleanProperty canMove;

    private ObservableList<Integer> availableMoves = FXCollections.observableArrayList();

    /**
     * Indicates if the user can start the game
     */
    public BooleanProperty canStartGameProperty() {
        return canStartGame;
    }

    /**
     * Indicates if the user can move
     */
    public BooleanProperty canMoveProperty() {
        return canMove;
    }

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

        canStartGame = new SimpleBooleanProperty(false);
        statusText = new SimpleStringProperty("");
        canMove = new SimpleBooleanProperty(false);

        Sync();
    }

    public StringProperty statusTextProperty() {
        return statusText;
    }

    @Override
    public void Sync() {
        ViewModelContext context = getContext();
        GamePlayerRequestBase request = new GamePlayerRequestBase();
        request.setIdGame(context.getIdGame());
        request.setIdPlayer(context.getIdPlayer());

        Response<GetBoardStateResponse> response = requestHandler.makeGETRequest(Constants.GET_BOARD_STATE_PATH + "?idGame=" + context.getIdGame()
                +"&idPlayer="+context.getIdPlayer(), new TypeToken<Response<GetBoardStateResponse>>(){}.getType());

        if(response.getHttpStatusCode()==response.HTTP_OK) {
            int gameState = response.getData().getGameState();
            int turnState = response.getData().getTurnState();

            //TODO fix magic numbers
            if(gameState==0) {  //Game has not started
                boardState=response.getData().getPlayerGameIdMap().size()>1?BoardState.ReadyToStart:BoardState.WaitingForPlayers;
            } else if(gameState==1) {  //Game in progress
                int idCurrentTurn = response.getData().getIdCurrentTurn();

                if(idCurrentTurn==context.getIdPlayer()) { //Current user's turn

                    //Update only if the user does not know it is already their turn
                    if(boardState==BoardState.ReadyToStart
                        || boardState== BoardState.WaitingForPlayers
                        || boardState== BoardState.WaitingForTurn) {
                        if(turnState==0 || turnState==1) { //Base turn
                            canMove.setValue(true);
                            boardState=BoardState.BaseTurn;
                        } else if(turnState==2) { //Disapproval flow
                            boardState=BoardState.DisproveSuggestion;
                        }
                    }
                } else {
                    boardState=BoardState.WaitingForTurn;
                }
            } else if(gameState==2) { //Game over
                boardState=BoardState.GameOver;
            }

            setStateProperties();
        } else {
            //TODO Error scenario
        }
    }

    private void setStateProperties() {
        canStartGame.setValue(boardState==BoardState.ReadyToStart);
        statusText.setValue(boardState.toString());
    }

    /**
     * Starts the game
     */
    public void startGame() {
        ViewModelContext context = getContext();
        GamePlayerRequestBase request = new GamePlayerRequestBase();
        request.setIdPlayer(context.getIdPlayer());
        request.setIdGame(context.getIdGame());

        Response<GetBoardStateResponse> response = requestHandler.makePOSTRequest(Constants.START_GAME_PATH, request,
                new TypeToken<Response<GetBoardStateResponse>>() {
                }.getType());

        if(response.getHttpStatusCode()==response.HTTP_OK) {
            //TODO Do something with the start_game response
        } else {
            //TODO Error scenario
        }

    }

    /**
     * Begins the movement flow
     */
    public void beginMove() {
        boardState=BoardState.StartMove;
        ViewModelContext context = getContext();

        Response<GetValidMovesResponse> response = requestHandler.makeGETRequest(Constants.GET_VALID_MOVES_PATH + "?idGame=" + context.getIdGame() + "&idPlayer=" + context.getIdPlayer(),
                new TypeToken<Response<GetValidMovesResponse>>() {
                }.getType());

        if(response.getHttpStatusCode()==response.HTTP_OK) {
            availableMoves.setAll(response.getData());

            //TODO Setup layout
        } else {
            //TODO Error scenario
        }

    }

    /***
     * Sends the move request to the server
     * @param idRoom Id of the room to move to
     */
    public void moveToRoom(int idRoom) {
        canMove.setValue(false); //TODO Implement this fully

        MovePlayerRequest request = new MovePlayerRequest();
        ViewModelContext context = getContext();
        request.setIdGame(context.getIdGame());
        request.setIdPlayer(context.getIdPlayer());
        request.setIdRoom(idRoom);

        Response<GetBoardStateResponse> response = requestHandler.makePOSTRequest(Constants.MOVE_PLAYER_REQUEST, request,
                new TypeToken<Response<GetBoardStateResponse>>() {
                }.getType());

        if(response.getHttpStatusCode()==response.HTTP_OK) {
            //TODO Do something
        } else {
            //TODO Trigger error scenario
        }
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
