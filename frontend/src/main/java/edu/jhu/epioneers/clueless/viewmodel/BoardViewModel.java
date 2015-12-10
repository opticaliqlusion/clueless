package edu.jhu.epioneers.clueless.viewmodel;

import com.google.gson.reflect.TypeToken;
import edu.jhu.epioneers.clueless.Constants;
import edu.jhu.epioneers.clueless.communication.*;
import edu.jhu.epioneers.clueless.model.BoardState;
import edu.jhu.epioneers.clueless.model.ModelBase;
import edu.jhu.epioneers.clueless.model.RoomModel;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.awt.Color;
import java.util.*;

/**
 * Represents the game board and corresponding logic
 */
public class BoardViewModel extends ViewModelBase {

    private BooleanProperty canStartGame = new SimpleBooleanProperty(false);
    private BooleanProperty canEndTurn = new SimpleBooleanProperty(false);
    private StringProperty statusText = new SimpleStringProperty("");
    private BooleanProperty canMove = new SimpleBooleanProperty(false);
    private BooleanProperty canCancel = new SimpleBooleanProperty(false);
    private BooleanProperty canDisproveSuggestion = new SimpleBooleanProperty(false);
    private BooleanProperty isAccusation = new SimpleBooleanProperty(false);
    private BooleanProperty canMakeSuggestion = new SimpleBooleanProperty(false);
    private BooleanProperty isBaseTurn = new SimpleBooleanProperty(false);
    private ObservableList<String> logText = FXCollections.observableArrayList();
    private ObservableList<String> cardsText = FXCollections.observableArrayList();

    private boolean movedDuringCurrentTurn;

    private ObservableList<Integer> availableMoves = FXCollections.observableArrayList();
    private GetBoardStateResponse lastData;
    private ObservableList<ModelBase> disprovalCards = FXCollections.observableArrayList();

    public ObservableList<String> logTextProperty() {
        return logText;
    }

    public ObservableList<String> cardsTextProperty() {
        return cardsText;
    }

    public BooleanProperty isBaseTurnProperty() {
        return isBaseTurn;
    }

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

    public BooleanProperty canMakeSuggestionProperty() {
        return canMakeSuggestion;
    }

    /**
     * Indicates the current state of the game board
     */
    private BoardState boardState;

    public BoardViewModel(RequestHandler requestHandler) {
        super(requestHandler);
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
        if(logText.size()!=data.getLogs().size()) {
            Platform.runLater(new Runnable() { //TODO Sync only when necessary
                @Override
                public void run() {
                    logText.setAll(data.getLogs());
                }
            });
        }

        ViewModelContext context = getContext();
        int gameState = data.getGameState();
        int turnState = data.getTurnState();
        HashMap<Integer, Integer> playerMaps = data.getPlayerGameIdMap();

        //TODO fix magic numbers
        if(data.getWinner()!=null) {
            if(data.getWinner()==getContext().getIdPlayer()) {
                boardState = BoardState.GameOverWin;
            } else {
                boardState = BoardState.GameOverFail;
            }

            setStatusText();
            setModelDisposed(true);
            return;
        }
        else if(gameState==0) {  //Game has not started
            boardState = playerMaps.size()>1?BoardState.ReadyToStart:BoardState.WaitingForPlayers;
        } else if(gameState==1) {  //Game in progress
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Integer> cardIds = data.getCardIds();

                        if(cardIds!=null && cardsText.size()!=cardIds.size()) {
                            cardsText.clear();

                            ArrayList<ModelBase> allCards = new ArrayList<ModelBase>();
                            allCards.addAll(getWeaponCards());
                            allCards.addAll(getCharacterCards());
                            allCards.addAll(getRoomCards());

                            for(Integer cardId : cardIds) {
                                ModelBase card = allCards.stream().filter(c -> c.getId() == cardId).findFirst().orElse(null);
                                cardsText.add(card.getName());
                            }
                        }
                    }
                });

            int idCurrentTurn = data.getIdCurrentTurn();

            //User needs to disprove
            if(data.getTurnState()==2) {
                if(data.getIdCurrentDisprover()==context.getIdPlayer()&& boardState == BoardState.WaitingForTurn) {
                    ArrayList<ModelBase> localDisproveCards = new ArrayList<ModelBase>();

                    //TODO Ugly
                    for(Integer cardId : data.getCurrentSuggestion()) {
                        if(data.getCardIds().stream().filter(c->c.equals(cardId)).findFirst().orElse(null)!=null) {
                            ModelBase card;
                            if((card=getCharacterCards().stream().filter(c->c.getId()==cardId).findFirst().orElse(null))!=null) {
                                localDisproveCards.add(card);
                            } else if((card=getWeaponCards().stream().filter(c->c.getId()==cardId).findFirst().orElse(null))!=null) {
                                localDisproveCards.add(card);
                            } else {
                                localDisproveCards.add(getRoomCards().stream().filter(c->c.getId()==cardId).findFirst().orElse(null));
                            }
                        }
                    }

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

                    if(localDisproveCards.size()>0) {
                        boardState = BoardState.DisproveSuggestion;

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                disprovalCards.setAll(localDisproveCards);
                                canDisproveSuggestion.setValue(true);
                            }
                        });
                    } else {
                        DisproveSuggestionRequest request = new DisproveSuggestionRequest();
                        request.setIdGame(getContext().getIdGame());
                        request.setIdPlayer(getContext().getIdPlayer());

                        Response<GetBoardStateResponse> response = requestHandler.makePOSTRequest(Constants.DISPROVE_PATH, request,
                                new TypeToken<Response<GetBoardStateResponse>>() {
                                }.getType());

                        if(response.getHttpStatusCode()==response.HTTP_OK) {
                            //TODO Do nothing?
                        } else {
                            //TODO Error scenario
                        }
                    }
                }
            }
            else if(idCurrentTurn==context.getIdPlayer()) { //Current user's turn
                //Update only if the user does not know it is already their turn
                if(boardState==BoardState.ReadyToStart
                        || boardState== BoardState.WaitingForPlayers
                        || boardState== BoardState.WaitingForTurn) {
                    setCharacterOverlays(playerMaps, data);
                    if(turnState==0 || turnState==1) { //Base turn

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                isBaseTurn.setValue(true);
                                canMove.setValue(true);
                                movedDuringCurrentTurn=false;
                            }
                        });

                        boardState=BoardState.BaseTurn;
                        setCharacterOverlays(playerMaps, data);
                    }
                } else if(boardState==BoardState.WaitingForDisprover) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            canMove.setValue(false);
                            isBaseTurn.setValue(true);
                        }
                    });
                    boardState=BoardState.BaseTurn;
                }
            } else {
                boardState=BoardState.WaitingForTurn;
                setCharacterOverlays(playerMaps, data);
            }
        } else if(gameState==2) { //Game over
            boardState=BoardState.GameOver;
        }

            setStateProperties();
    }

    private void setCharacterOverlays(HashMap<Integer, Integer> playerMaps, GetBoardStateResponse data) {
        try {
            HashMap<Integer, Integer> characterMap = data.getCharacterMap();
            ArrayList<ModelBase> characters = getAllCharacters();

            //Set character locations over rooms
            for(RoomModel room : getAllRooms()) {
                StringJoiner joiner = new StringJoiner("\n");
                Boolean isPlayer = false;
                for (Map.Entry<Integer, Integer> playerMap : playerMaps.entrySet()) {
                    try {
                    	
                        if(playerMap.getValue()==room.getId()) {
                        	Integer characterId = characterMap.entrySet().stream().filter(c -> c.getKey().equals(playerMap.getKey())).findFirst().orElse(null).getValue();
                            if(getContext().getIdCharacter()==characterId){
                                joiner.add(characters.stream().filter(c->c.getId()==characterId).findFirst().orElse(null).getName() + "(you)");
                            	isPlayer = true;
                            } else {
                                joiner.add(characters.stream().filter(c->c.getId()==characterId).findFirst().orElse(null).getName());
                            }
                        }
                       
                        
                    } catch (NullPointerException ex) {}//TODO Handle this hack
                }

                String newText = joiner.toString();
                Boolean isThePlayer = isPlayer;
                if(!room.textOverlayProperty().getValue().equals(newText)) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            room.textOverlayProperty().setValue(newText);
                            if(isThePlayer){
                            	room.getStyle().setValue("-fx-color: black; -fx-background-color: yellow;");
                            } else {
                            	room.getStyle().setValue("-fx-color: black; -fx-background-color: white;");
                            }
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
    	Platform.runLater(new Runnable() {
        @Override
        public void run() {
	        switch (boardState) {
	            case WaitingForTurn:
	                ArrayList<Integer> losers = lastData.getLosers();
	
	                statusText.setValue(losers==null || losers.stream().anyMatch(c->c.equals(getContext().getIdPlayer()))?
	                                "You have lost.  Please remain in the game to help disprove suggestions."
	                                :"Waiting for your turn to begin.");
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
	            case WaitingForDisprover:
	                statusText.setValue("Waiting for someone to disprove your suggestion.");
	                break;
	            case GameOverWin:
	                statusText.setValue("You have won the game!");
	                break;
	            case GameOverFail:
	                statusText.setValue("You have lost the game!");
	                break;
	            default:
	                statusText.setValue(boardState.toString());
	                break;
	
	       }
        }
    	});
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

                if(getAllRooms().stream().filter(r -> r.getId()==idRoom).findFirst().orElse(null).getType()==0) { //TODO Magic number
                    boardState=BoardState.StartSuggestion;
                    canMakeSuggestion.setValue(true);
                    isAccusation.setValue(false);
                    isBaseTurn.setValue(false);
                } else {
                    isBaseTurn.setValue(true);
                    canEndTurn.setValue(true);
                    canCancel.setValue(false);
                }

                setCharacterOverlays(response.getData().getPlayerGameIdMap(), response.getData());
            } else {
                //TODO Trigger error scenario
            }
        }
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
            isBaseTurn.setValue(false);
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
        setCharacterOverlays(lastData.getPlayerGameIdMap(), lastData);
        isBaseTurn.setValue(true);
        canMakeSuggestion.setValue(false);
    }

    /**
     * Sends the suggestion request to the server.  The room id is implicit based on location
     * @param weaponId The id of the weapon being suggested
     * @param characterId The id of the character being suggested
     */
    public void submitSuggestion(int weaponId, int characterId) {
        MakeSuggestionRequest request = new MakeSuggestionRequest();
        request.setIdPlayer(getContext().getIdPlayer());
        request.setIdGame(getContext().getIdGame());

        ArrayList<Integer> cards = new ArrayList<Integer>();

        cards.add(weaponId);
        cards.add(characterId);
        request.setCards(cards);

        Response<GetBoardStateResponse> response = requestHandler.makePOSTRequest(Constants.MAKE_SUGGESTION_PATH,request,
                new TypeToken<Response<GetBoardStateResponse>>() {
                }.getType());

        if(response.getHttpStatusCode()==response.HTTP_OK) {
            syncFromData(response.getData());
            canMakeSuggestion.setValue(false);
            canMove.setValue(false);
            canCancel.setValue(false);
            boardState=BoardState.WaitingForDisprover;
            setStateProperties();
        } else {
            //TODO Error scenario
        }
    }

    /**
     * Sends the disprove suggestion request to the server.  Only one of the id fields will contain a value.
     */
    public void disproveSuggestion(int idCard) {
        DisproveSuggestionRequest request = new DisproveSuggestionRequest();
        request.setIdCard(idCard);
        request.setIdPlayer(getContext().getIdPlayer());
        request.setIdGame(getContext().getIdGame());

        Response<GetBoardStateResponse> response = requestHandler.makePOSTRequest(Constants.DISPROVE_PATH,request,
                new TypeToken<Response<GetBoardStateResponse>>() {
                }.getType());

        if(response.getHttpStatusCode()==response.HTTP_OK) {
            syncFromData(response.getData());
            canDisproveSuggestion.set(false);
            boardState=BoardState.WaitingForTurn;
            setStateProperties();
        } else {
            //TODO Error scenario
        }
    }

    /**
     * Sends the make accusation request to the server.
     */
    public void beginAccusation() {
        canMakeSuggestion.setValue(true);
        isAccusation.setValue(true);
        boardState=BoardState.MakeAccusation;
        isBaseTurn.setValue(false);

        setStatusText();
    }

    public void submitAccusation(int weaponId, int characterId, int roomId) {
        canMakeSuggestion.setValue(false);

        ArrayList<Integer> cards = new ArrayList<Integer>();
        cards.add(weaponId);
        cards.add(characterId);
        cards.add(roomId);

        MakeSuggestionRequest request = new MakeSuggestionRequest();
        request.setIdPlayer(getContext().getIdPlayer());
        request.setIdGame(getContext().getIdGame());
        request.setCards(cards);

        Response<GetBoardStateResponse> response = requestHandler.makePOSTRequest(Constants.MAKE_ACCUSATION_PATH,request,
                new TypeToken<Response<GetBoardStateResponse>>() {
                }.getType());

        if(response.getHttpStatusCode()==response.HTTP_OK) {
            GetBoardStateResponse data = response.getData();

            //See if you won or lost 
            if(data.getLosers().contains(getContext().getIdPlayer())) {
                endTurn();
                canDisproveSuggestion.set(false);
                setStatusText();
            } else { 
                setModelDisposed(true);
                boardState=BoardState.GameOverWin;
                setStatusText();

            }

            setStateProperties();
        } else {
            //TODO Error scenario
        }
    }

    public ObservableList<ModelBase> getDisprovalCards() {
        return disprovalCards;
    }

    public BooleanProperty canDisproveSuggestionProperty() {
        return canDisproveSuggestion;
    }

    public BooleanProperty isAccusationProperty() {
        return isAccusation;
    }

    public void addLog(String text) {
        AddLogRequest request = new AddLogRequest();
        request.setIdGame(getContext().getIdGame());
        request.setIdPlayer(getContext().getIdPlayer());
        request.setLogContent(text);

        requestHandler.makePOSTRequest(Constants.ADD_LOG_PATH,request,
                new TypeToken<Response<GetBoardStateResponse>>() {
                }.getType());
    }
}
