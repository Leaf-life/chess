package websocket;

import exception.ResponseException;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public interface ServerMessageHandler {
    void notificationMessage(NotificationMessage notification);

    void errorMessage(ErrorMessage notification);

    void loadGameMessage(LoadGameMessage notification) throws ResponseException;
}

