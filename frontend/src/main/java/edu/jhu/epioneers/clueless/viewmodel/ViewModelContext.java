package edu.jhu.epioneers.clueless.viewmodel;

import edu.jhu.epioneers.clueless.communication.IdNameType;
import edu.jhu.epioneers.clueless.model.GameSummaryModel;
import edu.jhu.epioneers.clueless.model.ModelBase;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * Singleton class that contains data to be synced between ViewModels
 */
public class ViewModelContext {
    private final static ViewModelContext instance = new ViewModelContext();

    public static ViewModelContext getInstance() {
        return instance;
    }

    //These three fields contain static game data for the current player
    private int idGame;
    private int idCharacter;
    private int idPlayer;

    /**
     * Currently selected game for the choose character screen
     */
    private GameSummaryModel selectedGame;

    public GameSummaryModel getSelectedGame() {
        return selectedGame;
    }

    public void setSelectedGame(GameSummaryModel selectedGame) {
        this.selectedGame = selectedGame;
    }

    private ArrayList<ModelBase> allCharacters;
    private ArrayList<IdNameType> allRooms;

    private ObservableList<ModelBase> weaponCards;
    private ObservableList<ModelBase> characterCards;
    private ObservableList<ModelBase> roomCards;

    public ArrayList<ModelBase> getAllCharacters() {
        return allCharacters;
    }

    public void setAllCharacters(ArrayList<ModelBase> allCharacters) {
        this.allCharacters = allCharacters;
    }

    public int getIdGame() {
        return idGame;
    }

    public void setIdGame(int idGame) {
        this.idGame = idGame;
    }

    public int getIdCharacter() {
        return idCharacter;
    }

    public void setIdCharacter(int idCharacter) {
        this.idCharacter = idCharacter;
    }

    public int getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(int idPlayer) {
        this.idPlayer = idPlayer;
    }

    public ObservableList<ModelBase> getWeaponCards() {
        return weaponCards;
    }

    public void setWeaponCards(ObservableList<ModelBase> weaponCards) {
        this.weaponCards = weaponCards;
    }

    public ObservableList<ModelBase> getCharacterCards() {
        return characterCards;
    }

    public void setCharacterCards(ObservableList<ModelBase> characterCards) {
        this.characterCards = characterCards;
    }

    public ObservableList<ModelBase> getRoomCards() {
        return roomCards;
    }

    public void setRoomCards(ObservableList<ModelBase> roomCards) {
        this.roomCards = roomCards;
    }

    public ArrayList<IdNameType> getAllRooms() {
        return allRooms;
    }

    public void setAllRooms(ArrayList<IdNameType> allRooms) {
        this.allRooms = allRooms;
    }
}
