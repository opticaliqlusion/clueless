package edu.jhu.epioneers.clueless.communication;

import edu.jhu.epioneers.clueless.model.RoomType;

/***
 * Data class containing room information
 */
public class IdNameType {
    /***
     * Room id
     */
    private int id;

    /***
     * Name of the room
     */
    private String name;

    /***
     * The type of room
     */
    private int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
