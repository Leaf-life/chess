package dataaccess;

import model.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SqlAccessAuth implements AuthAccess{

    public SqlAccessAuth() throws DataAccessException{
        configureDatabase();
    }

    public void createAuth(AuthData authorization){
        try (var conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "passCauseImLazy")) {
            if (authorization.authToken().matches("[a-zA-z0-9]+") && authorization.username().matches("[a-zA-z]+")) {
                try (var preparedStatement = conn.prepareStatement("INSERT INTO auth (authTocken, username) VALUES(?, ?)")) {
                    preparedStatement.setString(1, authorization.authToken());
                    preparedStatement.setString(2, authorization.username());

                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public AuthData getAuth(String authToken){
        try (var conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "passCauseImLazy")) {
            try (var preparedStatement = conn.prepareStatement("SELECT authToken, username FROM auth WHERE authTocken=?")) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                   String token = rs.getString("authToken");
                   String name = rs.getString("username");
                   return (new AuthData(token, name));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAuth(String authToken){
        try (var conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "passCauseImLazy")) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM auth WHERE authToken=?")) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearAuths(){
        try (var conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "passCauseImLazy")) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE auth")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String listAuths(){
        return null;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
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
