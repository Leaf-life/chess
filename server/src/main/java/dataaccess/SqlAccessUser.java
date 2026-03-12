package dataaccess;

import chess.ChessGame;
import model.*;

import java.sql.*;

public class SqlAccessUser implements UserAccess {

    public SqlAccessUser(){
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void createUser(UserData user){
        try (var conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(true);
            try (var preparedStatement = conn.prepareStatement("INSERT INTO user (username, password, email) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, user.username());
                preparedStatement.setString(2, user.password());
                preparedStatement.setString(3, user.email());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public UserData getUser(String username) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT username, password, email FROM user WHERE username=?")) {
                preparedStatement.setString(1, username);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return readuser(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void clearUsers() throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(true);
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE user")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage(), 401);
        }
    }

    private UserData readuser(ResultSet rs) throws SQLException {
        String user = rs.getString("username");
        String pass = rs.getString("password");
        String email = rs.getString("email");
        return new UserData(user, pass, email);
    }

    public String listUsers(){
        return null;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user (
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
