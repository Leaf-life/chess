package client;

import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
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


    //@Test
    //public void sampleTest() {
       // Assertions.assertTrue(true);
    //}

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
    @Order(2)
    @DisplayName("Logout Pos")
    public void posLogoutTest() throws ResponseException {
        AuthData auth = serverFacade.registration(new UserData("user", "pass", "email@123"));
        serverFacade.logout(auth.authToken());
        Assertions.assertThrows(ResponseException.class,() -> {serverFacade.createGame(auth.authToken(), "game1");});
    }

    public static void clearDatabases() throws DataAccessException{
        new SqlAccessAuth().clearAuths();
        new SqlAccessGame().clearGames();
        new SqlAccessUser().clearUsers();
    }
}
