package handlers;

import dataaccess.AccessAuth;
import dataaccess.AccessUser;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import server.Server;

public class RegistrationHandler {
    AccessUser users = new AccessUser();
    AccessAuth auths = new AccessAuth();

    public RegistrationHandler(){}

    public UserData registration(UserData user) throws DataAccessException {
        UserData userdata = users.getUser(user.username());
        if (userdata != null){
            throw new DataAccessException(" \"message\": \"Error: already taken\" ");
        }
        users.createUser(userdata);
        auths.createAuth(new AuthData("123", user.username()));
        return userdata;
    }
}
