package service;

import model.*;
import dataaccess.*;

public class ChessService {

    private final UserAccess useraccess;
    private final GameAccess gameaccess;
    private final AuthAccess authaccess;

    public ChessService(UserAccess useraccess, GameAccess gameaccess, AuthAccess authaccess){
        this.useraccess = useraccess;
        this.gameaccess = gameaccess;
        this.authaccess = authaccess;
    }

    public AuthData registration(UserData user) throws DataAccessException {
        UserData userCheck = useraccess.getUser(user.email());
        if (userCheck == null){
            throw new DataAccessException("user already registered");
        }
        useraccess.createUser(user);
        AuthData auth = new AuthData("123", user.email());
        authaccess.createAuth(auth);
        return auth;
    }
}
