package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class SqlAccessGame implements GameAccess {

    public SqlAccessGame() throws DataAccessException{
        configureDatabase();
    }

    public void createGame(GameData game) throws DataAccessException{
        try (var conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "passCauseImLazy")) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) VALUES(?, ?)")) {
                preparedStatement.setInt(1, game.gameID());
                preparedStatement.setString(2, game.whiteUsername());
                preparedStatement.setString(3, game.blackUsername());
                preparedStatement.setString(4, game.gameName());
                preparedStatement.setString(5, new Gson().toJson(game.game()));

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public GameData getGame(int gameID) throws DataAccessException{
        try (var conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "passCauseImLazy")) {
            try (var preparedStatement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID=?")) {
                preparedStatement.setString(1, Integer.toString(gameID));
                try (var rs = preparedStatement.executeQuery()) {
                    return readgame(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteGame(GameData game){
        try (var conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "passCauseImLazy")) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM game WHERE gameID=?")) {
                preparedStatement.setString(1, Integer.toString(game.gameID()));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private GameData readgame(ResultSet rs) throws SQLException {
        int ID = rs.getInt("gameID");
        String white = rs.getString("whiteUsername");
        String black = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        ChessGame game = (ChessGame) rs.getObject("game");
        return new GameData(ID, white, black, gameName, game);
    }

    public Collection<ChessGame> listGame() throws DataAccessException{
        Collection<ChessGame> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID FROM game";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readgame(rs).game());
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()), 500);
        }
        return result;
    }

    public void clearGames(){
        try (var conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "passCauseImLazy")) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE game")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS game (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `game` ChessGame,
              PRIMARY KEY (`gameID`),
              INDEX(gameName)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()), 400);
        }
    }
}
