package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Session, Session> connections = new ConcurrentHashMap<>();

    public void add(Session session) {
        connections.put(session, session);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void send(Session session, ServerMessage notification, String message) throws IOException {
        Gson gson = new Gson();
        String msg = gson.toJson(notification);
        session.getRemote().sendString(msg);
    }

    public void broadcast(Session excludeSession, ServerMessage notification, String message) throws IOException {
        Gson gson = new Gson();
        String msg = gson.toJson(notification);
        for (Session c : connections.values()) {
            if (c.isOpen()) {
                if(!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}
