package dataaccess;

public class UnAthorized extends RuntimeException {
    public UnAthorized(String message) {
        super(message);
    }
}
