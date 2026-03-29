package exception;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {

    public enum Code {
        ServerError,
        ClientError,
    }

    final private Code code;

    public ResponseException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public static ResponseException fromJson(String json) {
        var map = new Gson().fromJson(json, HashMap.class);
        if (map.get("status") != null) {
            var status = Code.valueOf(map.get("status").toString());
            String message = map.get("message").toString();
            return new ResponseException(status, message);
        }
        return new ResponseException(Code.ServerError, "incorrect input");
    }

    public Code code() {
        return code;
    }

    public static Code fromHttpStatusCode(int httpStatusCode) {
        return switch (httpStatusCode) {
            case 500 -> Code.ServerError;
            case 400 -> Code.ClientError;
            default -> throw new IllegalArgumentException("Unknown HTTP status code: " + httpStatusCode);
        };
    }
}
