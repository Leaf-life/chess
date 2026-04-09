package websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
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
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import service.ChessService;
import server.Server;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Server server;
    ChessService service;
    private boolean observer = false;

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
    public void handleMessage(WsMessageContext ctx) throws IOException {
        int gameId = -1;
        Session session = ctx.session;

        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            gameId = command.getGameID();
            String username = getUsername(command.getAuthToken());
            service.checklogin(command.getAuthToken());
            service.checkGameID(command.getAuthToken(),gameId);
            service.isObserver(command.getAuthToken(), gameId);
            saveSession(gameId, session);

            switch (command.getCommandType()) {
                case UserGameCommand.CommandType.CONNECT -> connect(ctx.session, username, ctx, gameId);
                case UserGameCommand.CommandType.MAKE_MOVE -> makeMove(ctx.session, username, ctx, gameId);
                case UserGameCommand.CommandType.LEAVE -> leave(ctx.session, username, ctx, gameId);
                case UserGameCommand.CommandType.RESIGN -> resign(ctx.session, username, ctx, gameId);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (DataAccessException e) {
            var notification = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            connections.send(session, notification, e.getMessage());
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private String getUsername(String authToken) throws DataAccessException {
        return service.getUsername(authToken);
    }

    private boolean isObserver(String authtoken, int gameID) throws DataAccessException {
        return service.isObserver(authtoken, gameID);
    }

    private ChessGame getGame(Session session, String authToken, int gameID) throws IOException {
        try{
            return server.getGame(authToken, gameID);
        } catch (Exception e) {
            return null;
        }
    }

    private void saveSession(int gameID, Session session){
        connections.add(gameID, session);
    }

    private void connect(Session session, String username, WsMessageContext ctx, int gameID) throws IOException, DataAccessException {
         if (!observer) {
             ConnectCommand command = new Gson().fromJson(ctx.message(), ConnectCommand.class);
             var message = String.format("Player: %s has joined", username);
             var loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, service.getGame(command.getAuthToken(), gameID));
             var notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
             connections.broadcast(gameID, session, notificationMessage, message);
             connections.send(session, loadGameMessage, message);
         }

    }

    private void makeMove(Session session, String username, WsMessageContext ctx, int gameID) throws IOException{
        MakeMoveCommand makeMoveCommand = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
        try {
            if(!service.checkResigned(makeMoveCommand.getAuthToken(), gameID)) {
                service.checkMove(makeMoveCommand.getAuthToken(), gameID, makeMoveCommand.getMove());
                service.makeMove(makeMoveCommand.getAuthToken(), gameID, makeMoveCommand.getMove());
                ChessGame game = service.getGame(makeMoveCommand.getAuthToken(), gameID);
                boolean isCheckMate = service.isCheckMate(makeMoveCommand.getAuthToken(), gameID);
                var message = String.format("Player: %s made move", username);
                var loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
                var notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.broadcast(gameID, session, loadGameMessage, message);
                connections.broadcast(gameID, session, notificationMessage, message);
                connections.send(session, loadGameMessage, message);
                if (isCheckMate){
                    message = String.format("Finish");
                    notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                    connections.send(session, notificationMessage, message);
                    connections.broadcast(gameID, session, notificationMessage, message);
                }
            }else {
                var message = String.format("%s resigned", username);
                var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
                connections.send(session, errorMessage, message);
            }
        } catch (DataAccessException | InvalidMoveException e) {
            var notification = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            connections.send(session, notification, e.getMessage());
        }
    }

    private void leave(Session session, String username, WsMessageContext ctx, int gameID) throws IOException, DataAccessException {
            LeaveCommand leaveCommand = new Gson().fromJson(ctx.message(), LeaveCommand.class);
            var message = String.format("Player: %s has left the game", username);
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            service.leaveGame(leaveCommand.getAuthToken(), gameID);
            connections.broadcast(gameID, session, notification, message);
            connections.remove(gameID, session);
    }

    private void resign(Session session, String username, WsMessageContext ctx, int gameID) throws IOException{
        var message = String.format("Player: %s has resigned", username);
       ResignCommand resignCommand = new Gson().fromJson(ctx.message(), ResignCommand.class);
        try {
            if (!service.checkResigned(resignCommand.getAuthToken(), gameID)) {
                service.setResigned(resignCommand.getAuthToken(), gameID);
                var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                service.checkPlayer(resignCommand.getAuthToken(), gameID);
                connections.broadcast(gameID, session, notification, message);
                connections.send(session, notification, message);
            } else {
                message = String.format("%s resigned", username);
                var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
                connections.send(session, errorMessage, message);
            }
        } catch (DataAccessException e) {
            var notification = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            connections.send(session, notification, e.getMessage());
        }
    }
    /*
    public void makeNoise(String petName, String sound) throws ResponseException {
        try {
            var message = String.format("%s says %s", petName, sound);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            connections.broadcast( notification, message);
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }
     */
}