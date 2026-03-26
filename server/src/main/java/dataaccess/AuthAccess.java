package dataaccess;

import model.*;
public interface AuthAccess {

    void createAuth(AuthData authorization);

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken);

    void clearAuths() throws DataAccessException;

    String listAuths();
}
