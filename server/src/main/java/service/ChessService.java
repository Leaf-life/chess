package service;

import chess.ChessGame;
import model.*;
import dataaccess.*;

import java.util.Collection;
import java.util.UUID;

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
        if (user.username() == null || user.password() == null || user.email() == null){
            throw new DataAccessException("bad request", 400);
        }
        UserData userCheck = useraccess.getUser(user.username());
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
        if (username == null || password == null){
            throw new DataAccessException("bad request", 400);
        }
        UserData user = useraccess.getUser(username);
        if (user == null || !(user.password().equals(password))){
            throw new DataAccessException("unauthorized", 401);
        }
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, username);
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
        if (gameName == null){
            throw new DataAccessException("Bad request", 400);
        }
        AuthData auth = authaccess.getAuth(authToken);
        GameData game = new GameData(nextGameID, null, null, gameName, new ChessGame());
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
        if (game == null || playerColor == null || !(playerColor.equals("WHITE") || playerColor.equals("BLACK"))){
            throw new DataAccessException("Error: bad game request", 400);
        }
        if (playerColor.equals("BLACK")){
            if (game.blackUsername() != null){
                throw new DataAccessException("Error: color already taken", 403);
            }
            GameData newGame = new GameData(game.gameID(), game.whiteUsername(), auth.username(), game.gameName(), new ChessGame());
            gameaccess.deleteGame(game);
            gameaccess.createGame(newGame);
        }else{
            if (game.whiteUsername() != null){
                throw new DataAccessException("Error: color already taken", 403);
            }
            GameData newGame = new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), new ChessGame());
            gameaccess.deleteGame(game);
            gameaccess.createGame(newGame);
        }
    }
    public void clear(){
        gameaccess.clearGames();
        useraccess.clearUsers();
        authaccess.clearAuths();
    }
}
