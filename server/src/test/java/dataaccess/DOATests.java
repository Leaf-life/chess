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
        assertThrows(RuntimeException.class, () -> {accessauth.getAuth("123");});
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
        int ID = accessGame.createGame(new GameData(123, null, null, "game", new ChessGame()));
        //assertThrows(RuntimeException.class, () -> {accessGame.createGame(new GameData(ID, null, null, "game", new ChessGame()));});
    }

    @Test
    @Order(8)
    @DisplayName("Get Game Positive")
    public void posGetGameTest() throws DataAccessException{
        GameAccess accessGame= new SqlAccessGame();
        GameData expectedGame = new GameData(123, null, null, "game", new ChessGame());
        int ID = accessGame.createGame(expectedGame);
        GameData game = accessGame.getGame(ID);
        Assertions.assertEquals(expectedGame.game(), game.game());
    }

    @Test
    @Order(10)
    @DisplayName("List Game Positive")
    public void posListGameTest() throws DataAccessException{
        GameAccess accessGame= new SqlAccessGame();
        GameData game1 = new GameData(123, null, null, "game", new ChessGame());
        GameData game2 = new GameData(456, null, null, "game", new ChessGame());
        GameData game3 = new GameData(789, null, null, "game", new ChessGame());
        GameData game4 = new GameData(0, null, null, "game", new ChessGame());
        accessGame.createGame(game1);
        accessGame.createGame(game2);
        accessGame.createGame(game3);
        accessGame.createGame(game4);
        Collection<ChessGame> expectedGame = new ArrayList<>();
        expectedGame.add(game1.game());
        expectedGame.add(game2.game());
        expectedGame.add(game3.game());
        expectedGame.add(game4.game());
        Collection<GameData> game = accessGame.listGame();
        Assertions.assertEquals(expectedGame, game);
    }

    @Test
    @Order(12)
    @DisplayName("Delete Game Positive")
    public void posDeleteGameTest() throws DataAccessException{
        GameAccess accessGame= new SqlAccessGame();
        GameData expectedGame = new GameData(123, null, null, "game", new ChessGame());
        int ID = accessGame.createGame(expectedGame);
        accessGame.deleteGame(new GameData(ID, null, null, "game", new ChessGame()));
        Assertions.assertEquals(0, sizeChecker("game"));
    }

    @Test
    @Order(14)
    @DisplayName("Create User Positive")
    public void posCreateUserTest() throws DataAccessException{
        UserAccess accessUser= new SqlAccessUser();
        accessUser.createUser(new UserData("user", "pass", "email"));
        Assertions.assertEquals(1, sizeChecker("user"));
    }

    @Test
    @Order(16)
    @DisplayName("Get User Positive")
    public void posGetUserTest() throws DataAccessException{
        UserAccess accessUser= new SqlAccessUser();
        UserData expecteduser = new UserData("user", "pass", "email");
        accessUser.createUser(expecteduser);
        UserData user = accessUser.getUser("user");
        Assertions.assertEquals(expecteduser, user);
    }

    @Test
    @Order(18)
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
