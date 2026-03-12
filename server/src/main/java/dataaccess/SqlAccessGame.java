package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class SqlAccessGame implements GameAccess {

    public SqlAccessGame(){
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void updateGame(GameData game) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?", Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, game.whiteUsername());
                preparedStatement.setString(2, game.blackUsername());
                preparedStatement.setString(3, game.gameName());
                preparedStatement.setString(4, new Gson().toJson(game.game()));
                preparedStatement.setInt(5, game.gameID());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), 500);
        }
    }

    public int createGame(GameData game) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(true);
            try (var preparedStatement = conn.prepareStatement("INSERT INTO game (whiteUsername, blackUsername, gameName, game) VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, game.whiteUsername());
                preparedStatement.setString(2, game.blackUsername());
                preparedStatement.setString(3, game.gameName());
                preparedStatement.setString(4, new Gson().toJson(game.game()));

                preparedStatement.executeUpdate();

                ResultSet rs = preparedStatement.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                throw new DataAccessException("", 500);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), 500);
        }
    }

    public GameData getGame(int gameID) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID=?")) {
                preparedStatement.setString(1, Integer.toString(gameID));
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return readgame(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void deleteGame(GameData game){
        try (var conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(true);
            try (var preparedStatement = conn.prepareStatement("DELETE FROM game WHERE gameID=?")) {
                preparedStatement.setString(1, Integer.toString(game.gameID()));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private GameData readgame(ResultSet rs) throws SQLException {
        int iD = rs.getInt("gameID");
        String white = rs.getString("whiteUsername");
        String black = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        ChessGame game = new Gson().fromJson(rs.getString("game"), ChessGame.class);
        return new GameData(iD, white, black, gameName, game);
    }

    public Collection<GameData> listGame() throws DataAccessException{
        Collection<GameData> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readgame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()), 500);
        }
        return result;
    }

    public void clearGames() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE game")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("" + e.getMessage(), 500);
        }
    }


    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS game (
              `gameID` INT AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `game` JSON,
              PRIMARY KEY (`gameID`),
              INDEX(gameName)
            ) AUTO_INCREMENT = 1, ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
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
