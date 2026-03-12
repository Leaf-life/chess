package dataaccess;

import model.*;

import java.util.Collection;

public interface GameAccess {

    int createGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void deleteGame(GameData game);

    <T> Collection<T> listGame() throws DataAccessException;

    void clearGames();
}
