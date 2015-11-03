package edu.jhu.epioneers.clueless.viewmodel;

import edu.jhu.epioneers.clueless.model.ModelBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by Phillip on 10/31/2015.
 */
public class ChooseCharacterViewModel {
    public ChooseCharacterViewModel() {
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


    private ObservableList<ModelBase> _availableCharacters;
    private ObservableList<ModelBase> _inUseCharacters;
}
