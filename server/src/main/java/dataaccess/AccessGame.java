package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.ArrayList;
import java.util.Collection;

public class AccessGame {
    private final Collection<GameData> games = new ArrayList<>();
    AccessGame(){}

    public void createGame(String gameName){
        games.add(new GameData(1, null, null, gameName, new ChessGame()));
    }

    public GameData getGame(int gameID){
        for(GameData x: games){
            if (x.gameID() == gameID){
                return x;
            }
        }
        return null;
    }

    public Collection<GameData> listGame(){
        return games;
    }
}
