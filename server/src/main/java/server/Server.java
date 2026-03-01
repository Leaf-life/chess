package server;

import handlers.RegistrationHandler;
import chess.ChessGame;
import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import service.*;
import dataaccess.*;
import org.jetbrains.annotations.NotNull;

public class Server {

    private final ChessService service;
    private final Javalin javalin;

    public Server() {
        this.service = new ChessService(new AccessUser(), new AccessGame(), new AccessAuth());
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
            .post("/user/{user}", this::registration)
            .post("/session/{session}", this::login)
            .delete("/session/{session}", this::logout)
            .get("/game", this::listGames)
            .post("/game/{game}", this::createGame)
            .put("/game", this::joinGame)
            .delete("/db", this::clear);
        // Register your endpoints and exception handlers here.

    }

    private void registration(@NotNull Context context) throws DataAccessException {
        UserData reg = new Gson().fromJson(context.body(), UserData.class);
        AuthData auth = service.registration(reg);
        context.result(new Gson().toJson(auth));
    }

    private void login(@NotNull Context context){}

    private void logout(@NotNull Context context){}

    private void listGames(@NotNull Context context){}

    private void createGame(@NotNull Context context){}

    private void joinGame(@NotNull Context context){}

    private void clear(@NotNull Context context){}

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
