package passoff.server;

import chess.ChessGame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import service.*;
import model.*;
import dataaccess.*;

import java.util.ArrayList;
import java.util.Collection;

public class ServiceTests {
    AccessUser accessuser = new AccessUser();
    AccessGame accessgame = new AccessGame();
    AccessAuth accessauth = new AccessAuth();
    ChessService chessservice = new ChessService(accessuser, accessgame, accessauth);

    @Test
    @Order(1)
    @DisplayName("Registration")
    public void testRegistration() throws DataAccessException{
        UserData user = new UserData("user", "pass", "email");
        AuthData auth = chessservice.registration(user);
        Assertions.assertEquals("user", auth.username());
        Assertions.assertEquals(user, accessuser.getUser("user"));
    }

    @Test
    @Order(2)
    @DisplayName("login")
    public void testLogin() throws DataAccessException{
        UserData user = new UserData("user", "pass", "email");
        chessservice.registration(user);
        AuthData auth = chessservice.login("user", "pass");
        Assertions.assertNotNull(auth);
    }

    @Test
    @Order(3)
    @DisplayName("logout")
    public void testLogout() throws DataAccessException{
        UserData user = new UserData("user", "pass", "email");
        AuthData auth = chessservice.registration(user);
        chessservice.logout(auth.authToken());
    }

    @Test
    @Order(4)
    @DisplayName("createGame")
    public void testcreateGame() throws DataAccessException{
        UserData user = new UserData("user", "pass", "email");
        AuthData auth = chessservice.registration(user);
        GameData game = chessservice.createGame(auth.authToken(), "bestGame");
        Assertions.assertNotNull(game);
    }

    @Test
    @Order(5)
    @DisplayName("listGames")
    public void testListGames() throws DataAccessException{
        UserData user = new UserData("user", "pass", "email");
        AuthData auth = chessservice.registration(user);
        chessservice.createGame(auth.authToken(), "bestGame1");
        chessservice.createGame(auth.authToken(), "bestGame2");
        chessservice.createGame(auth.authToken(), "bestGame3");
        Collection<GameData> games = chessservice.listGame(auth.authToken());
        Collection<GameData> expectedGames = new ArrayList<>();
        expectedGames.add(new GameData(1, "user", null, "bestGame1", new ChessGame()));
        expectedGames.add(new GameData(2, "user", null, "bestGame2", new ChessGame()));
        expectedGames.add(new GameData(3, "user", null, "bestGame3", new ChessGame()));
        Assertions.assertEquals(expectedGames, games);
    }

    @Test
    @Order(6)
    @DisplayName("joinGame")
    public void testJoinGame() throws DataAccessException{
        UserData user1 = new UserData("user", "pass", "email");
        AuthData auth1 = chessservice.registration(user1);
        UserData user2 = new UserData("user2", "pass", "email");
        AuthData auth2 = chessservice.registration(user2);
        GameData game = chessservice.createGame(auth1.authToken(), "bestGame1");
        chessservice.joinGame(auth2.authToken(), "black", game.gameID());
        Collection<GameData> games = chessservice.listGame(auth2.authToken());
        Assertions.assertEquals(2, games.toArray().length);
    }

    @Test
    @Order(7)
    @DisplayName("clear")
    public void testClear() throws DataAccessException{
        chessservice.clear();
        Assertions.assertEquals(new ArrayList<>(), accessgame.listGame());
        Assertions.assertNull(accessuser.listUsers());
        Assertions.assertNull(accessauth.listAuths());
    }
}
