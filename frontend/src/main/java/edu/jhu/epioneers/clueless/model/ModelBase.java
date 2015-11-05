package edu.jhu.epioneers.clueless.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Base model for all entity data objects
 */
public class ModelBase {
    /**
     * Name, or descriptive identifier
     */
    private String name;
    /**
     * Database id field
     */
    private int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public final StringProperty nameProperty() {
        StringProperty prop = new SimpleStringProperty();
        prop.setValue(name);
        return prop;
    }

    public void setId(int id) {
        this.id = id;
    }
}
