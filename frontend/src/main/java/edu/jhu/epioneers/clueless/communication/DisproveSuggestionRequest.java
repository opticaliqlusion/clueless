package edu.jhu.epioneers.clueless.communication;

/***
 *
 */
public class DisproveSuggestionRequest extends GamePlayerRequestBase {
    private Integer idCard;

    public Integer getIdCard() {
        return idCard;
    }

    public void setIdCard(Integer idCard) {
        this.idCard = idCard;
    }
}