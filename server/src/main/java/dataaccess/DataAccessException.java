package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{
    private int statusCode = 500;
    public DataAccessException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    public DataAccessException(String message, Throwable ex) {
        super(message, ex);
    }

    public int getStatusCode() {
        return statusCode;
    }
}
