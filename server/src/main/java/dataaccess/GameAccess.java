package dataaccess;

import model.*;

import java.util.Collection;

public interface GameAccess {

    void updateGame(GameData game) throws DataAccessException;

    int createGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void deleteGame(GameData game);

    <T> Collection<T> listGame() throws DataAccessException;

    void clearGames();
}
