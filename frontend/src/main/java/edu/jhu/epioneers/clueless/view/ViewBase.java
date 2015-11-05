package edu.jhu.epioneers.clueless.view;

import edu.jhu.epioneers.clueless.viewmodel.ViewModelBase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Base view used for binding data between ViewModels and layouts
 * @param <T> Generic ViewModel constraint
 */
public abstract class ViewBase<T extends ViewModelBase> implements Initializable {
    /**
     * ViewModel instance
     */
    private T model;

    /**
     * Must be implemented by View to create an instance of a ViewModel
     * @return ViewModel instance
     */
    protected abstract T createModel();

    /**
     * Must be implemented by View for setup and data binding
     */
    protected abstract void initialize();

    /**
     * Default constructor, creates model
     */
    ViewBase()
    {
        model = createModel();
    }

    /**
     * Base FXML initialize class, which hooks into the child initialization automatically
     * @param location FXML location
     * @param resources FXML resources
     */
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        initialize();
    }

    public T getModel() {
        return model;
    }
}
