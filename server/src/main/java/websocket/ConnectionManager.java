package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final Map<Integer, HashSet<Session>> connections = new HashMap<>();

    public void add(int gameID, Session session) {
        if (!connections.containsKey(gameID)){
            connections.put(gameID, new HashSet<>());
        }
        connections.get(gameID).add(session);
    }

    public void remove(int gameID, Session session) {
        connections.get(gameID).remove(session);
    }

    public void send(Session session, ServerMessage notification, String message) throws IOException {
        Gson gson = new Gson();
        String msg = gson.toJson(notification);
        session.getRemote().sendString(msg);
    }

    public void broadcast(int gameID, Session excludeSession, ServerMessage notification, String message) throws IOException {
        Gson gson = new Gson();
        String msg = gson.toJson(notification);
        for (Session c : connections.get(gameID)) {
            if (c.isOpen()) {
                if(!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}
