package edu.jhu.epioneers.clueless.model;

/***
 * Represents a type of room: a room or hallway
 */
public enum RoomType {
    /***
     * The room type
     */
    ROOM(0),
    /***
     * The hallway type
     */
    HALLWAY(1);

    private int value;

    private RoomType(int val){
        value = val;
    }

    public int getValue(){
        return value;
    }
}
