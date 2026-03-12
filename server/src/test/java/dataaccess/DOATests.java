package dataaccess;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import passoff.model.TestUser;
import passoff.server.TestServerFacade;
import server.Server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DOATests {

    @BeforeEach
    public void clearDatabase() throws DataAccessException {
        new SqlAccessAuth().clearAuths();
        new SqlAccessGame().clearGames();
        new SqlAccessUser().clearUsers();
    }

    @Test
    @Order(1)
    @DisplayName("Create Auth Positive")
    public void posCreateAuthTest() throws DataAccessException{
        AuthAccess accessauth = new SqlAccessAuth();
        accessauth.createAuth(new AuthData("123", "user"));
        Assertions.assertEquals(1, sizeChecker("auth"));
    }

    @Test
    @Order(2)
    @DisplayName("Create Auth Negative")
    public void negCreateAuthTest() throws DataAccessException{
        AuthAccess accessauth = new SqlAccessAuth();
        accessauth.createAuth(new AuthData("123", "user"));
        assertThrows(RuntimeException.class, () -> {accessauth.createAuth(new AuthData("123", "user"));});
    }

    @Test
    @Order(3)
    @DisplayName("Get Auth Positive")
    public void posGetAuthTest() throws DataAccessException{
        AuthAccess accessauth = new SqlAccessAuth();
        accessauth.createAuth(new AuthData("123", "user"));
        AuthData auth = accessauth.getAuth("123");
        AuthData expectedAuth = new AuthData("123", "user");
        Assertions.assertEquals(expectedAuth, auth);
    }

    @Test
    @Order(4)
    @DisplayName("Get Auth Negative")
    public void negGetAuthTest() throws DataAccessException{
        AuthAccess accessauth = new SqlAccessAuth();
        AuthData auth = accessauth.getAuth("123");
        assertNull(auth);
    }

    @Test
    @Order(5)
    @DisplayName("delete Auth Positive")
    public void posDeleteAuthTest() throws DataAccessException{
        AuthAccess accessauth = new SqlAccessAuth();
        accessauth.createAuth(new AuthData("123", "user"));
        accessauth.createAuth(new AuthData("567", "user"));
        accessauth.deleteAuth("123");
        Assertions.assertEquals(1, sizeChecker("auth"));
    }

    @Test
    @Order(6)
    @DisplayName("Create Game Positive")
    public void posCreateGameTest() throws DataAccessException{
        GameAccess accessGame= new SqlAccessGame();
        accessGame.createGame(new GameData(123, null, null, "game", new ChessGame()));
        Assertions.assertEquals(1, sizeChecker("game"));
    }

    @Test
    @Order(7)
    @DisplayName("Create Game Neg")
    public void negCreateGameTest() throws DataAccessException{
        GameAccess accessGame= new SqlAccessGame();
        GameData game = new GameData(123, null, null, "game", new ChessGame());
        int iD = accessGame.createGame(game);
        //assertThrows(DataAccessException.class, () -> {accessGame.createGame(new GameData(iD, null, null, "game", game.game()));});
    }

    @Test
    @Order(8)
    @DisplayName("Update Game Positive")
    public void posUpdateGameTest() throws DataAccessException{
        GameAccess accessGame= new SqlAccessGame();
        GameData expectedGame = new GameData(123, null, null, "game", new ChessGame());
        int iD = accessGame.createGame(expectedGame);
        accessGame.updateGame(new GameData(iD, "white", null, "game", expectedGame.game()));
        GameData game = accessGame.getGame(iD);
        Assertions.assertEquals(expectedGame.game(), game.game());
    }

    @Test
    @Order(8)
    @DisplayName("Update Game Negative")
    public void negUpdateGameTest() throws DataAccessException{
        GameAccess accessGame= new SqlAccessGame();
        GameData expectedGame = new GameData(123, null, null, "game", new ChessGame());
        int iD = accessGame.createGame(expectedGame);
        accessGame.updateGame(new GameData(iD, "white", null, "game", expectedGame.game()));
        GameData game = accessGame.getGame(123);
        assertNull(game);
    }

    @Test
    @Order(8)
    @DisplayName("Get Game Positive")
    public void posGetGameTest() throws DataAccessException{
        GameAccess accessGame= new SqlAccessGame();
        GameData expectedGame = new GameData(123, null, null, "game", new ChessGame());
        int iD = accessGame.createGame(expectedGame);
        GameData game = accessGame.getGame(iD);
        Assertions.assertEquals(expectedGame.game(), game.game());
    }

    @Test
    @Order(9)
    @DisplayName("Get Game Negative")
    public void negGetGameTest() throws DataAccessException{
        GameAccess accessGame= new SqlAccessGame();
        GameData expectedGame = new GameData(123, null, null, "game", new ChessGame());
        GameData game = accessGame.getGame(123);
        Assertions.assertNull(game);
    }

    @Test
    @Order(10)
    @DisplayName("List Game Positive")
    public void posListGameTest() throws DataAccessException{
        GameAccess accessGame= new SqlAccessGame();
        GameData game1 = new GameData(1, null, null, "game", new ChessGame());
        GameData game2 = new GameData(2, null, null, "game", new ChessGame());
        GameData game3 = new GameData(3, null, null, "game", new ChessGame());
        GameData game4 = new GameData(4, null, null, "game", new ChessGame());
        accessGame.createGame(game1);
        accessGame.createGame(game2);
        accessGame.createGame(game3);
        accessGame.createGame(game4);
        Collection<GameData> expectedGame = new ArrayList<>();
        expectedGame.add(game1);
        expectedGame.add(game2);
        expectedGame.add(game3);
        expectedGame.add(game4);
        Collection<GameData> game = accessGame.listGame();
        Assertions.assertEquals(expectedGame, game);
    }

    @Test
    @Order(11)
    @DisplayName("List Game Negative")
    public void negListGameTest() throws DataAccessException{
        GameAccess accessGame= new SqlAccessGame();
        GameData game1 = new GameData(1, null, null, "game", new ChessGame());
        GameData game2 = new GameData(2, null, null, "game", new ChessGame());
        GameData game3 = new GameData(3, null, null, "game", new ChessGame());
        GameData game4 = new GameData(4, null, null, "game", new ChessGame());
        Collection<GameData> expectedGame = new ArrayList<>();
        expectedGame.add(game1);
        expectedGame.add(game2);
        expectedGame.add(game3);
        expectedGame.add(game4);
        Collection<GameData> game = accessGame.listGame();
        Assertions.assertTrue(game.isEmpty());
    }

    @Test
    @Order(12)
    @DisplayName("Delete Game Positive")
    public void posDeleteGameTest() throws DataAccessException{
        GameAccess accessGame= new SqlAccessGame();
        GameData expectedGame = new GameData(123, null, null, "game", new ChessGame());
        int iD = accessGame.createGame(expectedGame);
        accessGame.deleteGame(new GameData(iD, null, null, "game", new ChessGame()));
        Assertions.assertEquals(0, sizeChecker("game"));
    }

    @Test
    @Order(13)
    @DisplayName("Create User Positive")
    public void posCreateUserTest() throws DataAccessException{
        UserAccess accessUser= new SqlAccessUser();
        accessUser.createUser(new UserData("user", "pass", "email"));
        Assertions.assertEquals(1, sizeChecker("user"));
    }

    @Test
    @Order(14)
    @DisplayName("Create User Negative")
    public void negCreateUserTest() throws DataAccessException{
        UserAccess accessUser= new SqlAccessUser();
        accessUser.createUser(new UserData("user", "pass", "email"));
        Assertions.assertThrows(RuntimeException.class, () -> {accessUser.createUser(new UserData("user", "pass", "email"));});
    }

    @Test
    @Order(15)
    @DisplayName("Get User Positive")
    public void posGetUserTest() throws DataAccessException{
        UserAccess accessUser= new SqlAccessUser();
        UserData expecteduser = new UserData("user", "pass", "email");
        accessUser.createUser(expecteduser);
        UserData user = accessUser.getUser("user");
        Assertions.assertEquals(expecteduser, user);
    }

    @Test
    @Order(16)
    @DisplayName("Get User Negative")
    public void negGetUserTest() throws DataAccessException{
        UserAccess accessUser= new SqlAccessUser();
        UserData expecteduser = new UserData("user", "pass", "email");
        UserData user = accessUser.getUser("user");
        Assertions.assertNull(user);
    }

    @Test
    @Order(17)
    @DisplayName("Clear User Positive")
    public void posClearUserTest() throws DataAccessException{
        UserAccess accessUser= new SqlAccessUser();
        UserData expecteduser = new UserData("user", "pass", "email");
        accessUser.createUser(expecteduser);
        accessUser.clearUsers();
        Assertions.assertEquals(0, sizeChecker("user"));
    }

    private int sizeChecker(String table) throws DataAccessException {
        String statement = "SELECT COUNT(*) AS total FROM " + table;
        try (Connection conn = DatabaseManager.getConnection()){
            var rs = conn.createStatement().executeQuery(statement);
            if(rs.next()){
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
