package dataaccess;

import model.*;
import org.junit.jupiter.api.*;
import passoff.model.TestUser;
import passoff.server.TestServerFacade;
import server.Server;

public class DOATests {
    @Test
    @Order(1)
    @DisplayName("Create Auth Positive")
    public void posCreateAuthTest() throws DataAccessException{
       AuthAccess accessauth = new SqlAccessAuth();
       accessauth.createAuth(new AuthData("123", "user"));
       AuthData auth = accessauth.getAuth("123");
       Assertions.assertEquals(new AuthData("123", "user"), auth);
    }
}
