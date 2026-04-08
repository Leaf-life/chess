package websocket.messages;

public class NotificationMessage extends ServerMessage {

    NotificationMessage notificationMessage;

    public NotificationMessage(ServerMessageType type, NotificationMessage notificationMessage) {
        super(type);
        this.notificationMessage = notificationMessage;
    }

    public NotificationMessage getMessage() {
        return notificationMessage;
    }
}
