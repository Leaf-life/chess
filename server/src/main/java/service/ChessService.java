package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
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

    public void checklogin(String authtoken) throws DataAccessException{
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
        String authToken = create.authToken();
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
            //gameaccess.deleteGame(game);
            //gameaccess.createGame(newGame);
            gameaccess.updateGame(newGame);
        }else{
            if (game.whiteUsername() != null){
                throw new DataAccessException("Error: color already taken", 403);
            }
            GameData newGame = new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), new ChessGame());
            //gameaccess.deleteGame(game);
            //gameaccess.createGame(newGame);
            gameaccess.updateGame(newGame);
        }
    }

    public void makeMove(String authToken, int gameID) throws DataAccessException{
        checklogin(authToken);
    }

    public ChessGame getGame(String authToken, int gameID) throws DataAccessException{
        checklogin(authToken);
        return gameaccess.getGame(gameID).game();
    }

    public void checkGameID(String authToken, int gameID) throws DataAccessException {
        checklogin(authToken);
        GameData game = gameaccess.getGame(gameID);
        if (game == null){
            throw new DataAccessException("Error: No Game Found", 401);
        }
    }

    public void checkMove(String authToken, int gameID, ChessMove move) throws DataAccessException {
        checklogin(authToken);
        GameData gameData = gameaccess.getGame(gameID);
        AuthData authData = authaccess.getAuth(authToken);
        ChessGame game = gameData.game();
        ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());
        ChessGame.TeamColor color = ChessGame.TeamColor.BLACK;
        ChessGame.TeamColor oppisteColor = ChessGame.TeamColor.WHITE;
        if (gameData.whiteUsername().equals(authData.username())){
            color = ChessGame.TeamColor.WHITE;
            oppisteColor = ChessGame.TeamColor.BLACK;
        }
        if (game.isInCheck(color) || game.isInCheck(oppisteColor)){
            return;
        }
        if (!piece.getTeamColor().equals(color) || game.getTeamTurn() != color){
            throw new DataAccessException("Error: moved out of turn", 400);
        }
        Collection<ChessMove> possibleMoves = piece.pieceMoves(game.getBoard(), move.getStartPosition());
        for (ChessMove m: possibleMoves){
            if (m.equals(move)){
                return;
            }
        }
        throw new DataAccessException("Error: invalid move", 400);
    }

    public void checkPlayer(String authToken, int gameID) throws DataAccessException {
        checklogin(authToken);
        GameData gameData = gameaccess.getGame(gameID);
        AuthData authData = authaccess.getAuth(authToken);
        if (!(gameData.whiteUsername().equals(authData.username()) || gameData.blackUsername().equals(authData.username()))){
            throw new DataAccessException("Error: not in the game", 400);
        }
    }

    public boolean checkResigned(String authToken, int gameID) throws DataAccessException {
        checklogin(authToken);
        GameData gameData = gameaccess.getGame(gameID);
        ChessGame game = gameData.game();
        return game.isResigned();
    }

    public void setResigned(String authToken, int gameID) throws DataAccessException {
        checklogin(authToken);
        GameData gameData = gameaccess.getGame(gameID);
        ChessGame game = gameData.game();
        game.setResigned();
    }

    public void clear() throws DataAccessException{
        gameaccess.clearGames();
        useraccess.clearUsers();
        authaccess.clearAuths();
    }
}
