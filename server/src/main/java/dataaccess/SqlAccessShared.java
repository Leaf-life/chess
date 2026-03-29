package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;

public class SqlAccessShared {
    private final String[] createStatements;

    public SqlAccessShared(String[] createStatements){
        this.createStatements = createStatements;
    }

    public void configureDatabase() throws DataAccessException {
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
