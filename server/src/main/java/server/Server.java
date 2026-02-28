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
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.post("/user/{user}", this::registration);
        javalin.post("/user/{user}", this::login);
        javalin.delete("/user/{user}", this::logout);
        javalin.get("/user", this::listGames);
        javalin.post("", this::)
        // Register your endpoints and exception handlers here.

    }

    private AuthData registration(@NotNull Context context) throws DataAccessException {
        UserData reg = new Gson().fromJson(context.body(), UserData.class);
        return service.registration(reg);
    }

    private void login(@NotNull Context context){}

    private void logout(@NotNull Context context){}

    private void listGames(@NotNull Context context){}

    private

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
