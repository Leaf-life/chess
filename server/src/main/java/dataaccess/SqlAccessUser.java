package dataaccess;

import model.*;

import java.sql.Connection;
import java.sql.SQLException;

public class SqlAccessUser implements UserAccess {

    public SqlAccessUser() throws DataAccessException{
        configureDatabase();
    }

    public void createUser(UserData user){

    }

    public UserData getUser(String username) throws DataAccessException{
        return null;
    }

    public void clearUsers(){

    }

    public String listUsers(){
        return null;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  pet (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(username)
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
