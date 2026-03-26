package service;

import chess.ChessGame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import dataaccess.*;
import model.*;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServiceTests {
    AccessUser accessuser = new AccessUser();
    AccessGame accessgame = new AccessGame();
    AccessAuth accessauth = new AccessAuth();
    ChessService chessservice = new ChessService(accessuser, accessgame, accessauth);

    @Test
    @Order(1)
    @DisplayName("Registration Positive")
    public void testRegistrationPositive() throws DataAccessException{
        UserData user = new UserData("user", "pass", "email");
        AuthData auth = chessservice.registration(user);
        Assertions.assertEquals("user", auth.username());
        Assertions.assertEquals(user, accessuser.getUser("user"));
    }

    @Test
    @Order(2)
    @DisplayName("Registration Negative")
    public void testRegistrationNegative() throws DataAccessException{
        UserData user = new UserData("user", "pass", "email");
        AuthData auth = chessservice.registration(user);
        UserData user2 = new UserData("user", "pass", "email");
        assertThrows(DataAccessException.class, () -> {chessservice.registration(user);});
    }

    @Test
    @Order(3)
    @DisplayName("login Positive")
    public void testLoginPositive() throws DataAccessException{
        UserData user = new UserData("user", "pass", "email");
        chessservice.registration(user);
        AuthData auth = chessservice.login("user", "pass");
        Assertions.assertNotNull(auth);
    }

    @Test
    @Order(4)
    @DisplayName("login Negative")
    public void testLoginNegative() throws DataAccessException{
        UserData user = new UserData("user", "pass", "email");
        chessservice.registration(user);
        assertThrows(DataAccessException.class, () -> {chessservice.login("user", "wrongPass");});
    }

    @Test
    @Order(5)
    @DisplayName("logout Positive")
    public void testLogoutPositive() throws DataAccessException{
        UserData user = new UserData("user", "pass", "email");
        AuthData auth = chessservice.registration(user);
        chessservice.logout(auth.authToken());
    }

    @Test
    @Order(6)
    @DisplayName("logout negative")
    public void testLogoutNegative() throws DataAccessException{
        UserData user = new UserData("user", "pass", "email");
        AuthData auth = chessservice.registration(user);
        chessservice.logout(auth.authToken());
        assertThrows(DataAccessException.class, () -> {chessservice.logout(auth.authToken());});
    }

    @Test
    @Order(7)
    @DisplayName("createGame Positive")
    public void testCreateGamePositive() throws DataAccessException{
        UserData user = new UserData("user", "pass", "email");
        AuthData auth = chessservice.registration(user);
        GameData game = chessservice.createGame(auth.authToken(), "bestGame");
        Assertions.assertNotNull(game);
    }

    @Test
    @Order(8)
    @DisplayName("createGame Negative")
    public void testCreateGameNegative() throws DataAccessException{
        assertThrows(DataAccessException.class, () -> {chessservice.createGame("0", "bestGame");});
    }

    @Test
    @Order(9)
    @DisplayName("listGamesPositive")
    public void testListGamesPositive() throws DataAccessException{
        UserData user = new UserData("user", "pass", "email");
        AuthData auth = chessservice.registration(user);
        chessservice.createGame(auth.authToken(), "bestGame1");
        chessservice.createGame(auth.authToken(), "bestGame2");
        chessservice.createGame(auth.authToken(), "bestGame3");
        Collection<GameData> games = chessservice.listGame(auth.authToken());
        Collection<GameData> expectedGames = new ArrayList<>();
        expectedGames.add(new GameData(1, null, null, "bestGame1", new ChessGame()));
        expectedGames.add(new GameData(2, null, null, "bestGame2", new ChessGame()));
        expectedGames.add(new GameData(3, null, null, "bestGame3", new ChessGame()));
        Assertions.assertEquals(expectedGames, games);
    }

    @Test
    @Order(10)
    @DisplayName("listGames Negative")
    public void testListGamesNegative() throws DataAccessException{
        UserData user = new UserData("user", "pass", "email");
        AuthData auth = chessservice.registration(user);
        chessservice.createGame(auth.authToken(), "bestGame1");
        chessservice.createGame(auth.authToken(), "bestGame2");
        chessservice.createGame(auth.authToken(), "bestGame3");
        assertThrows(DataAccessException.class, () -> {chessservice.listGame(null);});
    }

    @Test
    @Order(11)
    @DisplayName("joinGame Positive")
    public void testJoinGamePositive() throws DataAccessException{
        UserData user1 = new UserData("user", "pass", "email");
        AuthData auth1 = chessservice.registration(user1);
        UserData user2 = new UserData("user2", "pass", "email");
        AuthData auth2 = chessservice.registration(user2);
        GameData game = chessservice.createGame(auth1.authToken(), "bestGame1");
        chessservice.joinGame(auth2.authToken(), "BLACK", game.gameID());
        Collection<GameData> games = chessservice.listGame(auth2.authToken());
        Assertions.assertEquals(1, games.toArray().length);
    }

    @Test
    @Order(12)
    @DisplayName("joinGame Negative")
    public void testJoinGame() throws DataAccessException{
        UserData user1 = new UserData("user", "pass", "email");
        AuthData auth1 = chessservice.registration(user1);
        UserData user2 = new UserData("user2", "pass", "email");
        AuthData auth2 = chessservice.registration(user2);
        GameData game = chessservice.createGame(auth1.authToken(), "bestGame1");
        chessservice.joinGame(auth2.authToken(), "WHITE", game.gameID());
        assertThrows(DataAccessException.class, () -> {chessservice.joinGame(auth2.authToken(), "WHITE", game.gameID());});
    }

    @Test
    @Order(8)
    @DisplayName("clear")
    public void testClear() throws DataAccessException{
        chessservice.clear();
        Assertions.assertEquals(new ArrayList<>(), accessgame.listGame());
        Assertions.assertNull(accessuser.listUsers());
        Assertions.assertNull(accessauth.listAuths());
    }
}
