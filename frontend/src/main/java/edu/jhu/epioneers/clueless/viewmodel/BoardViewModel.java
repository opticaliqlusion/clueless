package edu.jhu.epioneers.clueless.viewmodel;

import com.google.gson.reflect.TypeToken;
import com.sun.javafx.tk.Toolkit;
import edu.jhu.epioneers.clueless.Constants;
import edu.jhu.epioneers.clueless.communication.*;
import edu.jhu.epioneers.clueless.model.BoardState;
import edu.jhu.epioneers.clueless.model.RoomModel;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the game board and corresponding logic
 */
public class BoardViewModel extends ViewModelBase {
    private BooleanProperty canStartGame;
    private BooleanProperty canEndTurn;
    private StringProperty statusText;
    private BooleanProperty canMove;
    private BooleanProperty canCancel;

    private boolean movedDuringCurrentTurn;

    private ObservableList<Integer> availableMoves = FXCollections.observableArrayList();
    private GetBoardStateResponse lastData;

    /**
     * Indicates if the user can start the game
     */
    public BooleanProperty canStartGameProperty() {
        return canStartGame;
    }

    public BooleanProperty canEndTurnProperty() {
        return canEndTurn;
    }

    /**
     * Indicates if the user can move
     */
    public BooleanProperty canMoveProperty() {
        return canMove;
    }

    public BooleanProperty canCancelProperty() {
        return canCancel;
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
        canCancel = new SimpleBooleanProperty(false);
        canEndTurn = new SimpleBooleanProperty(false);

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
            lastData = response.getData();
            syncFromData(lastData);
        } else {
            //TODO Error scenario
        }
    }

    private void syncFromData(GetBoardStateResponse data) {
        ViewModelContext context = getContext();
        int gameState = data.getGameState();
        int turnState = data.getTurnState();
        HashMap<Integer, Integer> playerMaps = data.getPlayerGameIdMap();

        //TODO fix magic numbers
        if(gameState==0) {  //Game has not started
            boardState = playerMaps.size()>1?BoardState.ReadyToStart:BoardState.WaitingForPlayers;
        } else if(gameState==1) {  //Game in progress
            int idCurrentTurn = data.getIdCurrentTurn();

            if(idCurrentTurn==context.getIdPlayer()) { //Current user's turn

                //Update only if the user does not know it is already their turn
                if(boardState==BoardState.ReadyToStart
                        || boardState== BoardState.WaitingForPlayers
                        || boardState== BoardState.WaitingForTurn) {
                    setCharacterOverlays(playerMaps);
                    if(turnState==0 || turnState==1) { //Base turn

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                canMove.setValue(true);
                                movedDuringCurrentTurn=false;
                            }
                        });

                        boardState=BoardState.BaseTurn;
                        setCharacterOverlays(playerMaps);
                    } else if(turnState==2) { //Disapproval flow
                        boardState=BoardState.DisproveSuggestion;
                    }
                }
            } else {
                boardState=BoardState.WaitingForTurn;
                setCharacterOverlays(playerMaps);
            }
        } else if(gameState==2) { //Game over
            boardState=BoardState.GameOver;
        }

            setStateProperties();
    }

    private void setCharacterOverlays(HashMap<Integer, Integer> playerMaps) {
        try {
            //Set character locations over rooms
            for(RoomModel room : getAllRooms()) {
                StringJoiner joiner = new StringJoiner("\n");

                for (Map.Entry<Integer, Integer> playerMap : playerMaps.entrySet()) {
                    try {
                        if(playerMap.getValue()==room.getId()) {
                            joiner.add("Player "+playerMap.getKey());
                        }
                    } catch (NullPointerException ex) {}//TODO Handle this hack
                }

                String newText = joiner.toString();

                if(!room.textOverlayProperty().getValue().equals(newText)) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            room.textOverlayProperty().setValue(newText);
                        }
                    });
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setStateProperties() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                canStartGame.setValue(boardState==BoardState.ReadyToStart);

                setStatusText();

                canEndTurn.setValue(boardState==BoardState.BaseTurn);
            }
        });
    }

    private void setStatusText() {
        switch (boardState) {
            case WaitingForTurn:
                statusText.setValue("Waiting for your turn to begin.");
                break;
            case WaitingForPlayers:
                statusText.setValue("Waiting for another player to join.");
                break;
            case ReadyToStart:
                statusText.setValue("Game is ready to begin.");
                break;
            case BaseTurn:
                statusText.setValue("It is your turn.  Choose an action");
                break;
            case StartMove:
                statusText.setValue("Click the board to move.");
                break;
            case StartSuggestion:
                statusText.setValue("Choose values to make a suggestion.");
                break;
            case DisproveSuggestion:
                statusText.setValue("Choose values to disprove a suggestion.");
                break;
            case MakeAccusation:
                statusText.setValue("Choose values to make an accusation.");
                break;
            case GameOver:
                statusText.setValue("The game has ended.");
                break;
            default:
                statusText.setValue(boardState.toString());
                break;

        }

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
            syncFromData(response.getData());
        } else {
            //TODO Error scenario
        }

    }

    /**
     * Begins the movement flow
     */
    public void beginMove() {
        boardState=BoardState.StartMove;
        canCancel.setValue(true);

        ViewModelContext context = getContext();

        Response<GetValidMovesResponse> response = requestHandler.makeGETRequest(Constants.GET_VALID_MOVES_PATH + "?idGame=" + context.getIdGame() + "&idPlayer=" + context.getIdPlayer(),
                new TypeToken<Response<GetValidMovesResponse>>() {
                }.getType());

        if(response.getHttpStatusCode()==response.HTTP_OK) {
            availableMoves.setAll(response.getData());

            for (Integer roomId : availableMoves) {
                RoomModel room = getAllRooms().stream().filter(r -> r.getId() == roomId).findFirst().orElse(null);
                room.textOverlayProperty().setValue("CLICK TO MOVE");
            }
        } else {
            //TODO Error scenario
        }

    }

    /***
     * Sends the move request to the server
     * @param idRoom Id of the room to move to
     */
    public void moveToRoom(int idRoom) {
        if(boardState==BoardState.StartMove && availableMoves.contains(idRoom)) {
            canMove.setValue(false); //TODO Implement this fully
            movedDuringCurrentTurn=true;

            MovePlayerRequest request = new MovePlayerRequest();
            ViewModelContext context = getContext();
            request.setIdGame(context.getIdGame());
            request.setIdPlayer(context.getIdPlayer());
            request.setIdRoom(idRoom);

            Response<GetBoardStateResponse> response = requestHandler.makePOSTRequest(Constants.MOVE_PLAYER_REQUEST, request,
                    new TypeToken<Response<GetBoardStateResponse>>() {
                    }.getType());

            if(response.getHttpStatusCode()==response.HTTP_OK) {
                boardState=BoardState.BaseTurn;
                canEndTurn.setValue(true);
                canCancel.setValue(false);
                setCharacterOverlays(response.getData().getPlayerGameIdMap());
            } else {
                //TODO Trigger error scenario
            }
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
        ViewModelContext context = getContext();
        GamePlayerRequestBase request = new GamePlayerRequestBase();
        request.setIdPlayer(context.getIdPlayer());
        request.setIdGame(context.getIdGame());
        canMove.setValue(false);

        Response<GetBoardStateResponse> response = requestHandler.makePOSTRequest(Constants.END_TURN_PATH,request,
                new TypeToken<Response<GetBoardStateResponse>>() {
                }.getType());

        if(response.getHttpStatusCode()==response.HTTP_OK) {
            syncFromData(response.getData());
        } else {
            //TODO Error scenario
        }
    }

    /**
     * Returns to the BaseTurn state
     */
    public void cancelAction() {
        boardState=BoardState.BaseTurn;
        canMove.setValue(movedDuringCurrentTurn);
        canCancel.setValue(false);
        setCharacterOverlays(lastData.getPlayerGameIdMap());
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
