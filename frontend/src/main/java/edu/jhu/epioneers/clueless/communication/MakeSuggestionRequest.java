package edu.jhu.epioneers.clueless.communication;

import java.util.ArrayList;

/***
 * Request class for making a suggestion or accusation
 */
public class MakeSuggestionRequest extends GamePlayerRequestBase {
    /***
     * Cards involved in the suggestion
     */
    private ArrayList<Integer> cards;

    public ArrayList<Integer> getCards() {
        return cards;
    }

    public void setCards(ArrayList<Integer> cards) {
        this.cards = cards;
    }
}
