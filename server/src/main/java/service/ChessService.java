package service;

import chess.ChessGame;
import model.*;
import dataaccess.*;

import java.util.Collection;

public class ChessService {

    private final UserAccess useraccess;
    private final GameAccess gameaccess;
    private final AuthAccess authaccess;
    private int nextAuthToken = 1;
    private int nextGameID = 1;

    public ChessService(UserAccess useraccess, GameAccess gameaccess, AuthAccess authaccess){
        this.useraccess = useraccess;
        this.gameaccess = gameaccess;
        this.authaccess = authaccess;
    }

    private void checklogin(String authtoken) throws DataAccessException{
        AuthData auth = authaccess.getAuth(authtoken);
        if (auth == null){
            throw new DataAccessException("Error: No Session Found", 401);
        }
    }

    public AuthData registration(UserData user) throws DataAccessException {
        UserData userCheck = useraccess.getUser(user.email());
        if (userCheck != null){
            throw new DataAccessException("user already registered", 403);
        }
        useraccess.createUser(user);
        AuthData auth = new AuthData(Integer.toString(nextAuthToken), user.username());
        nextAuthToken++;
        authaccess.createAuth(auth);
        return auth;
    }

    public AuthData login(String username, String password) throws DataAccessException{
        UserData user = useraccess.getUser(username);
        if (user == null){
            throw new DataAccessException("user name is incorrect", 400);
        }
        if (!(user.password().equals(password))){
            throw new DataAccessException("password is incorrect", 401);
        }
        AuthData auth = new AuthData(Integer.toString(nextAuthToken), username);
        nextAuthToken++;
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
        GameData game = new GameData(nextGameID, auth.username(), null, gameName, new ChessGame());
        nextGameID++;
        gameaccess.createGame(game);
        return game;
    }

    public Collection<GameData> listGame(String authtoken) throws DataAccessException{
        checklogin(authtoken);
        return gameaccess.listGame();
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws DataAccessException{
        checklogin(authToken);
        AuthData auth = authaccess.getAuth(authToken);
        GameData game = gameaccess.getGame(gameID);
        if (game == null || playerColor == null){
            throw new DataAccessException("Error: bad game request", 400);
        }
        if (playerColor.equals("white")){
            if (game.whiteUsername() != null){
                throw new DataAccessException("Error: color already taken", 403);
            }
            GameData newGame = new GameData(nextGameID, auth.username(), null, game.gameName(), new ChessGame());
            nextGameID++;
            gameaccess.createGame(game);
        }else{
            if (game.blackUsername() != null){
                throw new DataAccessException("Error: color already taken", 400);
            }
            GameData newGame = new GameData(nextGameID, auth.username(), null, game.gameName(), new ChessGame());
            nextGameID++;
            gameaccess.createGame(game);
        }
    }
    public void clear(){
        gameaccess.clearGames();
        useraccess.clearUsers();
        authaccess.clearAuths();
    }
}
