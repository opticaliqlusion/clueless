package edu.jhu.epioneers.clueless.communication;

import com.google.gson.Gson;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class JsonTest {

    private Gson gson = new Gson();

    @Test
    public void When_serialize_GetPendingGamesReponse_Then_expected_returned() {
        //Arrange
        GetPendingGamesReponse model = new GetPendingGamesReponse();
        HashMap<Integer, ArrayList<Integer>> map = new HashMap<Integer, ArrayList<Integer>>();

        ArrayList<Integer> characterIds = new ArrayList<Integer>();
        characterIds.add(1);
        characterIds.add(2);

        map.put(1, characterIds);
        model.setGames(map);

        //Act
        String result = gson.toJson(new Response<GetPendingGamesReponse>(model));

        //Assert
        assert(result.equals("{\"httpStatusCode\":200,\"data\":{\"games\":{\"1\":[1,2]}}}"));
    }

    @Test
    public void When_serializing_GetValidMovesResponse_Then_expected_returned() {
        //Arrange
        GetValidMovesResponse model = new GetValidMovesResponse();
        model.add(1);
        model.add(2);

        //Act
        String result = gson.toJson(new Response<GetValidMovesResponse>(model));

        //Assert
        assert(result.equals("{\"httpStatusCode\":200,\"data\":[1,2]}"));
    }

    @Test
    public void When_serializing_GetBoardStateResponse_Then_expected_returned() {
        //Arrange
        GetBoardStateResponse model = new GetBoardStateResponse();
        HashMap<Integer,Integer> playerGameIdMap = new HashMap<Integer,Integer>();
        playerGameIdMap.put(1,1);
        playerGameIdMap.put(2,3);
        playerGameIdMap.put(3,2);

        model.setPlayerGameIdMap(playerGameIdMap);
        model.setIdCurrentTurn(1);
        model.setPlayerId(2);
        model.setGameState(4);

        ArrayList<String> logs = new ArrayList<String>();
        logs.add("This is a log");
        model.setLogs(logs);
        model.setLastLogId(4);

        ArrayList<Integer> cardIds = new ArrayList<Integer>();
        cardIds.add(1);
        cardIds.add(2);
        cardIds.add(3);

        model.setCardIds(cardIds);

        //Act
        String result = gson.toJson(new Response<GetBoardStateResponse>(model));

        //Assert
        assert(result.equals("{\"httpStatusCode\":200,\"data\":{\"playerGameIdMap\":{\"1\":1,\"2\":3,\"3\":2},\"idCurrentTurn\":1,\"gameState\":4,\"logs\":[\"This is a log\"],\"lastLogId\":4,\"playerId\":2,\"cardIds\":[1,2,3]}}"));
    }

    @Test
    public void When_GetAllCardsResponse_serialized_Then_expected_returned()
    {
        //Arrange
        GetAllCardsResponse model = new GetAllCardsResponse();

        IdNameType card1 = new IdNameType();
        card1.setId(1);
        card1.setName("Card 1");
        card1.setType(0);
        IdNameType card2 = new IdNameType();
        card2.setId(1);
        card2.setName("Card 2");
        card2.setType(0);

        model.add(card1);
        model.add(card2);

        //Act
        String result = gson.toJson(new Response<GetAllCardsResponse>(model));

        //Assert
        assert(result.equals("{\"httpStatusCode\":200,\"data\":[{\"id\":1,\"name\":\"Card 1\",\"type\":0},{\"id\":1,\"name\":\"Card 2\",\"type\":0}]}"));
    }
}
