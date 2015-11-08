package edu.jhu.epioneers.clueless.communication;

/***
 *
 */
public class DisproveSuggestionRequest extends GamePlayerRequestBase {
    private int idCard;

    public int getIdCard() {
        return idCard;
    }

    public void setIdCard(int idCard) {
        this.idCard = idCard;
    }
}