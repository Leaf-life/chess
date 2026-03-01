package service;

import chess.ChessGame;
import model.*;
import dataaccess.*;

import java.util.Collection;

public class ChessService {

    private final UserAccess useraccess;
    private final GameAccess gameaccess;
    private final AuthAccess authaccess;

    public ChessService(UserAccess useraccess, GameAccess gameaccess, AuthAccess authaccess){
        this.useraccess = useraccess;
        this.gameaccess = gameaccess;
        this.authaccess = authaccess;
    }

    private void checklogin(String authtoken) throws DataAccessException{
        AuthData auth = authaccess.getAuth(authtoken);
        if (auth == null){
            throw new DataAccessException("Error: No Session Found");
        }
    }

    public AuthData registration(UserData user) throws DataAccessException {
        UserData userCheck = useraccess.getUser(user.email());
        if (userCheck != null){
            throw new DataAccessException("user already registered");
        }
        useraccess.createUser(user);
        AuthData auth = new AuthData("123", user.username());
        authaccess.createAuth(auth);
        return auth;
    }

    public AuthData login(String username, String password) throws DataAccessException{
        UserData user = useraccess.getUser(username);
        if (user == null){
            throw new DataAccessException("user name is incorrect");
        }
        if (!user.password().equals(password)){
            throw new DataAccessException("password is incorrect");
        }
        AuthData auth = new AuthData("123", username);
        authaccess.createAuth(auth);
        return auth;
    }

    public void logout(String authtoken) throws DataAccessException{
        checklogin(authtoken);
        authaccess.deleteAuth(authtoken);
    }

    public GameData createGame(String authToken, String gameName) throws DataAccessException{
        checklogin(authToken);
        AuthData auth = authaccess.getAuth(authToken);
        GameData game = new GameData(123, auth.username(), null, gameName, new ChessGame());
        gameaccess.createGame(game);
        return game;
    }

    public Collection<GameData> listGame(String authtoken) throws DataAccessException{
        checklogin(authtoken);
        return gameaccess.listGame();
    }
}
