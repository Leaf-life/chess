package client;

import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private AuthData auth;

    @BeforeAll
    public static void init() throws DataAccessException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
        clearDatabases();
    }

    @BeforeEach
    public void clear() throws DataAccessException {
        clearDatabases();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    @Order(1)
    @DisplayName("Reg Positive")
    public void posRegTest() throws ResponseException{
        AuthData auth = serverFacade.registration(new UserData("user", "pass", "email@123"));
        Assertions.assertEquals("user", auth.username());
    }

    @Test
    @Order(2)
    @DisplayName("Reg Negative")
    public void negRegTest() throws ResponseException {
        UserData user = new UserData("user", "pass", "email@123");
        serverFacade.registration(user);
        Assertions.assertThrows(ResponseException.class,() -> {serverFacade.registration(user);});
    }

    @Test
    @Order(3)
    @DisplayName("Logout Pos")
    public void posLogoutTest() throws ResponseException {
        AuthData auth = serverFacade.registration(new UserData("user", "pass", "email@123"));
        serverFacade.logout(auth.authToken());
        Assertions.assertThrows(ResponseException.class,() -> {serverFacade.createGame(auth.authToken(), "game1");});
    }
    @Test
    @Order(4)
    @DisplayName("Logout Negative")
    public void negLogoutTest() throws ResponseException {
        Assertions.assertThrows(ResponseException.class,() -> {serverFacade.logout("123");});
    }

    @Test
    @Order(5)
    @DisplayName("Login Positive")
    public void posLoginTest() throws ResponseException {
        AuthData auth = serverFacade.registration(new UserData("user", "pass", "email@123"));
        serverFacade.logout(auth.authToken());
        auth = serverFacade.login("user", "pass");
        Assertions.assertEquals("user", auth.username());
    }

    @Test
    @Order(6)
    @DisplayName("Login Negative")
    public void negLoginTest() throws ResponseException {
        Assertions.assertThrows(ResponseException.class,() -> {serverFacade.login("user", "pass");});
    }

    @Test
    @Order(7)
    @DisplayName("Create Game Positive")
    public void posCreateGameTest() throws ResponseException {
        AuthData auth = serverFacade.registration(new UserData("user", "pass", "email@123"));
        GameData game = serverFacade.createGame(auth.authToken(), "game1");
        Assertions.assertEquals("game1", game.gameName());
    }

    @Test
    @Order(8)
    @DisplayName("Create Game Negative")
    public void negCreateGameTest() throws ResponseException {
        Assertions.assertThrows(ResponseException.class,() -> {serverFacade.createGame("123", "game1");});
    }

    public static void clearDatabases() throws DataAccessException{
        new SqlAccessAuth().clearAuths();
        new SqlAccessGame().clearGames();
        new SqlAccessUser().clearUsers();
    }
}
