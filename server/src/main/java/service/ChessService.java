package service;

import chess.ChessGame;
import dataaccess.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collection;
import java.util.UUID;

import model.*;

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
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        useraccess.createUser(new UserData(user.username(), hashedPassword, user.email()));
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, user.username());
        authaccess.createAuth(auth);
        return auth;
    }

    public AuthData login(String username, String password) throws DataAccessException{
        if (username == null || password == null){
            throw new DataAccessException("bad request", 400);
        }
        UserData user = useraccess.getUser(username);
        if (user == null || !BCrypt.checkpw(password, user.password())){
            throw new DataAccessException("unauthorized", 401);
        }
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, username);
        authaccess.createAuth(auth);
        return auth;
    }

    public void logout(String authtoken) throws DataAccessException{
        checklogin(authtoken);
        authaccess.deleteAuth(authtoken);
    }

    public GameData createGame(CreateGameRequest create) throws DataAccessException{
        String authToken = create.Authorization();
        String gameName = create.gameName();
        checklogin(authToken);
        if (gameName == null){
            throw new DataAccessException("Bad request", 400);
        }
        GameData game = new GameData(0, null, null, gameName, new ChessGame());
        int iD = gameaccess.createGame(game);
        return new GameData(iD, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
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
            //gameaccess.updateGame(newGame);
        }else{
            if (game.whiteUsername() != null){
                throw new DataAccessException("Error: color already taken", 403);
            }
            GameData newGame = new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), new ChessGame());
            gameaccess.deleteGame(game);
            gameaccess.createGame(newGame);
            //gameaccess.updateGame(newGame);
        }
    }
    public void clear() throws DataAccessException{
        gameaccess.clearGames();
        useraccess.clearUsers();
        authaccess.clearAuths();
    }
}
