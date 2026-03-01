package dataaccess;

import model.*;

public interface AuthAccess {

    void createAuth(AuthData authorization);

    AuthData getAuth(String authToken);

    void deleteAuth(String authToken);

    void clearAuths();

    String listAuths();
}
