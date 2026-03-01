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

    public Server() {
        this.service = new ChessService(new AccessUser(), new AccessGame(), new AccessAuth());
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
            .post("/user", this::registration)
            .post("/session", this::login)
            .delete("/session", this::logout)
            .get("/game", this::listGames)
            .post("/game", this::createGame)
            .put("/game", this::joinGame)
            .delete("/db", this::clear)
            .exception(DataAccessException.class, this::exceptionHandler)
            .exception(BadRequest.class, (e, config) -> config.status(400))
            .exception(UnAthorized.class, (e, config) -> config.status(401))
            .exception(AlreadyTaken.class, (e, config) -> config.status(403));
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
        context.result(new Gson().toJson(auth));
    }

    private void logout(@NotNull Context context) throws DataAccessException{
        String authToken = context.header("Authorization");
        service.logout(authToken);
    }

    private void listGames(@NotNull Context context) throws DataAccessException{
        String authToken = context.header("Authorization");
        Collection<GameData> games = service.listGame(authToken);
        context.result(new Gson().toJson(games.toString()));
    }

    private void createGame(@NotNull Context context) throws DataAccessException{
        String authToken = context.header("Authorization");
        String gameName = new Gson().toJson(context.body(), CreateGameRequest.class);
        GameData game = service.createGame(authToken, gameName);
        context.result(new Gson().toJson(game.toString()));
    }

    private void joinGame(@NotNull Context context){}

    private void clear(@NotNull Context context){
        service.clear();
    }

    private void exceptionHandler(Exception e, Context context) throws Throwable {
        if (e.getCause() != null) {
            throw e.getCause();
        }

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
