package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.ArrayList;
import java.util.Collection;

public class AccessGame implements GameAccess {
    private final Collection<GameData> games = new ArrayList<>();

    public void updateGame(GameData game) throws DataAccessException{}

    public int createGame(GameData game) throws DataAccessException{
        games.add(game);
        return game.gameID();
    }

    public GameData getGame(int gameID) throws DataAccessException{
        for(GameData x: games){
            if (x.gameID() == gameID){
                return x;
            }
        }
        throw new DataAccessException("Error: bad request", 400);
    }

    public void deleteGame(GameData game){
        games.remove(game);
    }

    public Collection<GameData> listGame() throws DataAccessException{
        return games;
    }

    public void clearGames(){
        games.clear();
    }
}
