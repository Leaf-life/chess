package websocket.messages;

public class LoadGameMessage extends ServerMessage{

    private final int game;

    public LoadGameMessage(ServerMessageType type, int game) {
        super(type);
        this.game = game;
    }

    public int getGame(){
        return game;
    }
}
