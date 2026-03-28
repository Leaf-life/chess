package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.ResponseException;
import java.net.http.HttpClient;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import model.*;
public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public AuthData registration(UserData user) throws ResponseException{
        HttpRequest.Builder requestBuild = buildRequest("POST", "/user", user);
        var request = requestBuild.build();
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(String username, String password) throws ResponseException{
        HttpRequest.Builder requestBuild = buildRequest("POST", "/session", new LoginRequest(username, password));
        var request = requestBuild.build();
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public void logout(String authToken) throws ResponseException{
        HttpRequest.Builder requestBuild = buildRequest("DELETE", "/session", authToken);
        requestBuild.setHeader("Authorization", authToken);
        var request = requestBuild.build();
        sendRequest(request);
    }

    public Collection<GameData> listGames(String authToken) throws ResponseException{
        HttpRequest.Builder requestBuild = buildRequest("GET", "/game", authToken);
        requestBuild.setHeader("Authorization", authToken);
        var request = requestBuild.build();
        var response = sendRequest(request);
        return handleResponse(response, GameList.class);
    }

    public GameData createGame(String authToken, String gameName) throws ResponseException{
        HttpRequest.Builder requestBuild = buildRequest("POST", "/game", new CreateGameRequest(authToken, gameName));
        requestBuild.setHeader("Authorization", authToken);
        var request = requestBuild.build();
        var response = sendRequest(request);
        return handleResponse(response, GameData.class);
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws ResponseException{
        HttpRequest.Builder requestBuild = buildRequest("PUT", "/game", new JoinGameRquest(authToken, playerColor, gameID));
        requestBuild.setHeader("Authorization", authToken);
        var request = requestBuild.build();
        sendRequest(request);
    }

    public void observeGame(String authToken, int gameID) throws ResponseException{
        HttpRequest.Builder requestBuild = buildRequest("PUT", "/game", null);
        requestBuild.setHeader("Authorization", authToken);
        var request = requestBuild.build();
        sendRequest(request);
    }

    private HttpRequest.Builder buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request;
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException{
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            if (responseClass == GameList.class){
                JsonObject obj = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonArray arr = obj.getAsJsonArray("games");

                return new Gson().fromJson(arr, responseClass);
            }
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
