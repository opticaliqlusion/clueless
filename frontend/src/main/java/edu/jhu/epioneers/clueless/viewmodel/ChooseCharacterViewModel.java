package edu.jhu.epioneers.clueless.viewmodel;

import edu.jhu.epioneers.clueless.communication.RequestHandler;
import edu.jhu.epioneers.clueless.model.ModelBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * ViewModel for the choose character screen
 */
public class ChooseCharacterViewModel extends ViewModelBase {
    public ChooseCharacterViewModel(RequestHandler requestHandler) {
        super(requestHandler);
    }

    @Override
    protected void Sync() {
        ModelBase model1 = new ModelBase();
        model1.setName("Char 1");
        ModelBase model2 = new ModelBase();
        model2.setName("Char 2");
        ModelBase model3 = new ModelBase();
        model3.setName("Char 3");
        ModelBase model4 = new ModelBase();
        model4.setName("Char 4");
        ModelBase model5 = new ModelBase();
        model5.setName("Char 5");
        ModelBase model6 = new ModelBase();
        model6.setName("Char 6");

        _availableCharacters = FXCollections.observableArrayList();
        _availableCharacters.add(model1);
        _availableCharacters.add(model2);
        _availableCharacters.add(model3);

        _inUseCharacters = FXCollections.observableArrayList();
        _inUseCharacters.add(model4);
        _inUseCharacters.add(model5);
        _inUseCharacters.add(model6);
    }

    /**
     * Characters that are currently available
     */
    private ObservableList<ModelBase> _availableCharacters;

    /**
     * Characters that are currently unavailable
     */
    private ObservableList<ModelBase> _inUseCharacters;

    /**
     * Sends the choose character request to the server
     * @param characterId Id for the character to be chosen
     */
    public void chooseCharacter(int characterId) {
        //Note to implementor Game id comes from the context
    }
}
