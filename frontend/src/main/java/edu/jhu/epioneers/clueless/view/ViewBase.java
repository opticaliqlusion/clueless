package edu.jhu.epioneers.clueless.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Phillip on 10/31/2015.
 */
public abstract class ViewBase<T> implements Initializable {
    public T model;
    protected abstract T createModel();
    protected abstract void initialize();

    ViewBase()
    {
        model = createModel();
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        initialize();
    }
}
