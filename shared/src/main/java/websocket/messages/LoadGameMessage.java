package websocket.messages;

public class LoadGameMessage extends ServerMessage{

    LoadGameMessage gameMessage;

    public LoadGameMessage(ServerMessageType type, LoadGameMessage gameMessage) {
        super(type);
        this.gameMessage = gameMessage;
    }

    public LoadGameMessage getGame(){
        return gameMessage;
    }
}
