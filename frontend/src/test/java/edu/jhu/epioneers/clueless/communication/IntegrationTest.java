package edu.jhu.epioneers.clueless.communication;

import com.google.gson.reflect.TypeToken;
import edu.jhu.epioneers.clueless.Constants;
import org.junit.Test;

public class IntegrationTest {
    @Test
    public void When_calling_get_pending_games_Then_successful() {
        //Arrange
        RequestHandler handler = new RequestHandler();

        //Act
        Response<GetPendingGamesReponse> games = handler.makeGETRequest(Constants.GET_PENDING_GAMES_PATH,
                new TypeToken<Response<GetPendingGamesReponse>>() {
                }.getType());

        //Assert
        assert (games.getHttpStatusCode()==games.HTTP_OK);
    }

    @Test
    public void When_calling_get_characters_Then_characters_returned() {
        //Arrange
        RequestHandler handler = new RequestHandler();

        //Act
        Response<GetAllCharactersResponse> characters = handler.makeGETRequest(Constants.GET_CHARACTERS_PATH,
                new TypeToken<Response<GetAllCharactersResponse>>(){}.getType());

        //Assert
        assert (characters.getHttpStatusCode()==characters.HTTP_OK);
        assert (characters.getData().size()>1);
    }

    @Test
    public void When_calling_join_game_new_game_Then_game_created() {
        //Arrange
        RequestHandler handler = new RequestHandler();
        JoinGameRequest request = new JoinGameRequest();

        //Act
        Response<GetBoardStateResponse> characters = handler.makePOSTRequest(Constants.JOIN_GAME_PATH,
                request, new TypeToken<Response<GetBoardStateResponse>>(){}.getType());

        //Assert
        assert (characters.getHttpStatusCode()==characters.HTTP_OK);
        assert (characters.getData().getIdPlayer()>0);
    }
}