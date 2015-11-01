package edu.jhu.epioneers.clueless.view;

import edu.jhu.epioneers.clueless.viewmodel.ChooseCharacterViewModel;

/**
 * Created by Phillip on 10/31/2015.
 */
public class ChooseCharacterView extends ViewBase<ChooseCharacterViewModel> {
    @Override
    protected ChooseCharacterViewModel createModel() {
        return new ChooseCharacterViewModel();
    }

    @Override
    protected void initialize() {

    }
}
