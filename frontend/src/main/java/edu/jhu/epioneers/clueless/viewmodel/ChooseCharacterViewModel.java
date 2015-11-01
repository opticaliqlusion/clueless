package edu.jhu.epioneers.clueless.viewmodel;

import edu.jhu.epioneers.clueless.model.CharacterModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by Phillip on 10/31/2015.
 */
public class ChooseCharacterViewModel {
    public ChooseCharacterViewModel() {
        CharacterModel model1 = new CharacterModel();
        model1.set_name("Char 1");
        CharacterModel model2 = new CharacterModel();
        model2.set_name("Char 2");
        CharacterModel model3 = new CharacterModel();
        model3.set_name("Char 3");
        CharacterModel model4 = new CharacterModel();
        model4.set_name("Char 4");
        CharacterModel model5 = new CharacterModel();
        model5.set_name("Char 5");
        CharacterModel model6 = new CharacterModel();
        model6.set_name("Char 6");

        _availableCharacters = FXCollections.observableArrayList();
        _availableCharacters.add(model1);
        _availableCharacters.add(model2);
        _availableCharacters.add(model3);

        _inUseCharacters = FXCollections.observableArrayList();
        _inUseCharacters.add(model4);
        _inUseCharacters.add(model5);
        _inUseCharacters.add(model6);
    }


    private ObservableList<CharacterModel> _availableCharacters;
    private ObservableList<CharacterModel> _inUseCharacters;
}
