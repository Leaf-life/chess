package websocket;

import chess.ChessGame;

public class LoadGameMessage {
    ChessGame game;

    public LoadGameMessage(ChessGame game){
        this.game = game;
    }

    public ChessGame getGame(){
        return game;
    }
}
