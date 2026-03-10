package dataaccess;

import model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

public class SqlAccessGame implements GameAccess {

    public SqlAccessGame() throws DataAccessException{
        configureDatabase();
    }

    void createGame(GameData game) throws DataAccessException{

    }

    GameData getGame(int gameID) throws DataAccessException{

    }

    void deleteGame(GameData game){

    }

    <T> Collection<T> listGame() throws DataAccessException{

    }

    void clearGames(){

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  pet (
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
