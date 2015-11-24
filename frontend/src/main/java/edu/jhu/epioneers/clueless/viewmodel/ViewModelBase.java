package edu.jhu.epioneers.clueless.viewmodel;

import com.google.gson.reflect.TypeToken;
import edu.jhu.epioneers.clueless.Constants;
import edu.jhu.epioneers.clueless.communication.*;
import edu.jhu.epioneers.clueless.model.ModelBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Base class for each view model, contains common methods and abstract definitions
 */
public abstract class ViewModelBase {
    /**
     * Communicates to the game server
     */
    protected RequestHandler requestHandler;

    /**
     * Persists data between ViewModels
     */
    private ViewModelContext context = ViewModelContext.getInstance();

    public ViewModelBase(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    /**
     * Change
     * @param stage Stage for which to set the scene
     * @param layout Layout to load
     */
    public void changeScene(Stage stage, String layout) {
        //TODO Figure out error pattern
        try {
            Parent root = FXMLLoader.load(getClass().getResource(layout));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ViewModelContext getContext() {
        return context;
    }

    /**
     * Function should be overridden in the implementation class to sync ViewModel
     * data from the game server and context
     */
    public abstract void Sync();

    protected ArrayList<ModelBase> getAllCharacters() {
        if(context.getAllCharacters()==null)
        {
            context.setAllCharacters((IdNameListResponse) requestHandler.makeGETRequest(Constants.GET_CHARACTERS_PATH,
                    new TypeToken<Response<IdNameListResponse>>(){}.getType()).getData());
        }

        return context.getAllCharacters();
    }

    protected ArrayList<IdNameType> getAllRooms() {
        if(context.getAllRooms()==null) {
            context.setAllRooms((IdNameTypeListResponse) requestHandler.makeGETRequest(Constants.GET_ALL_ROOMS_PATH,
                    new TypeToken<Response<IdNameTypeListResponse>>(){}.getType()).getData());
        }

        return context.getAllRooms();
    }

    public ObservableList<ModelBase> getWeaponCards() {
        setUpCards();
        return context.getWeaponCards();
    }

    public ObservableList<ModelBase> getCharacterCards() {
        setUpCards();
        return context.getCharacterCards();
    }

    private void setUpCards() {
        //Lazy init
        if(context.getWeaponCards()==null) {
            ObservableList<ModelBase> weaponCards = FXCollections.observableArrayList();
            ObservableList<ModelBase> characterCards = FXCollections.observableArrayList();
            ObservableList<ModelBase> roomCards = FXCollections.observableArrayList();

            Response<IdNameTypeListResponse> response = requestHandler.makeGETRequest(Constants.GET_ALL_CARDS_PATH,
                    new TypeToken<Response<IdNameTypeListResponse>>() {
                    }.getType());

            if(response.getHttpStatusCode()==response.HTTP_OK) {
                for(IdNameType card : response.getData()) {
                    ModelBase addModel = new ModelBase();
                    addModel.setId(card.getId());
                    addModel.setName(card.getName());

                    //TODO Magic numbers
                    if(card.getType()==0) { //weapon
                        weaponCards.add(addModel);
                    } else if(card.getType()==1) { //room
                        roomCards.add(addModel);
                    } else if(card.getType()==2) { //character
                        characterCards.add(addModel);
                    }
                }
            } else {
                //TODO Trigger error scenario
            }

            context.setWeaponCards(weaponCards);
            context.setRoomCards(roomCards);
            context.setCharacterCards(characterCards);
        }
    }
}
