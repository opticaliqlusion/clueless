package edu.jhu.epioneers.clueless.view;

import edu.jhu.epioneers.clueless.communication.RequestHandler;
import edu.jhu.epioneers.clueless.viewmodel.BoardViewModel;

/**
 * Used to bind data from the BoardViewModel to the FXML layout
 */
public class BoardView extends ViewBase<BoardViewModel> {
    @Override
    protected BoardViewModel createModel() {
        return new BoardViewModel(new RequestHandler());
    }

    @Override
    protected void initialize() {

    }
}
