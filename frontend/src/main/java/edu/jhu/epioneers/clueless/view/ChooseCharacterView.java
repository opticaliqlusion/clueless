package edu.jhu.epioneers.clueless.view;

import edu.jhu.epioneers.clueless.communication.RequestHandler;
import edu.jhu.epioneers.clueless.viewmodel.ChooseCharacterViewModel;

/**
 * Used to bind data from the ChooseCharacterViewModel to the FXML layout
 */
public class ChooseCharacterView extends ViewBase<ChooseCharacterViewModel> {
    @Override
    protected ChooseCharacterViewModel createModel() {
        return new ChooseCharacterViewModel(new RequestHandler());
    }

    @Override
    protected void initialize() {

    }
}
