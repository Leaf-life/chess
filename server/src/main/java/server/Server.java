package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import service.*;
import dataaccess.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public class Server {

    private final ChessService service;
    private final Javalin javalin;

    public Server(){
        this(new ChessService(new SqlAccessUser(), new SqlAccessGame(), new SqlAccessAuth()));
    }

    public Server(ChessService service) {
        this.service = service;
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
            .post("/user", this::registration)
            .post("/session", this::login)
            .delete("/session", this::logout)
            .get("/game", this::listGames)
            .post("/game", this::createGame)
            .put("/game", this::joinGame)
            .delete("/db", this::clear)
            .exception(DataAccessException.class, this::exceptionHandler);
        // Register your endpoints and exception handlers here.

    }

    private void registration(@NotNull Context context) throws DataAccessException {
        UserData reg = new Gson().fromJson(context.body(), UserData.class);
        AuthData auth = service.registration(reg);
        context.result(new Gson().toJson(auth));
    }

    private void login(@NotNull Context context) throws DataAccessException{
        LoginRequest reg = new Gson().fromJson(context.body(), LoginRequest.class);
        AuthData auth = service.login(reg.username(), reg.password());
        var serializer = new Gson();
        var json = serializer.toJson(auth);
        context.result(json);
    }

    private void logout(@NotNull Context context) throws DataAccessException{
        String authToken = context.header("Authorization");
        service.logout(authToken);
    }

    private void listGames(@NotNull Context context) throws DataAccessException{
        String authToken = context.header("Authorization");
        Collection<GameData> games = service.listGame(authToken);
        var serializer = new Gson();
        var json = serializer.toJson(Map.of("games", games));
        context.result(json);
    }

    private void createGame(@NotNull Context context) throws DataAccessException{
        Gson gson = new Gson();
        String authToken = context.header("Authorization");
        CreateGameRequest gameName = gson.fromJson(context.body(), CreateGameRequest.class);
        GameData game = service.createGame(authToken, gameName.gameName());
        var serializer = new Gson();
        var json = serializer.toJson(game);
        context.json(json);
    }

    private void joinGame(@NotNull Context context) throws DataAccessException{
        String authToken = context.header("Authorization");
        JoinGameRquest joinrequest = new Gson().fromJson(context.body(), JoinGameRquest.class);
        service.joinGame(authToken, joinrequest.playerColor(), joinrequest.gameID());
    }

    private void clear(@NotNull Context context){
        service.clear();
        context.status(200);
    }

    private void exceptionHandler(DataAccessException e, Context context){
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        context.status(e.getStatusCode());
        context.json(body);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
