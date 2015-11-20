package edu.jhu.epioneers.clueless.viewmodel;

import com.google.gson.reflect.TypeToken;
import edu.jhu.epioneers.clueless.Constants;
import edu.jhu.epioneers.clueless.communication.GetBoardStateResponse;
import edu.jhu.epioneers.clueless.communication.JoinGameRequest;
import edu.jhu.epioneers.clueless.communication.RequestHandler;
import edu.jhu.epioneers.clueless.communication.Response;
import edu.jhu.epioneers.clueless.model.GameSummaryModel;
import edu.jhu.epioneers.clueless.model.ModelBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * ViewModel for the choose character screen
 */
public class ChooseCharacterViewModel extends ViewModelBase {
    public ChooseCharacterViewModel(RequestHandler requestHandler) {
        super(requestHandler);
    }

    @Override
    protected void Sync() {
        availableCharacters = FXCollections.observableArrayList();
        inUseCharacters = FXCollections.observableArrayList();

        GameSummaryModel selectedGame = getContext().getSelectedGame();

        ArrayList<Integer> inUseIds = selectedGame==null?new ArrayList<>():selectedGame.getInUseCharacters();

        for(ModelBase character : getAllCharacters()) {
            if(inUseIds.stream().filter(c -> c.equals(character.getId())).findFirst().orElse(null)==null) {
                availableCharacters.add(character);
            } else {
                inUseCharacters.add(character);
            }
        }
    }

    /**
     * Characters that are currently available
     */
    private ObservableList<ModelBase> availableCharacters;

    /**
     * Characters that are currently unavailable
     */
    private ObservableList<ModelBase> inUseCharacters;

    /**
     * Sends the choose character request to the server
     * @param window
     * @param characterId Id for the character to be chosen
     */
    public void chooseCharacter(Stage window, int characterId) {
        ViewModelContext context = getContext();
        GameSummaryModel existingGame = context.getSelectedGame();
        JoinGameRequest request  = new JoinGameRequest();
        request.setIdCharacter(characterId);

        if(existingGame!=null) {
            request.setIdGame(existingGame.getId());
        }

        Response<GetBoardStateResponse> response = requestHandler.makePOSTRequest(Constants.JOIN_GAME_PATH, request,
                new TypeToken<Response<GetBoardStateResponse>>(){}.getType());

        if(response.getHttpStatusCode() == response.HTTP_OK) {
            context.setIdGame(response.getData().getIdGame());
            context.setIdCharacter(characterId);
            context.setIdPlayer(response.getData().getIdPlayer());
        } else {
            //TODO Trigger error scenario
        }
    }

    public ObservableList<ModelBase> getAvailableCharacters() {
        return availableCharacters;
    }
}
