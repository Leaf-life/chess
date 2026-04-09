package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import websocket.ConnectionManager;
import websocket.commands.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import service.ChessService;
import server.Server;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Server server;
    ChessService service;

    public WebSocketHandler(ChessService service, Server server) {
        this.service = service;
        this.server = server;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) throws IOException {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        int gameId = -1;
        Session session = ctx.session;

        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            gameId = command.getGameID();
            String username = getUsername(command.getAuthToken());
            saveSession(gameId, session);

            switch (command.getCommandType()) {
                case UserGameCommand.CommandType.CONNECT -> connect(ctx.session, username, ctx, gameId);
                case UserGameCommand.CommandType.MAKE_MOVE -> makeMove(ctx.session, username, (MakeMoveCommand) command);
                case UserGameCommand.CommandType.LEAVE -> leave(ctx.session, username, (LeaveCommand) command);
                case UserGameCommand.CommandType.RESIGN -> resign(ctx.session, username, (ResignCommand) command);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private String getUsername(String authToken){

        return "";
    }

    private ChessGame getGame(Session session, String authToken, int gameID) throws IOException {
        try{
            return server.getGame(authToken, gameID);
        } catch (Exception e) {
            return null;
        }
    }

    private void saveSession(int gameID, Session session){
        connections.add(session);
    }

    private void connect(Session session, String authTocken, WsMessageContext ctx, int gameID) throws IOException{
        ConnectCommand command = new Gson().fromJson(ctx.message(), ConnectCommand.class);
        ChessGame game = getGame(session, authTocken, gameID);
        if (game == null){
            String message = "bad game ID";
            var notification = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            connections.broadcast(session, notification, message);
        }
        else {
            var message = String.format("Player has joined");
            var notification = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameID);
            connections.broadcast(session, notification, message);
        }

    }

    private void makeMove(Session session, String authTocken, MakeMoveCommand command) throws IOException{
        var message = String.format("Player made move");
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        connections.broadcast(session, notification, message);

    }

    private void leave(Session session, String authTocken, LeaveCommand command) throws IOException{
        var message = String.format("Player has left");
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        connections.broadcast(session, notification, message);
        connections.remove(session);
    }

    private void resign(Session session, String authTocken, ResignCommand command) throws IOException{
        var message = String.format("Player has resigned");
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        connections.broadcast(session, notification, message);
        connections.remove(session);
    }

    public void makeNoise(String petName, String sound) throws ResponseException {
        try {
            var message = String.format("%s says %s", petName, sound);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            connections.broadcast(null, notification, message);
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }
}