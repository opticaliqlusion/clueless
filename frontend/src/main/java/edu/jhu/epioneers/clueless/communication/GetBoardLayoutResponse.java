package edu.jhu.epioneers.clueless.communication;

import edu.jhu.epioneers.clueless.model.RoomType;

import java.util.ArrayList;
import java.util.HashMap;

/***
 * The response of the get_board_layout service operation.
 */
public class GetBoardLayoutResponse {

    /***
     * Maps room information to a list of adjacent rooms
     */
    private HashMap<RoomType,ArrayList<Integer>> layout;

    public HashMap<RoomType, ArrayList<Integer>> getLayout() {
        return layout;
    }

    public void setLayout(HashMap<RoomType, ArrayList<Integer>> layout) {
        this.layout = layout;
    }
}
