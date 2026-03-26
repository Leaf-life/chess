package dataaccess;

import model.*;
public interface UserAccess {

    void createUser(UserData user);

    UserData getUser(String username) throws DataAccessException;

    void clearUsers() throws DataAccessException;

    String listUsers();
}
