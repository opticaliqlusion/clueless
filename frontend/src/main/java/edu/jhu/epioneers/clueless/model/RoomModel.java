package edu.jhu.epioneers.clueless.model;

import edu.jhu.epioneers.clueless.communication.IdNameType;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/***
 * Represents a room entity
 */
public class RoomModel extends PositionModel {
    private int type;
    private String imageResourceName;

    private StringProperty textOverlay;

    public RoomModel(IdNameType baseRoomData) {
        setName(baseRoomData.getName());
        setId(baseRoomData.getId());
        type = baseRoomData.getType();
        textOverlay = new SimpleStringProperty("");

        switch (baseRoomData.getName()) {
            case "Study":
                x=0;
                y=0;
                imageResourceName="/images/room.jpg";
                break;
            case "Hall":
                x=0;
                y=2;
                imageResourceName="/images/room.jpg";
                break;
            case "Hall-Study-Hall":
                x=0;
                y=1;
                imageResourceName="/images/hall.jpg";
                break;
            case "Lounge":
                x=0;
                y=4;
                imageResourceName="/images/room.jpg";
                break;
            case "Hall-Hall-Lounge":
                x=0;
                y=3;
                imageResourceName="/images/hall.jpg";
                break;
            case "Dining Room":
                x=2;
                y=4;
                imageResourceName="/images/room.jpg";
                break;
            case "Hall-Lounge-Dining Room":
                x=1;
                y=4;
                imageResourceName="/images/hall.jpg";
                break;
            case "Kitchen":
                x=4;
                y=4;
                imageResourceName="/images/room.jpg";
                break;
            case "Hall-Dining Room-Kitchen":
                x=3;
                y=4;
                imageResourceName="/images/hall.jpg";
                break;
            case "Ballroom":
                x=4;
                y=2;
                imageResourceName="/images/room.jpg";
                break;
            case "Hall-Kitchen-Ballroom":
                x=4;
                y=3;
                imageResourceName="/images/hall.jpg";
                break;
            case "Conservatory":
                x=4;
                y=0;
                imageResourceName="/images/room.jpg";
                break;
            case "Hall-Ballroom-Conservatory":
                x=4;
                y=1;
                imageResourceName="/images/hall.jpg";
                break;
            case "Library":
                x=2;
                y=0;
                imageResourceName="/images/room.jpg";
                break;
            case "Hall-Conservatory-Library":
                x=3;
                y=0;
                imageResourceName="/images/hall.jpg";
                break;
            case "Hall-Library-Study":
                x=1;
                y=0;
                imageResourceName="/images/hall.jpg";
                break;
            case "Billiard Room":
                x=2;
                y=2;
                imageResourceName="/images/room.jpg";
                break;
            case "Hall-Hall-Billiard Room":
                x=1;
                y=2;
                imageResourceName="/images/hall.jpg";
                break;
            case "Hall-Billiard Room-Ballroom":
                x=3;
                y=2;
                imageResourceName="/images/hall.jpg";
                break;
            case "Hall-Library-Billiard Room":
                x=2;
                y=1;
                imageResourceName="/images/hall.jpg";
                break;
            case "Hall-Billiard Room-Dining Room":
                x=2;
                y=3;
                imageResourceName="/images/hall.jpg";
                break;
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImageResourceName() {
        return imageResourceName;
    }

    public void setImageResourceName(String imageResourceName) {
        this.imageResourceName = imageResourceName;
    }

    public StringProperty textOverlayProperty() {
        return textOverlay;
    }
}
