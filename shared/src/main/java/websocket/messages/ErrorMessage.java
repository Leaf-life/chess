package websocket.messages;

public class ErrorMessage extends ServerMessage{

    ErrorMessage errorMessage;

    public ErrorMessage(ServerMessageType type, ErrorMessage errorMessage) {
        super(type);
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage(){
        return errorMessage;
    }
}
