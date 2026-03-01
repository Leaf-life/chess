package dataaccess;

import model.*;

public interface UserAccess {

    void createUser(UserData user);

    UserData getUser(String Username) throws DataAccessException;

    void clearUsers();

    String listUsers();
}
