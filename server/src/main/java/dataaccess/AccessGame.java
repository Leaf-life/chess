package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.ArrayList;
import java.util.Collection;

public class AccessGame implements GameAccess {
    private final Collection<GameData> games = new ArrayList<>();

    public void createGame(GameData game) throws DataAccessException{
        games.add(game);
    }

    public GameData getGame(int gameID) throws DataAccessException{
        for(GameData x: games){
            if (x.gameID() == gameID){
                return x;
            }
        }
        throw new DataAccessException("Error: bad request", 400);
    }

    public Collection<GameData> listGame() throws DataAccessException{
        return games;
    }

    public void clearGames(){
        games.clear();
    }
}
