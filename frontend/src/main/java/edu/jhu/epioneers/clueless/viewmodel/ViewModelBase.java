package edu.jhu.epioneers.clueless.viewmodel;

import edu.jhu.epioneers.clueless.Constants;
import edu.jhu.epioneers.clueless.communication.RequestHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Base class for each view model, contains common methods and abstract definitions
 */
public abstract class ViewModelBase {
    /**
     * Communicates to the game server
     */
    private RequestHandler requestHandler;

    /**
     * Persists data between ViewModels
     */
    private ViewModelContext context = ViewModelContext.getInstance();

    public ViewModelBase(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
        Sync();
    }

    /**
     * Change
     * @param stage Stage for which to set the scene
     * @param layout Layout to load
     */
    public void changeScene(Stage stage, String layout) {
        //TODO Figure out error pattern
        try {
            Parent root = FXMLLoader.load(getClass().getResource(Constants.ChooseCharacterLayout));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ViewModelContext getContext() {
        return context;
    }

    /**
     * Function should be overridden in the implementation class to sync ViewModel
     * data from the game server and context
     */
    protected abstract void Sync();
}
