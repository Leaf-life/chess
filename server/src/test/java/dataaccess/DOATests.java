package dataaccess;

import model.*;

public class DOATests {

    public void posCreateAuthTest() throws DataAccessException{
       AuthAccess accessauth = new SqlAccessAuth();
       accessauth.createAuth(new AuthData("123", "user"));
    }
}
