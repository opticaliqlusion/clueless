package edu.jhu.epioneers.clueless.view;

import edu.jhu.epioneers.clueless.communication.RequestHandler;
import edu.jhu.epioneers.clueless.model.ModelBase;
import edu.jhu.epioneers.clueless.viewmodel.ChooseCharacterViewModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.lang.reflect.Field;

/**
 * Used to bind data from the ChooseCharacterViewModel to the FXML layout
 */
public class ChooseCharacterView extends ViewBase<ChooseCharacterViewModel> {

    @FXML
    public ListView<ModelBase> lvCharacters;

    @FXML
    public Button btnJoinGame;

    @Override
    protected ChooseCharacterViewModel createModel() {
        return new ChooseCharacterViewModel(new RequestHandler());
    }

    @Override
    protected void initialize() {
        final ChooseCharacterViewModel model = getModel();

        lvCharacters.setItems(model.getAvailableCharacters());
        lvCharacters.setCellFactory(ChoiceBoxListCell.forListView(new StringConverter<ModelBase>() {
            @Override
            public String toString(ModelBase object) {
                return object.getName();
            }

            @Override
            public ModelBase fromString(String string) { return null;}
        }));

        btnJoinGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ModelBase selected = lvCharacters.getSelectionModel().selectedItemProperty().get();

                if(selected!=null) {
                    model.chooseCharacter((Stage) btnJoinGame.getScene().getWindow(), selected.getId());
                }
            }
        });
    }
}
