package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
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

    public boolean isObserver(String authToken, int gameID) throws DataAccessException {
        checklogin(authToken);
        String username = authaccess.getAuth(authToken).username();
        GameData gameData = gameaccess.getGame(gameID);
        if (gameData.whiteUsername() != null){
            if (gameData.whiteUsername().equals(username)){
                return false;
            }
        }
        if (gameData.blackUsername() != null) {
            if (gameData.blackUsername().equals(username)) {
                return false;
            }
        }
        return true;
    }

    public String getUsername(String authToken) throws DataAccessException{
        checklogin(authToken);
        return authaccess.getAuth(authToken).username();
    }

    public GameData getGame(String authToken, int gameID) throws DataAccessException{
        checklogin(authToken);
        return gameaccess.getGame(gameID);
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
        ChessGame.TeamColor color = null;
        if (gameData.whiteUsername() == null || gameData.blackUsername() == null){
            throw new DataAccessException("Needs other player before game can start", 400);
        }
        if (gameData.whiteUsername().equals(authData.username())){
            color = ChessGame.TeamColor.WHITE;
        } else if (gameData.blackUsername().equals(authData.username())) {
            color = ChessGame.TeamColor.BLACK;
        } else{
            throw new DataAccessException("You are an observer you are not allowed to move pieces", 400);
        }
        if (game.isInCheck(ChessGame.TeamColor.WHITE) || game.isInCheck(ChessGame.TeamColor.BLACK)){
            return;
        }
        if (!piece.getTeamColor().equals(color)){
            throw new DataAccessException("Error: not your piece", 400);
        }
        if (game.getTeamTurn() != color){
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

    public void makeMove(String authToken, int gameID, ChessMove move) throws DataAccessException, InvalidMoveException {
        try {
            checklogin(authToken);
            GameData gameData = gameaccess.getGame(gameID);
            AuthData authData = authaccess.getAuth(authToken);
            ChessGame game = gameData.game();
            game.makeMove(move);
            gameaccess.updateGame(new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));
        } catch (Exception e){
            throw new InvalidMoveException("bad Move");
        }
    }

    public boolean isCheckMate(String authToken, int gameID) throws DataAccessException {
        checklogin(authToken);
        GameData gameData = gameaccess.getGame(gameID);
        ChessGame game = gameData.game();
        if  (game.isInCheckmate(ChessGame.TeamColor.WHITE) || game.isInCheckmate(ChessGame.TeamColor.BLACK)){
            return true;
        }
        return false;
    }

    public boolean isCheck(String authToken, int gameID) throws DataAccessException {
        checklogin(authToken);
        GameData gameData = gameaccess.getGame(gameID);
        ChessGame game = gameData.game();
        if  (game.isInCheck(ChessGame.TeamColor.WHITE) || game.isInCheck(ChessGame.TeamColor.BLACK)){
            return true;
        }
        return false;
    }

    public boolean isStalemate(String authToken, int gameID) throws DataAccessException {
        checklogin(authToken);
        GameData gameData = gameaccess.getGame(gameID);
        ChessGame game = gameData.game();
        if  (game.isInStalemate(ChessGame.TeamColor.WHITE) || game.isInStalemate(ChessGame.TeamColor.BLACK)){
            return true;
        }
        return false;
    }

    public void checkPlayer(String authToken, int gameID) throws DataAccessException {
        checklogin(authToken);
        GameData gameData = gameaccess.getGame(gameID);
        AuthData authData = authaccess.getAuth(authToken);
        if (!(gameData.whiteUsername().equals(authData.username()) || gameData.blackUsername().equals(authData.username()))){
            throw new DataAccessException("Error: not in the game", 400);
        }
    }

    public void leaveGame(String authToken, int gameID) throws DataAccessException {
        checklogin(authToken);
        GameData gameData = gameaccess.getGame(gameID);
        AuthData authData = authaccess.getAuth(authToken);
        if (gameData.whiteUsername() == null || gameData.blackUsername() == null){
            return;
        }
        if (gameData.whiteUsername().equals(authData.username())){
            gameaccess.updateGame(new GameData(gameID, null, gameData.blackUsername(), gameData.gameName(), gameData.game()));
        } else if (gameData.blackUsername().equals(authData.username())) {
            gameaccess.updateGame(new GameData(gameID, gameData.blackUsername(), null, gameData.gameName(), gameData.game()));
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
        gameaccess.updateGame(new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));
    }

    public void clear() throws DataAccessException{
        gameaccess.clearGames();
        useraccess.clearUsers();
        authaccess.clearAuths();
    }
}
